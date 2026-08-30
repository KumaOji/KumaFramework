-- =============================================================
-- base 库清理：删除历史遗留 / 未启用的表
-- Blog 服务连接 base；UAA/OAuth2 已迁移至 kuma_uaa 独立库
-- 执行前请确认无其他服务依赖这些表
-- =============================================================

USE `base`;

-- UAA 历史遗留（当前在 kuma_uaa 库）
DROP TABLE IF EXISTS `uaa_login_log`;
DROP TABLE IF EXISTS `uaa_role_permission`;
DROP TABLE IF EXISTS `uaa_user_role`;
DROP TABLE IF EXISTS `uaa_permission`;
DROP TABLE IF EXISTS `uaa_role`;
DROP TABLE IF EXISTS `uaa_user`;

-- OAuth2 历史遗留（当前在 kuma_uaa 库）
DROP TABLE IF EXISTS `oauth2_authorization_consent`;
DROP TABLE IF EXISTS `oauth2_authorization`;
DROP TABLE IF EXISTS `oauth2_registered_client`;

-- Blog 死代码表（RAG 已改用向量库，无 Controller 引用）
DROP TABLE IF EXISTS `source`;

-- 框架功能表（Blog 未启用对应特性）
DROP TABLE IF EXISTS `app_delay_message`;      -- delay 功能未开启
DROP TABLE IF EXISTS `business_idempotent`;    -- MySQL 幂等适配器未开启
DROP TABLE IF EXISTS `undo_log`;               -- Seata AT 未开启
DROP TABLE IF EXISTS `tx_demo_account`;        -- 仅 lab 实验用
