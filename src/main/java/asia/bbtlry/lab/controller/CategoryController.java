package asia.bbtlry.lab.controller;

import asia.bbtlry.lab.entity.DeviceCategory;
import asia.bbtlry.lab.mapper.DeviceCategoryMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final DeviceCategoryMapper mapper;

    public CategoryController(DeviceCategoryMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping
    public List<DeviceCategory> list() {
        return mapper.selectList(null);
    }
}