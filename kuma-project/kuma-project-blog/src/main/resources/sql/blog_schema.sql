-- Blog schema: all table definitions
-- No USE statements — run against the target database directly.

-- -------------------------
-- 1. article_category
-- -------------------------
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

-- -------------------------
-- 2. article
-- -------------------------
CREATE TABLE IF NOT EXISTS `article` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title`         VARCHAR(255)  NOT NULL DEFAULT '' COMMENT '标题',
    `summary`       VARCHAR(512)           DEFAULT NULL COMMENT '摘要',
    `content`       LONGTEXT               DEFAULT NULL COMMENT '正文内容（Markdown）',
    `cover_image`   VARCHAR(500)           DEFAULT NULL COMMENT '封面图片URL',
    `category_id`   BIGINT                 DEFAULT NULL COMMENT '分类ID',
    `tags`          VARCHAR(255)           DEFAULT NULL COMMENT '标签（逗号分隔）',
    `author_id`     BIGINT                 DEFAULT NULL COMMENT '作者用户ID',
    `author_name`   VARCHAR(100)           DEFAULT NULL COMMENT '作者名称',
    `view_count`    INT           NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `like_count`    INT           NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comment_count` INT           NOT NULL DEFAULT 0 COMMENT '评论数',
    `status`        TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0=草稿，1=已发布，2=已删除',
    `is_top`        TINYINT       NOT NULL DEFAULT 0 COMMENT '是否置顶：0=否，1=是',
    `is_recommend`  TINYINT       NOT NULL DEFAULT 0 COMMENT '是否推荐：0=否，1=是',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `publish_time`  DATETIME               DEFAULT NULL COMMENT '发布时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_status` (`status`),
    KEY `idx_is_top` (`is_top`),
    KEY `idx_is_recommend` (`is_recommend`),
    KEY `idx_publish_time` (`publish_time`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章';

-- -------------------------
-- 3. music
-- -------------------------
CREATE TABLE IF NOT EXISTS `music` (
    `id`             BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '音乐ID',
    `name`           VARCHAR(255) NOT NULL COMMENT '音乐名称',
    `artist`         VARCHAR(255)          DEFAULT NULL COMMENT '艺术家/歌手',
    `album`          VARCHAR(255)          DEFAULT NULL COMMENT '专辑名称',
    `file_path`      VARCHAR(500) NOT NULL COMMENT '音乐文件路径',
    `file_url`       VARCHAR(500)          DEFAULT NULL COMMENT '音乐文件URL（访问地址）',
    `file_size`      BIGINT(20)            DEFAULT NULL COMMENT '文件大小（字节）',
    `file_type`      VARCHAR(50)           DEFAULT NULL COMMENT '文件类型（如：mp3, flac, wav等）',
    `duration`       INT(11)               DEFAULT NULL COMMENT '时长（秒）',
    `cover_image`    VARCHAR(500)          DEFAULT NULL COMMENT '封面图片URL',
    `genre`          VARCHAR(100)          DEFAULT NULL COMMENT '音乐分类/风格',
    `lyrics`         TEXT                  DEFAULT NULL COMMENT '歌词内容',
    `play_count`     INT(11)      NOT NULL DEFAULT 0 COMMENT '播放次数',
    `like_count`     INT(11)      NOT NULL DEFAULT 0 COMMENT '点赞数',
    `status`         TINYINT(4)   NOT NULL DEFAULT 1 COMMENT '状态：0-待审核，1-已发布，2-已删除',
    `is_recommend`   TINYINT(4)   NOT NULL DEFAULT 0 COMMENT '是否推荐：0-否，1-是',
    `upload_user_id` BIGINT(20)            DEFAULT NULL COMMENT '上传用户ID',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_name` (`name`),
    INDEX `idx_artist` (`artist`),
    INDEX `idx_album` (`album`),
    INDEX `idx_genre` (`genre`),
    INDEX `idx_status` (`status`),
    INDEX `idx_is_recommend` (`is_recommend`),
    INDEX `idx_play_count` (`play_count`),
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_upload_user_id` (`upload_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐表';

-- -------------------------
-- 4. playlist
-- -------------------------
CREATE TABLE IF NOT EXISTS `playlist` (
    `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '播放列表ID',
    `name`        VARCHAR(255) NOT NULL COMMENT '播放列表名称',
    `description` TEXT                  DEFAULT NULL COMMENT '播放列表描述',
    `cover_image` VARCHAR(500)          DEFAULT NULL COMMENT '封面图片URL',
    `user_id`     BIGINT(20)   NOT NULL COMMENT '创建用户ID',
    `music_count` INT(11)      NOT NULL DEFAULT 0 COMMENT '音乐数量',
    `play_count`  INT(11)      NOT NULL DEFAULT 0 COMMENT '播放次数',
    `is_public`   TINYINT(4)   NOT NULL DEFAULT 0 COMMENT '是否公开：0-私有，1-公开',
    `status`      TINYINT(4)   NOT NULL DEFAULT 0 COMMENT '状态：0-正常，1-已删除',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_is_public` (`is_public`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='播放列表表';

-- -------------------------
-- 5. playlist_music
-- -------------------------
CREATE TABLE IF NOT EXISTS `playlist_music` (
    `id`          BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `playlist_id` BIGINT(20) NOT NULL COMMENT '播放列表ID',
    `music_id`    BIGINT(20) NOT NULL COMMENT '音乐ID',
    `sort_order`  INT(11)    NOT NULL DEFAULT 0 COMMENT '排序顺序',
    `create_time` DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_playlist_music` (`playlist_id`, `music_id`),
    INDEX `idx_playlist_id` (`playlist_id`),
    INDEX `idx_music_id` (`music_id`),
    INDEX `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='播放列表音乐关联表';

-- -------------------------
-- 6. play_history
-- -------------------------
CREATE TABLE IF NOT EXISTS `play_history` (
    `id`            BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '历史记录ID',
    `user_id`       BIGINT(20) NOT NULL COMMENT '用户ID',
    `music_id`      BIGINT(20) NOT NULL COMMENT '音乐ID',
    `play_duration` INT(11)             DEFAULT NULL COMMENT '播放时长（秒）',
    `play_time`     DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '播放时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_music_id` (`music_id`),
    INDEX `idx_play_time` (`play_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='播放历史表';

-- -------------------------
-- 7. ready
-- -------------------------
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

-- -------------------------
-- 8. user
-- -------------------------
CREATE TABLE IF NOT EXISTS `user` (
    `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`        VARCHAR(50)  NOT NULL COMMENT '用户名（登录账号）',
    `password`        VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    `nickname`        VARCHAR(50)           DEFAULT NULL COMMENT '昵称（显示名称）',
    `email`           VARCHAR(100)          DEFAULT NULL COMMENT '邮箱',
    `avatar`          VARCHAR(255)          DEFAULT NULL COMMENT '头像URL',
    `phone`           VARCHAR(20)           DEFAULT NULL COMMENT '手机号',
    `is_admin`        TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否管理员：0-否，1-是',
    `status`          TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '用户状态：0-禁用，1-启用',
    `last_login_time` DATETIME              DEFAULT NULL COMMENT '最后登录时间',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    INDEX `idx_email` (`email`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- -------------------------
-- 9. source
-- -------------------------
CREATE TABLE IF NOT EXISTS `source` (
    `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '资源ID',
    `name`        VARCHAR(255) NOT NULL COMMENT '资源名称',
    `location`    VARCHAR(500) NOT NULL COMMENT '资源位置',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_name` (`name`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源管理表';

-- ─────────────────────────────────────────────────────────────
-- RBAC 权限管理（三类用户：管理员 / 普通用户 / 授权用户）
-- ─────────────────────────────────────────────────────────────

-- 10. sys_permission（权限定义表）
-- code 即权限码字符串，如 article:create、music:*
CREATE TABLE IF NOT EXISTS `sys_permission` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`        VARCHAR(100) NOT NULL COMMENT '权限码（module:action 格式，支持通配符 module:*）',
    `name`        VARCHAR(100) NOT NULL COMMENT '权限名称（可读描述）',
    `module`      VARCHAR(50)  NOT NULL COMMENT '所属模块（article / music / system）',
    `description` VARCHAR(255)          DEFAULT NULL COMMENT '说明',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_module` (`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限定义表';

-- 11. sys_role（角色表）
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`        VARCHAR(50)  NOT NULL COMMENT '角色码（ROLE_ADMIN / ROLE_USER）',
    `name`        VARCHAR(100) NOT NULL COMMENT '角色名称',
    `description` VARCHAR(255)          DEFAULT NULL COMMENT '说明',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 12. sys_role_permission（角色 → 权限关联）
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
    `role_id`       BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 13. sys_user_role（用户 → 角色关联）
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 14. sys_user_permission（用户 → 权限直接授权，授权用户专用）
-- 当用户不是管理员，但需要特定权限时，在此表直接授权，无需改角色
CREATE TABLE IF NOT EXISTS `sys_user_permission` (
    `user_id`       BIGINT NOT NULL COMMENT '用户ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    `granted_by`    BIGINT          DEFAULT NULL COMMENT '授权人用户ID',
    `grant_time`    DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
    PRIMARY KEY (`user_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户直接权限授权表（授权用户）';

-- ─────────────────────────────────────────────────────────────
-- RBAC 初始化数据
-- ─────────────────────────────────────────────────────────────

-- 权限码初始化
INSERT INTO `sys_permission` (`id`, `code`, `name`, `module`) VALUES
(1,  'article:*',      '文章全部权限',     'article'),
(2,  'article:create', '创建文章',         'article'),
(3,  'article:update', '编辑文章',         'article'),
(4,  'article:delete', '删除文章',         'article'),
(5,  'article:read',   '查看文章',         'article'),
(6,  'music:*',        '音乐全部权限',     'music'),
(7,  'music:upload',   '上传音乐',         'music'),
(8,  'music:delete',   '删除音乐',         'music'),
(9,  'music:read',     '播放/查看音乐',    'music'),
(10, 'system:*',       '系统全部权限',     'system'),
(11, 'system:config',  '系统配置',         'system'),
(12, 'system:log',     '查看日志',         'system'),
(13, 'system:user',    '用户管理',         'system')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `module` = VALUES(`module`);

-- 角色初始化（三类用户）
INSERT INTO `sys_role` (`id`, `code`, `name`, `description`) VALUES
(1, 'ROLE_ADMIN', '管理员', '拥有所有权限'),
(2, 'ROLE_USER',  '普通用户', '仅拥有基础读权限')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `description` = VALUES(`description`);

-- 角色权限关联
-- 管理员拥有所有模块通配符
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 1), (1, 6), (1, 10)   -- ROLE_ADMIN → article:* + music:* + system:*
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- 普通用户拥有只读权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(2, 5), (2, 9)             -- ROLE_USER → article:read + music:read
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- -------------------------
-- Initial category data
-- -------------------------
INSERT INTO `article_category` (`id`, `code`, `name`, `parent_id`, `level`, `icon`, `sort_order`) VALUES
(1,    'Diary',            '日记',   NULL, 0, 'BookOutlined',      1),
(2,    'Tech',             '技术',   NULL, 0, 'CodeOutlined',      2),
(3,    'Tool',             '工具',   NULL, 0, 'ToolOutlined',      3),
(22,   'BackEnd',          '后端',   2,    1, 'ApiOutlined',       10),
(23,   'DATASOURCE',       '数据库', 2,    1, 'DatabaseOutlined',  11),
(24,   'FrontEnd',         '前端',   2,    1, 'LayoutOutlined',    12),
(25,   'AI',               '人工智能',2,   1, 'RobotOutlined',     13),
(26,   'BigData',          '大数据', 2,    1, 'BarChartOutlined',  14),
(221,  'BackEndJava',      'Java',   22,   2, 'CoffeeOutlined',    1),
(222,  'BackEndPython',    'Python', 22,   2, 'ThunderboltOutlined',2),
(2210, 'BackEndJavaJavaSE','JavaSE', 221,  3, '',                  1),
(2211, 'BackEndJavaJuc',   'Juc',    221,  3, '',                  2),
(2212, 'BackEndJavaSpring','Spring', 221,  3, 'CloudOutlined',     3),
(261,  'BigDataFlink',     'Flink',  26,   2, 'RocketOutlined',    1)
ON DUPLICATE KEY UPDATE
    `name`       = VALUES(`name`),
    `parent_id`  = VALUES(`parent_id`),
    `level`      = VALUES(`level`),
    `icon`       = VALUES(`icon`),
    `sort_order` = VALUES(`sort_order`);
