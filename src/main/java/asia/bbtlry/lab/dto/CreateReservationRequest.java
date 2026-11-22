package asia.bbtlry.lab.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateReservationRequest {

    @NotNull(message = "设备不能为空")
    private Long deviceId;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @Size(max = 255, message = "用途不能超过 255 个字符")
    private String purpose;
}
