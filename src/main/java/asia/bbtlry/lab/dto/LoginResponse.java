package asia.bbtlry.lab.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Data
public class LoginResponse {
    private String token;
    private String username;
    private String role;
}
