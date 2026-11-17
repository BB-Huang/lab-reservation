package asia.bbtlry.lab.service;

import asia.bbtlry.lab.common.ErrorCode;
import asia.bbtlry.lab.dto.DeviceCreateRequest;
import asia.bbtlry.lab.entity.Device;
import asia.bbtlry.lab.exception.BusinessException;
import asia.bbtlry.lab.mapper.DeviceCategoryMapper;
import asia.bbtlry.lab.mapper.DeviceMapper;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {

    private final DeviceMapper deviceMapper;
    private final DeviceCategoryMapper categoryMapper;

    public DeviceService(DeviceMapper deviceMapper,
                         DeviceCategoryMapper categoryMapper) {
        this.deviceMapper = deviceMapper;
        this.categoryMapper = categoryMapper;
    }

    public Long create(DeviceCreateRequest request) {
        if (categoryMapper.selectById(request.getCategoryId()) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        Device device = new Device();
        device.setName(request.getName());
        device.setModel(request.getModel());
        device.setCategoryId(request.getCategoryId());
        device.setLabRoom(request.getLabRoom());
        device.setDescription(request.getDescription());
        device.setStatus("AVAILABLE");

        deviceMapper.insert(device);
        return device.getId();
    }

    public Device getById(Long id) {
        Device device = deviceMapper.selectById(id);
        if (device == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        return device;
    }
}