package asia.bbtlry.lab.service;

import asia.bbtlry.lab.common.ErrorCode;
import asia.bbtlry.lab.dto.LoginRequest;
import asia.bbtlry.lab.dto.LoginResponse;
import asia.bbtlry.lab.dto.RegisterRequest;
import asia.bbtlry.lab.entity.User;
import asia.bbtlry.lab.exception.BusinessException;
import asia.bbtlry.lab.mapper.UserMapper;
import asia.bbtlry.lab.util.JwtUtil;
import asia.bbtlry.lab.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil)
    {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Long register(RegisterRequest request) {
        // 1. 查重
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername,
                        request.getUsername()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }

        // 2. DTO → entity，密码加密
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());

        // 3. 角色和状态不信任客户端，服务端写死
        user.setRole("STUDENT");
        user.setEnabled(1);

        // 4. 落库，id 会被回填到 user 对象
        userMapper.insert(user);
        return user.getId();
    }

    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername,
                        request.getUsername()));

        if (user == null || !passwordEncoder.matches(request.getPassword(),
                user.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }
        if (user.getEnabled() == null || user.getEnabled() != 1) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }

        String token = jwtUtil.generateToken(user);
        return new LoginResponse(token, user.getUsername(), user.getRole());
    }

    public UserVO getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return new UserVO(user.getId(), user.getUsername(), user.getRealName(), user.getRole());
    }
}