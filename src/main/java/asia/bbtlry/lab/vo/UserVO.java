package asia.bbtlry.lab.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserVO {
    private Long id;
    private String username;
    private String realName;
    private String role;
}