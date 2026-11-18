package asia.bbtlry.lab.service;

import asia.bbtlry.lab.common.ErrorCode;
import asia.bbtlry.lab.dto.DeviceCreateRequest;
import asia.bbtlry.lab.entity.Device;
import asia.bbtlry.lab.exception.BusinessException;
import asia.bbtlry.lab.mapper.DeviceCategoryMapper;
import asia.bbtlry.lab.mapper.DeviceMapper;
import asia.bbtlry.lab.vo.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    public PageResult<Device> page(int page, int size, Long categoryId, String keyword) {
        if (page < 1 || size < 1 || size > 100) {
            throw new BusinessException(ErrorCode.INVALID_PARAM);
        }

        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null, Device::getCategoryId, categoryId)
                .and(StringUtils.hasText(keyword),
                        w -> w.like(Device::getName, keyword)
                                .or().like(Device::getModel, keyword))
                .orderByDesc(Device::getCreatedAt);

        Page<Device> result = deviceMapper.selectPage(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
}