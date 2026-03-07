package com.kuma.boot.security.spring.access.expression;

/**
 * 角色权限字符串常量，配合 {@link Authorize} 注解使用，避免硬编码。
 *
 * <pre>{@code
 * // 推荐写法
 * @Authorize(RoleConstants.ADMIN)
 * @Authorize({RoleConstants.ADMIN, RoleConstants.USER})
 * @Authorize(value = {RoleConstants.ADMIN, RoleConstants.AUDITOR}, logical = Authorize.Logical.AND)
 *
 * // 不推荐：硬编码字符串
 * @Authorize("ROLE_ADMIN")
 * }</pre>
 *
 * <p>应用层可仿照此类定义自己的角色常量，字符串值须以 {@code ROLE_} 为前缀。
 */
public final class RoleConstants {

    private RoleConstants() {}

    /** 管理员，最高权限 */
    public static final String ADMIN = "ROLE_ADMIN";

    /** 普通用户 */
    public static final String USER = "ROLE_USER";

    /** 数据库管理员（权限介于 ADMIN 与 USER 之间） */
    public static final String DBA = "ROLE_DBA";
}
