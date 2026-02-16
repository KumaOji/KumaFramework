-- 资源管理表建表SQL
-- 数据库：blog_source
-- 表名：source

CREATE TABLE IF NOT EXISTS `source` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '资源ID',
    `name` VARCHAR(255) NOT NULL COMMENT '资源名称',
    `location` VARCHAR(500) NOT NULL COMMENT '资源位置',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_name` (`name`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源管理表';
