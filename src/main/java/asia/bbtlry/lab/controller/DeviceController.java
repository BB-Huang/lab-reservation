package asia.bbtlry.lab.controller;

import asia.bbtlry.lab.common.Result;
import asia.bbtlry.lab.dto.DeviceCreateRequest;
import asia.bbtlry.lab.entity.Device;
import asia.bbtlry.lab.mapper.DeviceMapper;
import asia.bbtlry.lab.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceMapper deviceMapper;
    private final DeviceService deviceService;

    public DeviceController(DeviceMapper deviceMapper, DeviceService deviceService) {
        this.deviceMapper = deviceMapper;
        this.deviceService = deviceService;
    }

    @GetMapping
    public Result<List<Device>> list() {
        return Result.success(deviceMapper.selectList(null));
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