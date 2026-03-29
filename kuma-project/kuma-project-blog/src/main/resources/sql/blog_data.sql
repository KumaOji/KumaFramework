-- Initial seed data for the blog application.
-- Passwords are BCrypt-encoded. Run this after blog_schema.sql.

-- Default admin user: admin / admin123
INSERT INTO `user` (`username`, `password`, `nickname`, `is_admin`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6OICK', '管理员', 1, 1)
ON DUPLICATE KEY UPDATE `nickname` = VALUES(`nickname`);

-- 关联 admin 用户到 ROLE_ADMIN 角色（用子查询避免硬编码 ID）
INSERT IGNORE INTO `sys_user_role` (`user_id`, `role_id`)
SELECT u.id, r.id FROM `user` u
INNER JOIN `sys_role` r ON r.code = 'ROLE_ADMIN'
WHERE u.username = 'admin';
