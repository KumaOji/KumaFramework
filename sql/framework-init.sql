-- =============================================================
-- kuma-boot-framework + kuma-cloud-starter-* 框架初始化SQL
-- 说明：这些表是框架功能表，添加到业务服务的主数据库中即可
-- 来源模块：
--   kuma-boot-starter-idempotent  → business_idempotent
--   kuma-boot-starter-data-mybatis → app_delay_message
--   kuma-cloud-starter-seata       → undo_log (每个参与分布式事务的库都需要)
-- =============================================================

-- 如果使用统一数据库，先建库（按实际情况修改库名）
CREATE DATABASE IF NOT EXISTS `kuma-framework` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `kuma-framework`;

-- =============================================================
-- 1. 幂等控制表
-- 模块：kuma-boot-starter-idempotent
-- 说明：防止接口重复请求，基于唯一键实现幂等
-- =============================================================
CREATE TABLE IF NOT EXISTS `business_idempotent` (
    `idempotent_id`        bigint       NOT NULL AUTO_INCREMENT COMMENT '幂等主键',
    `namespace`            varchar(32)           DEFAULT NULL COMMENT '命名空间',
    `source`               varchar(64)           DEFAULT NULL COMMENT '来源',
    `operation_type`       varchar(64)           DEFAULT NULL COMMENT '操作类型',
    `business_key`         varchar(128) NOT NULL COMMENT '业务key',
    `unique_key`           varchar(255) NOT NULL COMMENT '唯一键（用于幂等控制）',
    `idempotent_status`    tinyint      NOT NULL COMMENT '幂等状态(1=处理中 2=处理成功)',
    `object_version_number` bigint      NOT NULL COMMENT '版本号',
    `response`             blob                  DEFAULT NULL COMMENT '响应数据',
    `create_date`          datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `last_modified_date`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近修改时间',
    PRIMARY KEY (`idempotent_id`),
    UNIQUE KEY `udx_unique_key` (`unique_key`) COMMENT '幂等键唯一索引'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '业务幂等表';

-- =============================================================
-- 2. 延迟消息表（基于DB实现的延迟队列）
-- 模块：kuma-boot-starter-data-mybatis
-- 说明：利用数据库轮询实现延迟消息，不依赖Redis/MQ
-- =============================================================
CREATE TABLE IF NOT EXISTS `app_delay_message` (
    `id`          varchar(64)  NOT NULL COMMENT '消息ID',
    `app_id`      varchar(64)           DEFAULT NULL COMMENT '应用ID',
    `ttl`         int                   DEFAULT NULL COMMENT '超时时长(小时)',
    `type`        varchar(32)           DEFAULT NULL COMMENT '场景类型(TYPE1/TYPE2)',
    `stage`       varchar(32)           DEFAULT NULL COMMENT '阶段(REAL_COMPILE)',
    `status`      varchar(32)           DEFAULT NULL COMMENT '状态(PENDING/PROCESSED/TIMEOUT/INVALID)',
    `remark`      varchar(512)          DEFAULT NULL COMMENT '备考',
    `callback`    varchar(256)          DEFAULT NULL COMMENT '回调函数',
    `create_time` datetime              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_time` datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `delete_flg`  varchar(8)            DEFAULT '0' COMMENT '删除标(0=未删除 1=已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_status_create` (`status`, `create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '延迟消息表（DB延迟队列）';

-- =============================================================
-- 3. Seata AT模式回滚日志表
-- 模块：kuma-cloud-starter-seata
-- 说明：每个参与分布式事务的业务库都需要建此表
--       如果多个服务共用一个库，只需建一次
-- =============================================================
CREATE TABLE IF NOT EXISTS `undo_log` (
    `branch_id`     bigint       NOT NULL COMMENT '分支事务ID',
    `xid`           varchar(128) NOT NULL COMMENT '全局事务ID',
    `context`       varchar(128) NOT NULL COMMENT '上下文',
    `rollback_info` longblob     NOT NULL COMMENT '回滚信息',
    `log_status`    int          NOT NULL COMMENT '状态(0=正常 1=全局已完成)',
    `log_created`   datetime(6)  NOT NULL COMMENT '创建时间',
    `log_modified`  datetime(6)  NOT NULL COMMENT '修改时间',
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = 'Seata AT模式回滚日志表';
