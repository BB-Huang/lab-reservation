package asia.bbtlry.lab.controller;

import asia.bbtlry.lab.common.Result;
import asia.bbtlry.lab.dto.CategoryRequest;
import asia.bbtlry.lab.entity.DeviceCategory;
import asia.bbtlry.lab.mapper.DeviceCategoryMapper;
import asia.bbtlry.lab.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final DeviceCategoryMapper mapper;
    private final CategoryService categoryService;

    public CategoryController(DeviceCategoryMapper mapper, CategoryService categoryService) {
        this.mapper = mapper;
        this.categoryService = categoryService;
    }

    @GetMapping
    public Result<List<DeviceCategory>> list() {
        return Result.success(mapper.selectList(null));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Long> create(@RequestBody @Valid CategoryRequest request) {
        return Result.success(categoryService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> update(@PathVariable Long id,
                               @RequestBody @Valid CategoryRequest request) {
        categoryService.update(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}