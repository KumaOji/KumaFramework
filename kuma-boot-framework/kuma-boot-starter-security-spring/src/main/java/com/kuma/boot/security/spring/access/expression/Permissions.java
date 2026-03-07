package com.kuma.boot.security.spring.access.expression;

/**
 * 框架级权限常量基类，仅定义通配符规则。
 *
 * <p>权限码格式约定：{@code module:action}，支持三级匹配（由 {@link AuthorizeCheckService} 实现）：
 * <ol>
 *   <li>精确匹配：用户有 {@code article:create} → 通过 {@code article:create}</li>
 *   <li>模块通配符：用户有 {@code article:*} → 通过所有 {@code article:xxx}</li>
 *   <li>超级权限：用户有 {@code *} → 通过所有校验</li>
 * </ol>
 *
 * <p><b>应用层扩展方式：</b>在自己的项目中定义业务权限常量，不要修改此类。
 * <pre>{@code
 * public final class BlogPermissions {
 *     public static final String ARTICLE_CREATE = "article:create";
 *     public static final String MUSIC_UPLOAD   = "music:upload";
 * }
 * }</pre>
 */
public final class Permissions {

    private Permissions() {}

    /** 超级权限，拥有此权限将通过所有 {@link Authorize} 校验 */
    public static final String ALL = "*";
}
