-- 文章分类表（与 blog 库同库，建表前请 USE 对应 schema）
-- 支持多级分类：parent_id 为 null 表示顶级，level 表示层级（0=顶级，1=二级，2=三级…）

CREATE TABLE IF NOT EXISTS `article_category` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `code`       VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '分类代码（英文标识）',
    `name`       VARCHAR(128) NOT NULL DEFAULT '' COMMENT '分类名称（中文显示）',
    `parent_id`  BIGINT                DEFAULT NULL COMMENT '父分类ID，null 表示顶级',
    `level`      TINYINT      NOT NULL DEFAULT 0 COMMENT '层级：0=顶级，1=二级，2=三级…',
    `icon`       VARCHAR(128) NOT NULL DEFAULT '' COMMENT '小图标：图标类名/图标 key/图片 URL，前端按约定渲染',
    `sort_order` INT          NOT NULL DEFAULT 0 COMMENT '排序，数值越小越靠前',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_level` (`level`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章分类（多级）';

-- 若表已存在，可执行：ALTER TABLE article_category ADD COLUMN icon VARCHAR(128) NOT NULL DEFAULT '' COMMENT '小图标：图标类名/图标 key/图片 URL' AFTER level;

-- 初始数据（icon 可为图标 key、类名或图片 URL，前端按约定渲染；可后续在后台改）
INSERT INTO `article_category` (`id`, `code`, `name`, `parent_id`, `level`, `icon`, `sort_order`) VALUES
(1, 'Diary', '日记', NULL, 0, 'BookOutlined', 1),
(2, 'Tech', '技术', NULL, 0, 'CodeOutlined', 2),
(3, 'Tool', '工具', NULL, 0, 'ToolOutlined', 3),
(22, 'BackEnd', '后端', 2, 1, 'ApiOutlined', 10),
(23, 'DATASOURCE', '数据库', 2, 1, 'DatabaseOutlined', 11),
(24, 'FrontEnd', '前端', 2, 1, 'LayoutOutlined', 12),
(25, 'AI', '人工智能', 2, 1, 'RobotOutlined', 13),
(26, 'BigData', '大数据', 2, 1, 'BarChartOutlined', 14),
(221, 'BackEndJava', 'Java', 22, 2, 'CoffeeOutlined', 1),
(222, 'BackEndPython', 'Python', 22, 2, 'ThunderboltOutlined', 2),
(2210, 'BackEndJavaJavaSE', 'JavaSE', 221, 3, '', 1),
(2211, 'BackEndJavaJuc', 'Juc', 221, 3, '', 2),
(2212, 'BackEndJavaSpring', 'Spring', 221, 3, 'CloudOutlined', 3),
(261, 'BigDataFlink', 'Flink', 26, 2, 'RocketOutlined', 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `parent_id` = VALUES(`parent_id`), `level` = VALUES(`level`), `icon` = VALUES(`icon`), `sort_order` = VALUES(`sort_order`);
