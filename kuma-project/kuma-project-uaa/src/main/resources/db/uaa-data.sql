-- =============================================================================
-- Kuma UAA 初始化数据（角色与权限字典）
--
-- 管理员账号与 OAuth2 客户端不在此处写入：
--   - 管理员密码需经 DelegatingPasswordEncoder 编码，
--   - 客户端 client_settings / token_settings 为 SAS 内部 JSON 结构，
-- 二者均由 com.kuma.cloud.uaa.support.UaaDataInitializer 在启动时按配置幂等写入。
-- =============================================================================

INSERT INTO uaa_role (code, name, description)
VALUES ('ADMIN', '超级管理员', '拥有 UAA 全部管理权限'),
       ('USER', '普通用户', '仅可管理自己的账号与 MFA')
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);

INSERT INTO uaa_permission (code, name, resource)
VALUES ('uaa:user:read', '用户查询', 'GET /api/users'),
       ('uaa:user:write', '用户维护', 'POST,PUT,DELETE /api/users'),
       ('uaa:role:read', '角色查询', 'GET /api/roles'),
       ('uaa:role:write', '角色维护', 'POST,PUT,DELETE /api/roles'),
       ('uaa:permission:read', '权限查询', 'GET /api/permissions'),
       ('uaa:permission:write', '权限维护', 'POST,PUT,DELETE /api/permissions'),
       ('uaa:client:read', '客户端查询', 'GET /api/clients'),
       ('uaa:client:write', '客户端维护', 'POST,PUT,DELETE /api/clients')
ON DUPLICATE KEY UPDATE name = VALUES(name), resource = VALUES(resource);

-- ADMIN 角色绑定全部权限
INSERT INTO uaa_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM uaa_role r
         CROSS JOIN uaa_permission p
WHERE r.code = 'ADMIN'
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);
