package asia.bbtlry.lab.controller;

import asia.bbtlry.lab.common.Result;
import asia.bbtlry.lab.dto.DeviceCreateRequest;
import asia.bbtlry.lab.dto.DeviceUpdateRequest;
import asia.bbtlry.lab.entity.Device;
import asia.bbtlry.lab.service.DeviceService;
import asia.bbtlry.lab.service.ReservationService;
import asia.bbtlry.lab.vo.PageResult;
import asia.bbtlry.lab.vo.TimeSlotVO;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    private final DeviceService deviceService;
    private final ReservationService reservationService;

    public DeviceController(DeviceService deviceService, ReservationService reservationService) {
        this.deviceService = deviceService;
        this.reservationService = reservationService;
    }

    @GetMapping
    public Result<PageResult<Device>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        return Result.success(deviceService.page(page, size, categoryId, keyword));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Long> create(@RequestBody @Valid DeviceCreateRequest request) {
        return Result.success(deviceService.create(request));
    }

    @GetMapping("/{id}")
    public Result<Device> detail(@PathVariable Long id) {
        return Result.success(deviceService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid DeviceUpdateRequest request) {
        deviceService.update(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        deviceService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}/availability")
    public Result<List<TimeSlotVO>> availability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        deviceService.getById(id);   // 设备不存在 → 40401
        return Result.success(reservationService.occupiedSlots(id, date));
    }
}