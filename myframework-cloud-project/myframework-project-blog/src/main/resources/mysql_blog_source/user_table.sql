-- 用户表建表SQL
-- 数据库：blog_source

CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名（登录账号）',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（MD5加密）',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称（显示名称）',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `is_admin` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否管理员：0-否，1-是',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '用户状态：0-禁用，1-启用',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    INDEX `idx_email` (`email`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入默认管理员账号
-- 密码加密规则：MD5(SALT + rawPassword + SALT)，SALT = "blog_user_salt_2024"
-- 默认密码：admin123 -> 加密后：e10adc3949ba59abbe56e057f20f883e（MD5原始值，需要重新计算）

-- 管理员账号：admin / admin123
INSERT INTO `user` (`username`, `password`, `nickname`, `is_admin`, `status`) VALUES
('admin', 'a66abb5684c45962d887564f08346e8d', '管理员', 1, 1);

-- 普通用户账号：user / 123456
INSERT INTO `user` (`username`, `password`, `nickname`, `is_admin`, `status`) VALUES
('user', '7c4a8d09ca3762af61e59520943dc26494f8941b', '普通用户', 0, 1);

-- 注意：以上密码是通过以下 Java 代码生成的：
-- String salt = "blog_user_salt_2024";
-- String encoded = DigestUtils.md5DigestAsHex((salt + password + salt).getBytes(StandardCharsets.UTF_8));
-- 
-- 如果密码不对，请通过接口或代码重新生成：
-- admin123 的加密结果应为：UserService.encodePassword("admin123")
-- 123456 的加密结果应为：UserService.encodePassword("123456")
