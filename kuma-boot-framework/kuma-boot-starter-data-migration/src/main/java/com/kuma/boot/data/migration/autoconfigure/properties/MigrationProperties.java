package com.kuma.boot.data.migration.autoconfigure.properties;

import com.kuma.boot.data.migration.enums.MigrationType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 数据库版本管理统一配置。
 *
 * <p>本模块在 Spring Boot 原生 Flyway / Liquibase 自动装配之上，提供一个统一开关
 * 与引擎选择能力：{@link #enabled} 为总开关，{@link #type} 决定启用哪种引擎。
 * 引擎本身的细粒度参数仍沿用 Spring Boot 原生命名空间
 * （{@code spring.flyway.*} / {@code spring.liquibase.*}）。</p>
 *
 * @author kuma
 * @since 2026-06-09
 */
@RefreshScope
@ConfigurationProperties(prefix = MigrationProperties.PREFIX)
public class MigrationProperties {

    public static final String PREFIX = "kuma.boot.data.migration";

    /**
     * 数据库版本管理总开关。关闭时同时禁用 Flyway 与 Liquibase。默认开启。
     */
    private boolean enabled = true;

    /**
     * 迁移引擎选择，默认 {@link MigrationType#AUTO}。
     */
    private MigrationType type = MigrationType.AUTO;

    /**
     * 是否在启动时打印本模块启动横幅日志。默认开启。
     */
    private boolean banner = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public MigrationType getType() {
        return type;
    }

    public void setType(MigrationType type) {
        this.type = type;
    }

    public boolean isBanner() {
        return banner;
    }

    public void setBanner(boolean banner) {
        this.banner = banner;
    }
}
