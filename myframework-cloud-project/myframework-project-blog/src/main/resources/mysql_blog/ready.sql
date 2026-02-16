-- 待处理事项表（与 blog 库同库，建表前请 USE 对应 schema）
-- 状态 status：0-待处理 1-进行中 2-已完成 3-已删除（逻辑删除）
-- 优先级 priority：0-普通 1-高 2-紧急

CREATE TABLE IF NOT EXISTS `ready` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title`       VARCHAR(255) NOT NULL DEFAULT '' COMMENT '标题',
    `content`     TEXT                  DEFAULT NULL COMMENT '备注内容',
    `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-待处理 1-进行中 2-已完成 3-已删除',
    `priority`    TINYINT      NOT NULL DEFAULT 0 COMMENT '优先级：0-普通 1-高 2-紧急',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='待处理事项';
