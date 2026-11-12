package asia.bbtlry.lab.service;

import asia.bbtlry.lab.common.ErrorCode;
import asia.bbtlry.lab.dto.RegisterRequest;
import asia.bbtlry.lab.entity.User;
import asia.bbtlry.lab.exception.BusinessException;
import asia.bbtlry.lab.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder)
    {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
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
}