package asia.bbtlry.lab.service;

import asia.bbtlry.lab.common.ErrorCode;
import asia.bbtlry.lab.dto.CategoryRequest;
import asia.bbtlry.lab.entity.DeviceCategory;
import asia.bbtlry.lab.exception.BusinessException;
import asia.bbtlry.lab.mapper.DeviceCategoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final DeviceCategoryMapper categoryMapper;

    public CategoryService(DeviceCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public Long create(CategoryRequest request) {
        Long count = categoryMapper.selectCount(
                new LambdaQueryWrapper<DeviceCategory>()
                        .eq(DeviceCategory::getName, request.getName()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.CATEGORY_EXISTS);
        }

        DeviceCategory category = new DeviceCategory();
        category.setName(request.getName());
        category.setSort(request.getSort() == null ? 0 : request.getSort());
        categoryMapper.insert(category);
        return category.getId();
    }

    public void update(Long id, CategoryRequest request) {
        DeviceCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        Long count = categoryMapper.selectCount(
                new LambdaQueryWrapper<DeviceCategory>()
                        .eq(DeviceCategory::getName, request.getName())
                        .ne(DeviceCategory::getId, id));
        if (count > 0) {
            throw new BusinessException(ErrorCode.CATEGORY_EXISTS);
        }

        category.setName(request.getName());
        category.setSort(request.getSort() == null ? 0 : request.getSort());
        categoryMapper.updateById(category);
    }

    public void delete(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        categoryMapper.deleteById(id);
    }
}
