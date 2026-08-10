package com.kuma.cloud.blog.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 启动时加载友链默认数据（classpath:sql/friend_link_data.sql）。
 * 脚本按 url 幂等插入，可重复启动。
 */
@Slf4j
@Component
@Order(100)
@RequiredArgsConstructor
public class FriendLinkDataInitializer implements ApplicationRunner {

    private static final String SCRIPT = "sql/friend_link_data.sql";

    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) {
        Resource resource = new ClassPathResource(SCRIPT);
        if (!resource.exists()) {
            log.debug("Skip friend-link seed: {} not found", SCRIPT);
            return;
        }
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setSqlScriptEncoding("UTF-8");
        populator.setContinueOnError(false);
        populator.addScript(resource);
        try {
            populator.execute(dataSource);
            log.info("Friend-link default data loaded from {}", SCRIPT);
        } catch (Exception e) {
            log.warn("Friend-link default data init failed: {}", e.getMessage());
        }
    }
}
