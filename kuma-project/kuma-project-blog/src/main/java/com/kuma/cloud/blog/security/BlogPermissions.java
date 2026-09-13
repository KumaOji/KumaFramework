package com.kuma.cloud.blog.security;

import com.kuma.boot.security.spring.access.expression.Authorize;

import java.util.List;

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

    // ── AI 对话模块 ──────────────────────────────────────────────

    /** AI 对话模块所有权限（用于管理员角色关联） */
    public static final String AI_CHAT_ALL  = "ai_chat:*";
    /** 发起对话（含流式推理） */
    public static final String AI_CHAT_SEND = "ai_chat:send";
    /** 向知识库写入文档（仅管理员） */
    public static final String AI_CHAT_INGEST = "ai_chat:ingest";
    /** RAG 增强问答（仅管理员） */
    public static final String AI_CHAT_RAG = "ai_chat:rag";
    /** 使用文本工具（摘要、翻译、关键词、情感分析等） */
    public static final String AI_CHAT_TEXT = "ai_chat:text";

    // ── 聊天室模块 ───────────────────────────────────────────────

    /** 聊天室模块所有权限（用于管理员角色关联） */
    public static final String CHAT_ALL       = "chat:*";
    public static final String CHAT_CREATE    = "chat:create";
    public static final String CHAT_UPDATE    = "chat:update";
    public static final String CHAT_DELETE    = "chat:delete";
    public static final String CHAT_BLACKLIST = "chat:blacklist";
    /** 清理 / 删除聊天历史记录 */
    public static final String CHAT_HISTORY   = "chat:history";

    public record PermissionDef(String code, String name, String module, String description) {}

    /**
     * 权限清单（含知识库），启动时同步到 {@code sys_permission}，并默认授予 ROLE_ADMIN。
     */
    public static List<PermissionDef> catalog() {
        return List.of(
                def(ARTICLE_ALL, "文章全部权限", "article", "文章模块所有权限"),
                def(ARTICLE_CREATE, "创建文章", "article", "发布新文章"),
                def(ARTICLE_UPDATE, "编辑文章", "article", "修改已有文章"),
                def(ARTICLE_DELETE, "删除文章", "article", "删除文章"),
                def(ARTICLE_READ, "查看文章", "article", "查看文章"),
                def(MUSIC_ALL, "音乐全部权限", "music", "音乐模块所有权限"),
                def(MUSIC_UPLOAD, "上传音乐", "music", "上传音乐文件"),
                def(MUSIC_DELETE, "删除音乐", "music", "删除音乐"),
                def(MUSIC_READ, "播放/查看音乐", "music", "播放与查看音乐"),
                def(SYSTEM_ALL, "系统全部权限", "system", "系统模块所有权限"),
                def(SYSTEM_CONFIG, "系统配置", "system", "修改系统配置"),
                def(SYSTEM_LOG, "查看日志", "system", "查看系统日志"),
                def(SYSTEM_USER, "用户管理", "system", "管理用户与授权"),
                def(PROJECT_ALL, "项目全部权限", "project", "项目模块所有权限"),
                def(PROJECT_CREATE, "创建项目", "project", "新增项目"),
                def(PROJECT_UPDATE, "编辑项目", "project", "修改项目"),
                def(PROJECT_DELETE, "删除项目", "project", "删除项目"),
                def(PROJECT_READ, "查看项目", "project", "浏览项目"),
                def(MESSAGE_ALL, "留言板全部权限", "message", "留言板所有权限"),
                def(MESSAGE_AUDIT, "审核留言", "message", "通过或驳回留言"),
                def(MESSAGE_DELETE, "删除留言", "message", "删除留言"),
                def(FRIEND_LINK_ALL, "友链全部权限", "friend_link", "友链模块所有权限"),
                def(FRIEND_LINK_CREATE, "创建友链", "friend_link", "新增友链"),
                def(FRIEND_LINK_UPDATE, "编辑友链", "friend_link", "修改友链"),
                def(FRIEND_LINK_DELETE, "删除友链", "friend_link", "删除友链"),
                def(FRIEND_LINK_AUDIT, "审核友链申请", "friend_link", "通过友链申请"),
                def(AI_CHAT_ALL, "AI对话全部权限", "ai_chat", "对话、知识库与文本工具全部权限"),
                def(AI_CHAT_SEND, "发起对话", "ai_chat", "普通对话与流式推理"),
                def(AI_CHAT_INGEST, "知识库入库", "ai_chat", "上传文档到向量知识库、查看来源、检索测试"),
                def(AI_CHAT_RAG, "知识库问答", "ai_chat", "RAG 增强对话与会话管理"),
                def(AI_CHAT_TEXT, "文本工具", "ai_chat", "摘要、翻译、关键词、情感分析"),
                def(CHAT_ALL, "聊天室全部权限", "chat", "聊天室模块所有权限"),
                def(CHAT_CREATE, "创建聊天室", "chat", "新建聊天室"),
                def(CHAT_UPDATE, "编辑聊天室", "chat", "修改聊天室"),
                def(CHAT_DELETE, "删除聊天室", "chat", "删除聊天室"),
                def(CHAT_BLACKLIST, "聊天室黑名单", "chat", "管理聊天黑名单"),
                def(CHAT_HISTORY, "聊天历史", "chat", "清理或删除聊天记录"));
    }

    public static List<String> allCodes() {
        return catalog().stream().map(PermissionDef::code).toList();
    }

    private static PermissionDef def(String code, String name, String module, String description) {
        return new PermissionDef(code, name, module, description);
    }
}
