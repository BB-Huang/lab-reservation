package asia.bbtlry.lab.controller;

import asia.bbtlry.lab.common.Result;
import asia.bbtlry.lab.dto.DeviceCreateRequest;
import asia.bbtlry.lab.entity.Device;
import asia.bbtlry.lab.service.DeviceService;
import asia.bbtlry.lab.vo.PageResult;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
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
}