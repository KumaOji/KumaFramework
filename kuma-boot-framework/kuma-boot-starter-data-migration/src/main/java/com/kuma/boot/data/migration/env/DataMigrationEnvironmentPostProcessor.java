package com.kuma.boot.data.migration.env;

import com.kuma.boot.data.migration.autoconfigure.properties.MigrationProperties;
import com.kuma.boot.data.migration.enums.MigrationType;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * 将统一的 {@code kuma.boot.data.migration.*} 配置翻译为 Spring Boot 原生的
 * {@code spring.flyway.enabled} / {@code spring.liquibase.enabled}，从而用一个开关
 * 控制两套迁移引擎。
 *
 * <p>翻译规则：</p>
 * <ul>
 *     <li>{@code enabled=false}：同时关闭 Flyway 与 Liquibase；</li>
 *     <li>{@code type=FLYWAY}：关闭 Liquibase；</li>
 *     <li>{@code type=LIQUIBASE}：关闭 Flyway；</li>
 *     <li>{@code type=AUTO}：不干预，沿用 Spring Boot 原生条件。</li>
 * </ul>
 *
 * <p>派生的属性以最低优先级（{@code addLast}）加入环境，因此用户显式配置的
 * {@code spring.flyway.enabled} / {@code spring.liquibase.enabled} 始终优先生效。</p>
 *
 * @author kuma
 * @since 2026-06-09
 */
public class DataMigrationEnvironmentPostProcessor implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "kumaDataMigration";

    private static final String FLYWAY_ENABLED = "spring.flyway.enabled";

    private static final String LIQUIBASE_ENABLED = "spring.liquibase.enabled";

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        MigrationProperties properties = Binder.get(environment)
                .bind(MigrationProperties.PREFIX, MigrationProperties.class)
                .orElseGet(MigrationProperties::new);

        Map<String, Object> derived = new HashMap<>(2);
        if (!properties.isEnabled()) {
            derived.put(FLYWAY_ENABLED, "false");
            derived.put(LIQUIBASE_ENABLED, "false");
        } else if (properties.getType() == MigrationType.FLYWAY) {
            derived.put(LIQUIBASE_ENABLED, "false");
        } else if (properties.getType() == MigrationType.LIQUIBASE) {
            derived.put(FLYWAY_ENABLED, "false");
        }

        if (!derived.isEmpty()) {
            environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, derived));
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
