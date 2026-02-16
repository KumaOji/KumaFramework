package cn.kuma.blog.main.service.impl;

import cn.kuma.blog.framework.mybatisplus.util.SchemaContext;
import cn.kuma.blog.main.domain.entity.User;
import cn.kuma.blog.main.mapper.UserMapper;
import cn.kuma.blog.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类（密码仅使用 BCrypt）
 *
 * @author Kuma
 * @version 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    private static final String SCHEMA_NAME = "blog_source";

    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getByUsername(String username) {
        final User[] result = new User[1];
        SchemaContext.withSchema(SCHEMA_NAME, () -> {
            result[0] = userMapper.selectByUsername(username);
        });
        return result[0];
    }

    @Override
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        if (encodedPassword == null) {
            return false;
        }
        return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
    }

    @Override
    public String encodePassword(String rawPassword) {
        return PASSWORD_ENCODER.encode(rawPassword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(User user) {
        final Long[] result = new Long[1];
        SchemaContext.withSchema(SCHEMA_NAME, () -> {
            LocalDateTime now = LocalDateTime.now();
            user.setCreateTime(now);
            user.setUpdateTime(now);

            // 加密密码
            if (user.getPassword() != null) {
                user.setPassword(encodePassword(user.getPassword()));
            }

            // 默认状态为启用
            if (user.getStatus() == null) {
                user.setStatus(1);
            }

            // 默认非管理员
            if (user.getIsAdmin() == null) {
                user.setIsAdmin(0);
            }

            userMapper.insert(user);
            result[0] = user.getId();
        });
        return result[0];
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginTime(Long userId) {
        SchemaContext.withSchema(SCHEMA_NAME, () -> {
            User user = new User();
            user.setId(userId);
            user.setLastLoginTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
        });
    }
}
