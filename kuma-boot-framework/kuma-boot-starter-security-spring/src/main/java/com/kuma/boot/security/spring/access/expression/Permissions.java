package com.kuma.boot.security.spring.access.expression;

/**
 * 细粒度权限码常量，格式为 {@code module:action}，配合 {@link Authorize} 使用。
 *
 * <p>与 {@link RoleConstants} 的区别：
 * <ul>
 *   <li>{@link RoleConstants}：角色级别，粗粒度，表示"用户是什么"（ROLE_ADMIN、ROLE_USER）</li>
 *   <li>{@link Permissions}：权限级别，细粒度，表示"用户能做什么"（article:create）</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 单权限
 * @Authorize(Permissions.ARTICLE_CREATE)
 *
 * // 多权限 OR：有任意一个即可
 * @Authorize({Permissions.ARTICLE_UPDATE, Permissions.ARTICLE_CREATE})
 *
 * // 多权限 AND：必须同时拥有
 * @Authorize(value = {Permissions.ARTICLE_CREATE, Permissions.MUSIC_UPLOAD},
 *            logical = Authorize.Logical.AND)
 * }</pre>
 *
 * <p>通配符规则（由 {@link AuthorizeCheckService} 实现）：
 * <ul>
 *   <li>拥有 {@code article:*} → 自动获得所有 {@code article:xxx} 权限</li>
 *   <li>拥有 {@code *} → 超级权限，通过所有校验</li>
 * </ul>
 *
 * <p>应用层可参照此类定义业务特有的权限码，无需修改框架。
 */
public final class Permissions {

    private Permissions() {}

    // ── 通配符 ────────────────────────────────────────────────

    /** 超级权限，拥有此权限将通过所有校验 */
    public static final String ALL = "*";

    // ── 文章模块 ────────────────────────────────────────────────

    /** 文章模块所有权限 */
    public static final String ARTICLE_ALL    = "article:*";
    public static final String ARTICLE_CREATE = "article:create";
    public static final String ARTICLE_UPDATE = "article:update";
    public static final String ARTICLE_DELETE = "article:delete";
    public static final String ARTICLE_READ   = "article:read";

    // ── 音乐模块 ────────────────────────────────────────────────

    /** 音乐模块所有权限 */
    public static final String MUSIC_ALL    = "music:*";
    public static final String MUSIC_UPLOAD = "music:upload";
    public static final String MUSIC_DELETE = "music:delete";
    public static final String MUSIC_READ   = "music:read";

    // ── 系统模块 ────────────────────────────────────────────────

    /** 系统模块所有权限 */
    public static final String SYSTEM_ALL    = "system:*";
    public static final String SYSTEM_CONFIG = "system:config";
    public static final String SYSTEM_LOG    = "system:log";
    public static final String SYSTEM_USER   = "system:user";
}
