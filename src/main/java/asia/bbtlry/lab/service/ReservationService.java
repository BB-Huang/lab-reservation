package asia.bbtlry.lab.service;

import asia.bbtlry.lab.common.ErrorCode;
import asia.bbtlry.lab.dto.CreateReservationRequest;
import asia.bbtlry.lab.entity.Device;
import asia.bbtlry.lab.entity.Reservation;
import asia.bbtlry.lab.exception.BusinessException;
import asia.bbtlry.lab.mapper.DeviceMapper;
import asia.bbtlry.lab.mapper.ReservationMapper;
import asia.bbtlry.lab.vo.PageResult;
import asia.bbtlry.lab.vo.ReservationVO;
import asia.bbtlry.lab.vo.TimeSlotVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ReservationService {

    private final ReservationMapper reservationMapper;
    private final DeviceMapper deviceMapper;
    private final RedissonClient redissonClient;

    public ReservationService(ReservationMapper reservationMapper, DeviceMapper deviceMapper, RedissonClient redissonClient) {
        this.reservationMapper = reservationMapper;
        this.deviceMapper = deviceMapper;
        this.redissonClient = redissonClient;
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
        RLock lock = redissonClient.getLock("lock:device:" + request.getDeviceId());
        boolean locked = false;
        try {
            locked = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException(ErrorCode.LOCK_BUSY);
            }

            Long conflicts = reservationMapper.selectCount(
                    new LambdaQueryWrapper<Reservation>()
                            .eq(Reservation::getDeviceId, request.getDeviceId())
                            .in(Reservation::getStatus, "PENDING", "APPROVED")
                            .lt(Reservation::getStartTime, request.getEndTime())
                            .gt(Reservation::getEndTime, request.getStartTime()));
            if (conflicts > 0) {
                throw new BusinessException(ErrorCode.CONFLICT);
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.LOCK_BUSY);
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
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

    public PageResult<ReservationVO> myReservations(Long userId, int page, int size, String status) {
        if (page < 1 || size < 1 || size > 100) {
            throw new BusinessException(ErrorCode.INVALID_PARAM);
        }

        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getUserId, userId)
                .eq(StringUtils.hasText(status), Reservation::getStatus, status)
                .orderByDesc(Reservation::getCreatedAt);

        Page<Reservation> result = reservationMapper.selectPage(new Page<>(page, size), wrapper);

        List<ReservationVO> records = result.getRecords().stream()
                .map(this::toVO)
                .toList();

        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    public PageResult<ReservationVO> pending(int page, int size) {
        if (page < 1 || size < 1 || size > 100) {
            throw new BusinessException(ErrorCode.INVALID_PARAM);
        }

        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getStatus, "PENDING")
                .orderByAsc(Reservation::getStartTime);

        Page<Reservation> result = reservationMapper.selectPage(new Page<>(page, size), wrapper);
        List<ReservationVO> records = result.getRecords().stream()
                .map(this::toVO)
                .toList();

        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    private ReservationVO toVO(Reservation r) {
        return new ReservationVO(r.getId(), r.getDeviceId(),
                r.getStartTime(), r.getEndTime(), r.getPurpose(),
                r.getStatus(), r.getReviewComment(), r.getCreatedAt());
    }

    public void cancel(Long id, Long userId) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        if (!"PENDING".equals(reservation.getStatus())
                && !"APPROVED".equals(reservation.getStatus())) {
            throw new BusinessException(ErrorCode.RESERVATION_STATUS_INVALID);
        }

        reservation.setStatus("CANCELLED");
        reservationMapper.updateById(reservation);
    }

    private Reservation getPendingReservation(Long id) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        if (!"PENDING".equals(reservation.getStatus())) {
            throw new BusinessException(ErrorCode.RESERVATION_STATUS_INVALID);
        }
        return reservation;
    }

    public void approve(Long id, Long reviewerId) {
        Reservation reservation = getPendingReservation(id);
        reservation.setStatus("APPROVED");
        reservation.setReviewerId(reviewerId);
        reservationMapper.updateById(reservation);
    }

    public void reject(Long id, Long reviewerId, String comment) {
        Reservation reservation = getPendingReservation(id);
        reservation.setStatus("REJECTED");
        reservation.setReviewerId(reviewerId);
        reservation.setReviewComment(comment);
        reservationMapper.updateById(reservation);
    }
}