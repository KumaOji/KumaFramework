package com.kuma.cloud.blog.config;

import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.annotation.DataPermission;
import com.kuma.boot.security.spring.utils.SecurityUtils;
import com.kuma.cloud.blog.domain.entity.User;
import com.kuma.cloud.blog.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 启动时重置 admin 密码，解决 SQL 初始化脚本中的哈希与当前 BCrypt 不匹配的问题。
 * 仅在 blog.reset-admin-password=true 时执行。
 * 密码通过配置读取，建议从环境变量 ADMIN_INITIAL_PASSWORD 获取。
 */
@Slf4j
@Component
@Order(Integer.MIN_VALUE)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "blog.reset-admin-password", havingValue = "true")
public class AdminPasswordInitRunner implements ApplicationRunner {

    private static final String ADMIN_USERNAME = "admin";

    @Value("${blog.admin-initial-password:admin123}")
    private String adminInitialPassword;

    private final UserMapper userMapper;

    @Override
    @DataPermission(enable = false)
    public void run(ApplicationArguments args) {
        User admin = userMapper.selectByUsername(ADMIN_USERNAME);
        if (admin == null) {
            log.warn("Admin user '{}' not found, skipping password initialization", ADMIN_USERNAME);
            return;
        }
        String encoded = SecurityUtils.getPasswordEncoder().encode(adminInitialPassword);
        User update = new User();
        update.setId(admin.getId());
        update.setPassword(encoded);
        userMapper.updateById(update);
        log.info("Admin password reinitialized for user '{}'", ADMIN_USERNAME);
    }
}
