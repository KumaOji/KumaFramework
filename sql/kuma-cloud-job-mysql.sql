-- kuma-cloud-job 数据库初始化脚本
-- 执行方式: mysql -u root -p < kuma-cloud-job-mysql.sql

CREATE DATABASE IF NOT EXISTS `kuma-cloud-kmcjob` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `kuma-cloud-kmcjob`;

-- 应用信息表
CREATE TABLE IF NOT EXISTS `app_info` (
    `id`             bigint       NOT NULL AUTO_INCREMENT,
    `app_name`       varchar(128) NOT NULL COMMENT '应用名称',
    `sub_app_name`   varchar(128)          DEFAULT NULL COMMENT '子应用名称（分组用）',
    `current_server` varchar(128)          DEFAULT NULL COMMENT '当前分配的server地址',
    `password`       varchar(128)          DEFAULT NULL COMMENT '应用密码',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_app_name` (`app_name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '应用信息表';

-- 任务信息表
CREATE TABLE IF NOT EXISTS `job_info` (
    `id`                   bigint        NOT NULL AUTO_INCREMENT,
    `job_id`               bigint                 DEFAULT NULL COMMENT '任务ID',
    `app_name`             varchar(128)  NOT NULL COMMENT '所属应用名称',
    `job_name`             varchar(128)           DEFAULT NULL COMMENT '任务名称',
    `job_description`      varchar(256)           DEFAULT NULL COMMENT '任务描述',
    `job_params`           varchar(512)           DEFAULT NULL COMMENT '任务参数',
    `processor_info`       varchar(256)           DEFAULT NULL COMMENT '处理器信息',
    `time_expression_type` int                    DEFAULT NULL COMMENT '时间表达式类型(1-CRON 3-DAILY_TIME_INTERVAL)',
    `time_expression`      varchar(128)           DEFAULT NULL COMMENT '时间表达式',
    `lifecycle`            varchar(128)           DEFAULT NULL COMMENT '任务生命周期',
    `dispatch_strategy`    int                    DEFAULT NULL COMMENT '调度策略',
    `max_instance_num`     int                    DEFAULT NULL COMMENT '最大实例数',
    `status`               int                    DEFAULT NULL COMMENT '状态(1-启用 2-停用)',
    `next_trigger_time`    bigint                 DEFAULT NULL COMMENT '下次触发时间(ms)',
    `gmt_create`           datetime               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`         datetime               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_app_name` (`app_name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '任务信息表';

-- 任务实例表
CREATE TABLE IF NOT EXISTS `instance_info` (
    `id`                   bigint       NOT NULL AUTO_INCREMENT,
    `instance_id`          bigint                DEFAULT NULL COMMENT '任务实例ID',
    `job_id`               bigint                DEFAULT NULL COMMENT '所属任务ID',
    `app_name`             varchar(128)          DEFAULT NULL COMMENT '所属应用',
    `job_params`           varchar(512)          DEFAULT NULL COMMENT '任务参数快照',
    `instance_params`      varchar(512)          DEFAULT NULL COMMENT '实例参数',
    `type`                 int                   DEFAULT NULL COMMENT '类型(1-普通 2-工作流)',
    `wf_instance_id`       bigint                DEFAULT NULL COMMENT '工作流实例ID',
    `status`               int                   DEFAULT NULL COMMENT '状态',
    `result`               text                  DEFAULT NULL COMMENT '执行结果',
    `expected_trigger_time` bigint               DEFAULT NULL COMMENT '预计触发时间(ms)',
    `actual_trigger_time`  bigint                DEFAULT NULL COMMENT '实际触发时间(ms)',
    `finished_time`        bigint                DEFAULT NULL COMMENT '完成时间(ms)',
    `last_report_time`     bigint                DEFAULT NULL COMMENT '最后上报时间(ms)',
    `running_times`        bigint                DEFAULT NULL COMMENT '运行次数',
    `task_tracker_address` varchar(128)          DEFAULT NULL COMMENT '执行节点地址',
    `gmt_create`           datetime              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`         datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_job_id` (`job_id`),
    KEY `idx_instance_id` (`instance_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '任务实例表';

-- 分布式锁表
CREATE TABLE IF NOT EXISTS `distributed_lock` (
    `id`              bigint      NOT NULL AUTO_INCREMENT,
    `lock_name`       varchar(128) NOT NULL COMMENT '锁名称',
    `lock_owner`      varchar(128)          DEFAULT NULL COMMENT '锁持有者',
    `expiration_time` datetime              DEFAULT NULL COMMENT '锁过期时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_lock_name` (`lock_name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '分布式锁表';
