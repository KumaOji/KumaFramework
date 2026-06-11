package com.kuma.boot.data.migration.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.migration.autoconfigure.properties.MigrationProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 数据库版本管理自动装配。
 *
 * <p>实际的迁移执行仍由 Spring Boot 原生的 Flyway / Liquibase 自动装配完成，本类负责：</p>
 * <ol>
 *     <li>绑定统一配置 {@link MigrationProperties}（{@code kuma.boot.data.migration.*}）；</li>
 *     <li>在启动时打印模块横幅，标识当前选择的迁移引擎。</li>
 * </ol>
 *
 * <p>引擎间的互斥（Flyway / Liquibase）由
 * {@link com.kuma.boot.data.migration.env.DataMigrationEnvironmentPostProcessor} 在更早阶段完成。</p>
 *
 * @author kuma
 * @since 2026-06-09
 */
@AutoConfiguration
@EnableConfigurationProperties(MigrationProperties.class)
@ConditionalOnProperty(
        prefix = MigrationProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class DataMigrationAutoConfiguration implements InitializingBean {

    private final MigrationProperties properties;

    public DataMigrationAutoConfiguration(MigrationProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
        if (!properties.isBanner()) {
            return;
        }
        String engine = switch (properties.getType()) {
            case FLYWAY -> "flyway";
            case LIQUIBASE -> "liquibase";
            case AUTO -> "auto(flyway|liquibase)";
        };
        LogUtils.started(DataMigrationAutoConfiguration.class, "kuma-boot-starter-data-migration", engine);
    }
}
