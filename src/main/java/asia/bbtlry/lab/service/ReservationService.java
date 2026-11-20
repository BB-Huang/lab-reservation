package asia.bbtlry.lab.service;

import asia.bbtlry.lab.entity.Reservation;
import asia.bbtlry.lab.mapper.ReservationMapper;
import asia.bbtlry.lab.vo.TimeSlotVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationMapper reservationMapper;

    public ReservationService(ReservationMapper reservationMapper) {
        this.reservationMapper = reservationMapper;
    }

    public List<TimeSlotVO> occupiedSlots(Long deviceId, LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);

        List<Reservation> list = reservationMapper.selectList(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getDeviceId, deviceId)
                        .in(Reservation::getStatus, "PENDING", "APPROVED")
                        .lt(Reservation::getStartTime, dayEnd)
                        .gt(Reservation::getEndTime, dayStart)
                        .orderByAsc(Reservation::getStartTime));

        return list.stream()
                .map(r -> new TimeSlotVO(r.getStartTime(), r.getEndTime()))
                .toList();
    }
}
