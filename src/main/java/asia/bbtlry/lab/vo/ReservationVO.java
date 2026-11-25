package asia.bbtlry.lab.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReservationVO {
    private Long id;
    private Long deviceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose;
    private String status;
    private String reviewComment;
    private LocalDateTime createdAt;
}