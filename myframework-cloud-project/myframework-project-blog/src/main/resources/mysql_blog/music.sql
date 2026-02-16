-- 音乐播放器相关表建表SQL
-- 数据库：blog
-- 表名：music, playlist, playlist_music, play_history

-- 音乐表
CREATE TABLE IF NOT EXISTS `music` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '音乐ID',
    `name` VARCHAR(255) NOT NULL COMMENT '音乐名称',
    `artist` VARCHAR(255) DEFAULT NULL COMMENT '艺术家/歌手',
    `album` VARCHAR(255) DEFAULT NULL COMMENT '专辑名称',
    `file_path` VARCHAR(500) NOT NULL COMMENT '音乐文件路径（支持绝对路径，如 E:\\blog-resource\\music\\下等马.flac）',
    `file_url` VARCHAR(500) DEFAULT NULL COMMENT '音乐文件URL（访问地址）',
    `file_size` BIGINT(20) DEFAULT NULL COMMENT '文件大小（字节）',
    `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件类型（如：mp3, flac, wav等）',
    `duration` INT(11) DEFAULT NULL COMMENT '时长（秒）',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    `genre` VARCHAR(100) DEFAULT NULL COMMENT '音乐分类/风格',
    `lyrics` TEXT DEFAULT NULL COMMENT '歌词内容',
    `play_count` INT(11) NOT NULL DEFAULT 0 COMMENT '播放次数',
    `like_count` INT(11) NOT NULL DEFAULT 0 COMMENT '点赞数',
    `status` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '状态：0-待审核，1-已发布，2-已删除',
    `is_recommend` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '是否推荐：0-否，1-是',
    `upload_user_id` BIGINT(20) DEFAULT NULL COMMENT '上传用户ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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

-- 播放列表表
CREATE TABLE IF NOT EXISTS `playlist` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '播放列表ID',
    `name` VARCHAR(255) NOT NULL COMMENT '播放列表名称',
    `description` TEXT DEFAULT NULL COMMENT '播放列表描述',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    `user_id` BIGINT(20) NOT NULL COMMENT '创建用户ID',
    `music_count` INT(11) NOT NULL DEFAULT 0 COMMENT '音乐数量',
    `play_count` INT(11) NOT NULL DEFAULT 0 COMMENT '播放次数',
    `is_public` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '是否公开：0-私有，1-公开',
    `status` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态：0-正常，1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_is_public` (`is_public`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='播放列表表';

-- 播放列表音乐关联表
CREATE TABLE IF NOT EXISTS `playlist_music` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `playlist_id` BIGINT(20) NOT NULL COMMENT '播放列表ID',
    `music_id` BIGINT(20) NOT NULL COMMENT '音乐ID',
    `sort_order` INT(11) NOT NULL DEFAULT 0 COMMENT '排序顺序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_playlist_music` (`playlist_id`, `music_id`),
    INDEX `idx_playlist_id` (`playlist_id`),
    INDEX `idx_music_id` (`music_id`),
    INDEX `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='播放列表音乐关联表';

-- 播放历史表
CREATE TABLE IF NOT EXISTS `play_history` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '历史记录ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `music_id` BIGINT(20) NOT NULL COMMENT '音乐ID',
    `play_duration` INT(11) DEFAULT NULL COMMENT '播放时长（秒）',
    `play_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '播放时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_music_id` (`music_id`),
    INDEX `idx_play_time` (`play_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='播放历史表';
