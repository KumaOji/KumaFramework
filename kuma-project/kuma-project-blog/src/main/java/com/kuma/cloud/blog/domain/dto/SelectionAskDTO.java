package com.kuma.cloud.blog.domain.dto;

import lombok.Data;

@Data
public class SelectionAskDTO {
    /** 用户划选的文字 */
    private String selectedText;
    /** 用户的追加提问，留空则默认解释选中内容 */
    private String question;
    /** 页面标题 */
    private String pageTitle;
    /** 面包屑路径，如 "编程语言/Java/构建工具/插件开发" */
    private String pagePath;
    /** 当前页面 URL/路由，用于日志溯源 */
    private String pageUrl;
    /** 文章 ID，可选，用于 RAG 全文检索 */
    private Long articleId;
    /** 选中内容的前文（约 150 字） */
    private String contextBefore;
    /** 选中内容的后文（约 150 字） */
    private String contextAfter;
    /** 模型名称（可选） */
    private String model;
    /** 会话 ID（可选，设置后支持多轮追问） */
    private String sessionId;
}
