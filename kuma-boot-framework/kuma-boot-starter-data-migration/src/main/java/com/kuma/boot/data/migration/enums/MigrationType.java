package com.kuma.boot.data.migration.enums;

/**
 * 数据库版本管理引擎类型。
 *
 * <p>用于 {@code kuma.boot.data.migration.type} 配置项，决定启用哪一种迁移引擎。</p>
 *
 * @author kuma
 * @since 2026-06-09
 */
public enum MigrationType {

    /**
     * 自动模式：Flyway 与 Liquibase 均交由 Spring Boot 原生条件判断，
     * 谁存在迁移脚本/变更日志谁生效（默认）。
     */
    AUTO,

    /**
     * 仅启用 Flyway，自动关闭 Liquibase（{@code spring.liquibase.enabled=false}）。
     */
    FLYWAY,

    /**
     * 仅启用 Liquibase，自动关闭 Flyway（{@code spring.flyway.enabled=false}）。
     */
    LIQUIBASE
}
