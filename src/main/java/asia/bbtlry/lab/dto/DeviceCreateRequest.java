package asia.bbtlry.lab.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeviceCreateRequest {

    @NotBlank(message = "设备名不能为空")
    @Size(max = 100, message = "设备名不能超过 100 个字符")
    private String name;

    @Size(max = 100, message = "型号不能超过 100 个字符")
    private String model;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotBlank(message = "实验室不能为空")
    @Size(max = 50, message = "实验室不能超过 50 个字符")
    private String labRoom;

    @Size(max = 255, message = "描述不能超过 255 个字符")
    private String description;
}
