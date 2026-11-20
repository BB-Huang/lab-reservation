package asia.bbtlry.lab.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TimeSlotVO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
