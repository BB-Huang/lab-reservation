package asia.bbtlry.lab.controller;

import asia.bbtlry.lab.common.Result;
import asia.bbtlry.lab.entity.Device;
import asia.bbtlry.lab.mapper.DeviceMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceMapper deviceMapper;

    public DeviceController(DeviceMapper deviceMapper) {
        this.deviceMapper = deviceMapper;
    }

    @GetMapping
    public Result<List<Device>> list() {
        return Result.success(deviceMapper.selectList(null));
    }
}