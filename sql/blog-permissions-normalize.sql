-- Blog 默认权限整理；仅在 Blog 数据库执行（本地开发库为 base）。
-- 管理员继承所有权限；ROLE_USER 无业务授权，与游客共用公开接口。
-- 保留用户已有的单独授权、自定义角色及历史权限，不删除用户或修改密码。
-- 执行前备份 user 的身份字段及 sys_* 权限表；执行后清除受影响用户的权限缓存。
SET NAMES utf8mb4;
START TRANSACTION;
INSERT INTO sys_role (code, name, description) VALUES
('ROLE_ADMIN', '管理员', '默认拥有全部权限'),
('ROLE_USER', '普通用户', '默认与游客一致，额外能力通过用户单独授权')
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);
INSERT INTO sys_permission (code, name, module, description) VALUES
('article:*', '文章全部权限', 'article', '文章模块所有权限'),
('article:create', '创建文章', 'article', '发布新文章'),
('article:update', '编辑文章', 'article', '修改已有文章'),
('article:delete', '删除文章', 'article', '删除文章'),
('article:read', '查看文章', 'article', '查看文章'),
('music:*', '音乐全部权限', 'music', '音乐模块所有权限'),
('music:upload', '上传音乐', 'music', '上传音乐文件'),
('music:delete', '删除音乐', 'music', '删除音乐'),
('music:read', '播放/查看音乐', 'music', '播放与查看音乐'),
('system:*', '系统全部权限', 'system', '系统模块所有权限'),
('system:config', '系统配置', 'system', '修改系统配置'),
('system:log', '查看日志', 'system', '查看系统日志'),
('system:user', '用户管理', 'system', '管理用户与授权'),
('project:*', '项目全部权限', 'project', '项目模块所有权限'),
('project:create', '创建项目', 'project', '新增项目'),
('project:update', '编辑项目', 'project', '修改项目'),
('project:delete', '删除项目', 'project', '删除项目'),
('project:read', '查看项目', 'project', '浏览项目'),
('message:*', '留言板全部权限', 'message', '留言板所有权限'),
('message:audit', '审核留言', 'message', '通过或驳回留言'),
('message:delete', '删除留言', 'message', '删除留言'),
('friend_link:*', '友链全部权限', 'friend_link', '友链模块所有权限'),
('friend_link:create', '创建友链', 'friend_link', '新增友链'),
('friend_link:update', '编辑友链', 'friend_link', '修改友链'),
('friend_link:delete', '删除友链', 'friend_link', '删除友链'),
('friend_link:audit', '审核友链申请', 'friend_link', '通过友链申请'),
('ai_chat:*', 'AI对话全部权限', 'ai_chat', '对话、知识库与文本工具全部权限'),
('ai_chat:send', '发起对话', 'ai_chat', '普通对话与流式推理'),
('ai_chat:ingest', '知识库入库', 'ai_chat', '上传文档到向量知识库、查看来源、检索测试'),
('ai_chat:rag', '知识库问答', 'ai_chat', 'RAG 增强对话与会话管理'),
('ai_chat:text', '文本工具', 'ai_chat', '摘要、翻译、关键词、情感分析'),
('chat:*', '聊天室全部权限', 'chat', '聊天室模块所有权限'),
('chat:read', '查看聊天', 'chat', '查看聊天室、历史消息和在线用户'),
('chat:send', '发送聊天消息', 'chat', '进入聊天室并发送消息（同时需要查看聊天权限）'),
('chat:create', '创建聊天室', 'chat', '新建聊天室'),
('chat:update', '编辑聊天室', 'chat', '修改聊天室'),
('chat:delete', '删除聊天室', 'chat', '删除聊天室'),
('chat:blacklist', '聊天室黑名单', 'chat', '管理聊天黑名单'),
('chat:history', '聊天历史', 'chat', '清理或删除聊天记录')
ON DUPLICATE KEY UPDATE name = VALUES(name), module = VALUES(module), description = VALUES(description);
-- 保留原有的两种管理员身份来源，并使其一致，避免角色与 is_admin 判断不同。
UPDATE user u
INNER JOIN sys_user_role ur ON ur.user_id = u.id
INNER JOIN sys_role r ON r.id = ur.role_id AND r.code = 'ROLE_ADMIN'
SET u.is_admin = 1 WHERE COALESCE(u.is_admin, 0) <> 1;
INSERT IGNORE INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM user u CROSS JOIN sys_role r
WHERE r.code = 'ROLE_ADMIN' AND u.is_admin = 1;
INSERT IGNORE INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM user u CROSS JOIN sys_role r
WHERE r.code = 'ROLE_USER' AND COALESCE(u.is_admin, 0) <> 1;
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r CROSS JOIN sys_permission p
WHERE r.code = 'ROLE_ADMIN' AND p.code LIKE '%:*';
DELETE rp FROM sys_role_permission rp
INNER JOIN sys_role r ON r.id = rp.role_id
INNER JOIN sys_permission p ON p.id = rp.permission_id
WHERE r.code = 'ROLE_ADMIN' AND p.code NOT LIKE '%:*';
DELETE rp FROM sys_role_permission rp
INNER JOIN sys_role r ON r.id = rp.role_id WHERE r.code = 'ROLE_USER';
COMMIT;
