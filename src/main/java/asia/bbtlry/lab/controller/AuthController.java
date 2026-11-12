package asia.bbtlry.lab.controller;

import asia.bbtlry.lab.common.Result;
import asia.bbtlry.lab.dto.RegisterRequest;
import asia.bbtlry.lab.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<Long> register(@RequestBody @Valid RegisterRequest request)
    {
        return Result.success(authService.register(request));
    }
}