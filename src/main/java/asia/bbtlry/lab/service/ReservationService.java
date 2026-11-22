package asia.bbtlry.lab.service;

import asia.bbtlry.lab.common.ErrorCode;
import asia.bbtlry.lab.dto.CreateReservationRequest;
import asia.bbtlry.lab.entity.Device;
import asia.bbtlry.lab.entity.Reservation;
import asia.bbtlry.lab.exception.BusinessException;
import asia.bbtlry.lab.mapper.DeviceMapper;
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
    private final DeviceMapper deviceMapper;

    public ReservationService(ReservationMapper reservationMapper, DeviceMapper deviceMapper) {
        this.reservationMapper = reservationMapper;
        this.deviceMapper = deviceMapper;
    }

    public Long create(CreateReservationRequest request, Long userId) {
        Device device = deviceMapper.selectById(request.getDeviceId());
        if (device == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        if (!"AVAILABLE".equals(device.getStatus())) {
            throw new BusinessException(ErrorCode.DEVICE_UNAVAILABLE);
        }
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new BusinessException(ErrorCode.INVALID_PARAM);
        }
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.INVALID_PARAM);
        }

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setDeviceId(request.getDeviceId());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setPurpose(request.getPurpose());
        reservation.setStatus("PENDING");

        reservationMapper.insert(reservation);
        return reservation.getId();
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