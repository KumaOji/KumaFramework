-- =============================================================================
-- Kuma UAA 建表脚本（MySQL 8.0+）
--
-- 前三张 oauth2_* 表来自 Spring Authorization Server 官方 schema
-- （spring-security-oauth2-authorization-server-7.0.3.jar 内
--   org/springframework/security/oauth2/server/authorization/*.sql），
-- 按 MySQL 方言做了两处必要调整：
--   1. MySQL 不允许 blob 列声明 DEFAULT，故去掉 blob 列的 DEFAULT NULL；
--   2. timestamp 列显式声明 NULL DEFAULT NULL，避免隐式 NOT NULL。
--
-- 时间实例的准确性依赖 JDBC 连接参数，务必在 url 上追加：
--   preserveInstants=true&connectionTimeZone=UTC&forceConnectionTimeZoneToSession=true
-- =============================================================================

-- ── OAuth2 客户端注册表（JdbcRegisteredClientRepository） ──────────────────────
CREATE TABLE IF NOT EXISTS oauth2_registered_client
(
    id                            VARCHAR(100)  NOT NULL,
    client_id                     VARCHAR(100)  NOT NULL,
    client_id_issued_at           TIMESTAMP(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    client_secret                 VARCHAR(200)  NULL     DEFAULT NULL,
    client_secret_expires_at      TIMESTAMP(6)  NULL     DEFAULT NULL,
    client_name                   VARCHAR(200)  NOT NULL,
    client_authentication_methods VARCHAR(1000) NOT NULL,
    authorization_grant_types     VARCHAR(1000) NOT NULL,
    redirect_uris                 VARCHAR(1000) NULL     DEFAULT NULL,
    post_logout_redirect_uris     VARCHAR(1000) NULL     DEFAULT NULL,
    scopes                        VARCHAR(1000) NOT NULL,
    client_settings               VARCHAR(2000) NOT NULL,
    token_settings                VARCHAR(2000) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_oauth2_registered_client_client_id (client_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT 'OAuth2 接入客户端';

-- ── OAuth2 授权记录表（JdbcOAuth2AuthorizationService） ────────────────────────
CREATE TABLE IF NOT EXISTS oauth2_authorization
(
    id                            VARCHAR(100)  NOT NULL,
    registered_client_id          VARCHAR(100)  NOT NULL,
    principal_name                VARCHAR(200)  NOT NULL,
    authorization_grant_type      VARCHAR(100)  NOT NULL,
    authorized_scopes             VARCHAR(1000) NULL DEFAULT NULL,
    attributes                    BLOB          NULL,
    state                         VARCHAR(500)  NULL DEFAULT NULL,
    authorization_code_value      BLOB          NULL,
    authorization_code_issued_at  TIMESTAMP(6)  NULL DEFAULT NULL,
    authorization_code_expires_at TIMESTAMP(6)  NULL DEFAULT NULL,
    authorization_code_metadata   BLOB          NULL,
    access_token_value            BLOB          NULL,
    access_token_issued_at        TIMESTAMP(6)  NULL DEFAULT NULL,
    access_token_expires_at       TIMESTAMP(6)  NULL DEFAULT NULL,
    access_token_metadata         BLOB          NULL,
    access_token_type             VARCHAR(100)  NULL DEFAULT NULL,
    access_token_scopes           VARCHAR(1000) NULL DEFAULT NULL,
    oidc_id_token_value           BLOB          NULL,
    oidc_id_token_issued_at       TIMESTAMP(6)  NULL DEFAULT NULL,
    oidc_id_token_expires_at      TIMESTAMP(6)  NULL DEFAULT NULL,
    oidc_id_token_metadata        BLOB          NULL,
    refresh_token_value           BLOB          NULL,
    refresh_token_issued_at       TIMESTAMP(6)  NULL DEFAULT NULL,
    refresh_token_expires_at      TIMESTAMP(6)  NULL DEFAULT NULL,
    refresh_token_metadata        BLOB          NULL,
    user_code_value               BLOB          NULL,
    user_code_issued_at           TIMESTAMP(6)  NULL DEFAULT NULL,
    user_code_expires_at          TIMESTAMP(6)  NULL DEFAULT NULL,
    user_code_metadata            BLOB          NULL,
    device_code_value             BLOB          NULL,
    device_code_issued_at         TIMESTAMP(6)  NULL DEFAULT NULL,
    device_code_expires_at        TIMESTAMP(6)  NULL DEFAULT NULL,
    device_code_metadata          BLOB          NULL,
    PRIMARY KEY (id),
    KEY idx_oauth2_authorization_principal (principal_name),
    KEY idx_oauth2_authorization_client (registered_client_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT 'OAuth2 授权记录';

-- ── OAuth2 用户授权同意表（JdbcOAuth2AuthorizationConsentService） ─────────────
CREATE TABLE IF NOT EXISTS oauth2_authorization_consent
(
    registered_client_id VARCHAR(100)  NOT NULL,
    principal_name       VARCHAR(200)  NOT NULL,
    authorities          VARCHAR(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT 'OAuth2 用户授权同意';

-- ── UAA 自有的身份与权限模型 ──────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS uaa_user
(
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    username      VARCHAR(64)     NOT NULL COMMENT '登录名，签发 JWT 时作为 sub 与 preferred_username',
    password      VARCHAR(128)    NOT NULL COMMENT 'DelegatingPasswordEncoder 编码后的密文，形如 {bcrypt}$2a$10$...',
    nickname      VARCHAR(64)     NULL     DEFAULT NULL,
    email         VARCHAR(128)    NULL     DEFAULT NULL,
    phone         VARCHAR(32)     NULL     DEFAULT NULL,
    avatar        VARCHAR(512)    NULL     DEFAULT NULL,
    status        TINYINT         NOT NULL DEFAULT 1 COMMENT '1 启用 0 禁用',
    locked        TINYINT         NOT NULL DEFAULT 0 COMMENT '1 已锁定 0 正常',
    mfa_enabled   TINYINT         NOT NULL DEFAULT 0 COMMENT '1 已开启 TOTP 二次校验',
    mfa_secret    VARCHAR(128)    NULL     DEFAULT NULL COMMENT 'TOTP 共享密钥（Base32）',
    tenant_id     VARCHAR(64)     NOT NULL DEFAULT 'default',
    last_login_at DATETIME        NULL     DEFAULT NULL,
    last_login_ip VARCHAR(64)     NULL     DEFAULT NULL,
    create_time   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_uaa_user_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT 'UAA 用户';

CREATE TABLE IF NOT EXISTS uaa_role
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    code        VARCHAR(64)     NOT NULL COMMENT '角色编码，不含 ROLE_ 前缀，如 ADMIN',
    name        VARCHAR(64)     NOT NULL,
    description VARCHAR(255)    NULL     DEFAULT NULL,
    status      TINYINT         NOT NULL DEFAULT 1,
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_uaa_role_code (code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT 'UAA 角色';

CREATE TABLE IF NOT EXISTS uaa_permission
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    code        VARCHAR(128)    NOT NULL COMMENT '权限编码，如 uaa:user:read',
    name        VARCHAR(64)     NOT NULL,
    resource    VARCHAR(255)    NULL     DEFAULT NULL COMMENT '受保护资源描述，如 GET /api/users',
    status      TINYINT         NOT NULL DEFAULT 1,
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_uaa_permission_code (code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT 'UAA 权限';

CREATE TABLE IF NOT EXISTS uaa_user_role
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id     BIGINT UNSIGNED NOT NULL,
    role_id     BIGINT UNSIGNED NOT NULL,
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_uaa_user_role (user_id, role_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT 'UAA 用户角色关联';

CREATE TABLE IF NOT EXISTS uaa_role_permission
(
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    role_id       BIGINT UNSIGNED NOT NULL,
    permission_id BIGINT UNSIGNED NOT NULL,
    create_time   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_uaa_role_permission (role_id, permission_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT 'UAA 角色权限关联';

CREATE TABLE IF NOT EXISTS uaa_login_log
(
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    username     VARCHAR(64)     NOT NULL,
    success      TINYINT         NOT NULL,
    failure_code VARCHAR(64)     NULL     DEFAULT NULL COMMENT '失败原因，如 bad_credentials / captcha / mfa / locked',
    client_ip    VARCHAR(64)     NULL     DEFAULT NULL,
    user_agent   VARCHAR(512)    NULL     DEFAULT NULL,
    create_time  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_uaa_login_log_username (username, create_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT 'UAA 登录审计';
