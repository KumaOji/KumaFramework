package com.kuma.cloud.blog.config;

import com.kuma.boot.security.spring.utils.SecurityUtils;
import com.kuma.cloud.blog.domain.entity.User;
import com.kuma.cloud.blog.mapper.UserMapper;
import com.kuma.cloud.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 启动时重置 admin 密码为 admin123，解决 SQL 初始化脚本中的哈希与当前 BCrypt 不匹配的问题。
 * 仅在 blog.reset-admin-password=true 时执行。
 */
@Component
@Order(Integer.MIN_VALUE)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "blog.reset-admin-password", havingValue = "true")
public class AdminPasswordInitRunner implements ApplicationRunner {

    private static final String ADMIN_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin123";

    private final UserMapper userMapper;

    @Override
    public void run(ApplicationArguments args) {
        User admin = userMapper.selectByUsername(ADMIN_USERNAME);
        if (admin == null) {
            return;
        }
        String encoded = SecurityUtils.getPasswordEncoder().encode(DEFAULT_PASSWORD);
        User update = new User();
        update.setId(admin.getId());
        update.setPassword(encoded);
        userMapper.updateById(update);
    }
}
