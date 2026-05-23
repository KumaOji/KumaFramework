-- =============================================================
-- kuma-boot-framework + kuma-cloud-starter-* 框架初始化SQL
-- 说明：这些表是框架功能表，添加到业务服务的主数据库中即可
-- 来源模块：
--   kuma-boot-starter-idempotent      → business_idempotent
--   kuma-boot-starter-data-mybatis    → app_delay_message
--   kuma-cloud-starter-seata          → undo_log (每个参与分布式事务的库都需要)
--   kuma-boot-starter-ddd             → kmc_domain_event
--   kuma-boot-starter-data-jpa        → sys_tenant_datasource (多租户数据源)
--   kuma-boot-starter-security-spring → auth_token / user_connection (JustAuth)
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

-- =============================================================
-- 4. DDD 领域事件存储表
-- 模块：kuma-boot-starter-ddd
-- 说明：聚合根领域事件持久化，配合事件溯源 / MQ 异步发布
--       由 DomainEventServiceImpl 写入，无自动建表机制
-- =============================================================
CREATE TABLE IF NOT EXISTS `kmc_domain_event` (
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `event_type`    varchar(128)          DEFAULT NULL COMMENT '事件类型（领域事件类全限定名或简称）',
    `source_name`   varchar(128)          DEFAULT NULL COMMENT '事件来源（聚合根名称）',
    `topic`         varchar(128)          DEFAULT NULL COMMENT 'MQ 主题（topic）',
    `tag`           varchar(64)           DEFAULT NULL COMMENT 'MQ 标签（tag）',
    `service_id`    varchar(64)           DEFAULT NULL COMMENT '所属服务标识（spring.application.name）',
    `aggregate_id`  bigint                DEFAULT NULL COMMENT '聚合根 ID',
    `attribute`     text                  DEFAULT NULL COMMENT '事件载荷（JSON）',
    `create_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_service_create` (`service_id`, `create_time`) COMMENT '按 serviceId+时间清理过期事件用'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'DDD 领域事件表';

-- =============================================================
-- 5. JustAuth 第三方登录 Token 表
-- 模块：kuma-boot-starter-security-spring
-- 说明：缓存第三方平台返回的 accessToken/refreshToken
--       默认 ums.repository.enable-start-up-initialize-table=true 会自动建表
--       如需关闭自启动建表，可使用本 DDL 手动初始化
-- =============================================================
CREATE TABLE IF NOT EXISTS `auth_token` (
    `id`                       bigint        NOT NULL AUTO_INCREMENT,
    `enableRefresh`            tinyint(1)    NOT NULL DEFAULT '1' COMMENT '是否支持 refreshToken (1=支持 0=不支持)',
    `providerId`               varchar(20)            DEFAULT NULL COMMENT '第三方服务商：qq/github/...',
    `accessToken`              varchar(512)           DEFAULT NULL COMMENT 'accessToken',
    `expireIn`                 bigint                 DEFAULT '-1' COMMENT 'accessToken 过期时间，-1 表示无过期',
    `refreshTokenExpireIn`     bigint                 DEFAULT '-1' COMMENT 'refreshToken 过期时间，-1 表示无过期',
    `refreshToken`             varchar(512)           DEFAULT NULL COMMENT 'refreshToken',
    `uid`                      varchar(20)            DEFAULT NULL COMMENT 'alipay userId',
    `openId`                   varchar(256)           DEFAULT NULL COMMENT 'qq/mi/toutiao/wechatMp/...',
    `accessCode`               varchar(256)           DEFAULT NULL COMMENT 'dingTalk/taobao 附带属性',
    `unionId`                  varchar(256)           DEFAULT NULL COMMENT 'QQ 附带属性',
    `scope`                    varchar(256)           DEFAULT NULL COMMENT 'Google 附带属性',
    `tokenType`                varchar(20)            DEFAULT NULL COMMENT 'Google 附带属性',
    `idToken`                  varchar(256)           DEFAULT NULL COMMENT 'Google 附带属性',
    `macAlgorithm`             varchar(20)            DEFAULT NULL COMMENT '小米附带属性',
    `macKey`                   varchar(256)           DEFAULT NULL COMMENT '小米附带属性',
    `code`                     varchar(256)           DEFAULT NULL COMMENT '企业微信附带属性',
    `oauthToken`               varchar(256)           DEFAULT NULL COMMENT 'Twitter 附带属性',
    `oauthTokenSecret`         varchar(256)           DEFAULT NULL COMMENT 'Twitter 附带属性',
    `userId`                   varchar(64)            DEFAULT NULL COMMENT 'Twitter 附带属性',
    `screenName`               varchar(64)            DEFAULT NULL COMMENT 'Twitter 附带属性',
    `oauthCallbackConfirmed`   varchar(64)            DEFAULT NULL COMMENT 'Twitter 附带属性',
    `expireTime`               bigint                 DEFAULT '-1' COMMENT '过期时间 (1970-01-01T00:00:00Z 基准)，-1 表示无过期',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'JustAuth 第三方登录 Token 表';

-- =============================================================
-- 6. JustAuth 用户与第三方账号绑定表
-- 模块：kuma-boot-starter-security-spring
-- 说明：本地用户 userId 与第三方账号 (providerId + providerUserId) 的绑定
--       字段名可通过 ums.repository.*-column-name 配置项重写
--       默认 ums.repository.enable-start-up-initialize-table=true 会自动建表
-- =============================================================
CREATE TABLE IF NOT EXISTS `user_connection` (
    `userId`         varchar(36)  NOT NULL COMMENT '本地用户ID',
    `providerId`     varchar(20)  NOT NULL COMMENT '第三方服务商',
    `providerUserId` varchar(36)  NOT NULL COMMENT '第三方用户ID',
    `rank`           int          NOT NULL COMMENT '同一 (userId+providerId) 下的排序',
    `displayName`    varchar(64)           DEFAULT NULL COMMENT '第三方用户名',
    `profileUrl`     varchar(256)          DEFAULT NULL COMMENT '主页',
    `imageUrl`       varchar(256)          DEFAULT NULL COMMENT '头像',
    `accessToken`    varchar(512) NOT NULL COMMENT 'accessToken',
    `tokenId`        bigint                DEFAULT NULL COMMENT '关联 auth_token.id',
    `refreshToken`   varchar(512)          DEFAULT NULL COMMENT 'refreshToken',
    `expireTime`     bigint                DEFAULT '-1' COMMENT '过期时间 (1970-01-01T00:00:00Z 基准)，-1 表示无过期',
    PRIMARY KEY (`userId`, `providerId`, `providerUserId`),
    UNIQUE KEY `idx_userId_providerId_rank` (`userId`, `providerId`, `rank`),
    KEY `idx_providerId_providerUserId_rank` (`providerId`, `providerUserId`, `rank`),
    KEY `idx_tokenId` (`tokenId`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'JustAuth 用户与第三方账号绑定表';

-- =============================================================
-- 7. 多租户数据源注册表
-- 模块：kuma-boot-starter-data-jpa（JPA 多租户）
-- 说明：每个租户的数据源连接信息，启动时由 MultiTenantDataSourceFactory 读取
--       仅当 kuma.boot.data.jpa.tenant.enabled=true 时启用
--       通常依赖 Hibernate hbm2ddl 自动建表（dev/test 环境），生产建议使用本 DDL
-- =============================================================
CREATE TABLE IF NOT EXISTS `sys_tenant_datasource` (
    `datasource_id`     varchar(64)  NOT NULL COMMENT '数据源主键（UUID）',
    `tenant_id`         varchar(64)           DEFAULT NULL COMMENT '租户的唯一标识',
    `username`          varchar(100)          DEFAULT NULL COMMENT '数据库用户名',
    `password`          varchar(100)          DEFAULT NULL COMMENT '数据库密码',
    `driver_class_name` varchar(64)           DEFAULT NULL COMMENT '数据库驱动',
    `url`               varchar(1000)         DEFAULT NULL COMMENT '数据库连接 URL',
    `initialize`        tinyint(1)            DEFAULT '0' COMMENT '是否已初始化（0=否 1=是）',
    -- ── 来自 BaseSysEntity ──
    `status`            int                   DEFAULT NULL COMMENT '数据状态（DataItemStatusEnum 序号）',
    `is_reserved`       tinyint(1)            DEFAULT '0' COMMENT '是否保留数据（1=不可删 0=可删）',
    `reversion`         int                   DEFAULT '0' COMMENT '版本号',
    `description`       varchar(512)          DEFAULT NULL COMMENT '备注',
    -- ── 来自 BaseEntity ──
    `create_time`       datetime              DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
    `update_time`       datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据更新时间',
    `ranking`           int                   DEFAULT '0' COMMENT '排序值',
    PRIMARY KEY (`datasource_id`),
    UNIQUE KEY `uk_tenant_id` (`tenant_id`) COMMENT '租户ID唯一'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '多租户数据源注册表';
