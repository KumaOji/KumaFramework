package com.kuma.cloud.blog.security;

import com.kuma.boot.security.spring.access.expression.Authorize;

/**
 * 博客业务权限码，格式 {@code module:action}，配合 {@link Authorize} 使用。
 *
 * <p>三类用户的权限分配策略：
 * <ul>
 *   <li><b>管理员</b>：在 {@code sys_role_permission} 中关联所有模块通配符（{@code article:*} 等）</li>
 *   <li><b>普通用户</b>：固定拥有只读权限（{@code article:read}、{@code music:read}）</li>
 *   <li><b>授权用户</b>：在 {@code sys_user_permission} 中按人单独分配具体权限码</li>
 * </ul>
 */
public final class BlogPermissions {

    private BlogPermissions() {}

    // ── 文章模块 ────────────────────────────────────────────────

    /** 文章模块所有权限（用于管理员角色关联） */
    public static final String ARTICLE_ALL    = "article:*";
    public static final String ARTICLE_CREATE = "article:create";
    public static final String ARTICLE_UPDATE = "article:update";
    public static final String ARTICLE_DELETE = "article:delete";
    public static final String ARTICLE_READ   = "article:read";

    // ── 音乐模块 ────────────────────────────────────────────────

    /** 音乐模块所有权限（用于管理员角色关联） */
    public static final String MUSIC_ALL    = "music:*";
    public static final String MUSIC_UPLOAD = "music:upload";
    public static final String MUSIC_DELETE = "music:delete";
    public static final String MUSIC_READ   = "music:read";

    // ── 系统模块 ────────────────────────────────────────────────

    /** 系统模块所有权限（用于管理员角色关联） */
    public static final String SYSTEM_ALL    = "system:*";
    public static final String SYSTEM_CONFIG = "system:config";
    public static final String SYSTEM_LOG    = "system:log";
    public static final String SYSTEM_USER   = "system:user";
}
