package com.kuma.cloud.uaa.security;

/**
 * UAA 管理 API 权限码，供 {@link com.kuma.boot.security.spring.access.expression.Authorize} 使用。
 *
 * <p>格式遵循框架约定 {@code module:action}，支持 {@code uaa:*} 模块通配符（见 {@code AuthorizeCheckService}）。
 */
public final class UaaPermissions {

    public static final String MODULE_WILDCARD = "uaa:*";

    public static final String USER_READ = "uaa:user:read";

    public static final String USER_WRITE = "uaa:user:write";

    public static final String ROLE_READ = "uaa:role:read";

    public static final String ROLE_WRITE = "uaa:role:write";

    public static final String PERMISSION_READ = "uaa:permission:read";

    public static final String PERMISSION_WRITE = "uaa:permission:write";

    public static final String CLIENT_READ = "uaa:client:read";

    public static final String CLIENT_WRITE = "uaa:client:write";

    private UaaPermissions() {}
}
