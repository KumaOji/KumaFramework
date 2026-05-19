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

    // ── 服务管理模块 ─────────────────────────────────────────────

    /** 服务管理所有权限（用于管理员角色关联） */
    public static final String SERVICE_ALL    = "service:*";
    /** 管理外部服务进程（LibreTranslate 等）的启停与状态查询 */
    public static final String SERVICE_MANAGE = "service:manage";

    // ── 工具模块 ──────────────────────────────────────────────────

    /** 工具模块所有权限（用于管理员角色关联） */
    public static final String TOOL_ALL = "tool:*";
    /** 使用工具接口（RePKG 文件提取等） */
    public static final String TOOL_USE = "tool:use";

    // ── 项目模块 ──────────────────────────────────────────────────

    /** 项目模块所有权限（用于管理员角色关联） */
    public static final String PROJECT_ALL    = "project:*";
    public static final String PROJECT_CREATE = "project:create";
    public static final String PROJECT_UPDATE = "project:update";
    public static final String PROJECT_DELETE = "project:delete";
    public static final String PROJECT_READ   = "project:read";

    // ── 留言板模块 ───────────────────────────────────────────────

    /** 留言板模块所有权限（用于管理员角色关联） */
    public static final String MESSAGE_ALL    = "message:*";
    /** 审核留言（通过 / 驳回） */
    public static final String MESSAGE_AUDIT  = "message:audit";
    /** 删除留言 */
    public static final String MESSAGE_DELETE = "message:delete";

    // ── 友链模块 ─────────────────────────────────────────────────

    /** 友链模块所有权限（用于管理员角色关联） */
    public static final String FRIEND_LINK_ALL    = "friend_link:*";
    public static final String FRIEND_LINK_CREATE = "friend_link:create";
    public static final String FRIEND_LINK_UPDATE = "friend_link:update";
    public static final String FRIEND_LINK_DELETE = "friend_link:delete";
    /** 审核友链申请（通过） */
    public static final String FRIEND_LINK_AUDIT  = "friend_link:audit";

    // ── AI / OpenWebUI 模块 ──────────────────────────────────────

    /** OpenWebUI 模块所有权限（用于管理员角色关联） */
    public static final String OPENWEBUI_ALL  = "openwebui:*";
    /** 查询可用模型列表 */
    public static final String OPENWEBUI_READ = "openwebui:read";
    /** 发起对话（含流式推理） */
    public static final String OPENWEBUI_CHAT = "openwebui:chat";
}
