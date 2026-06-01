package com.kuma.boot.ai.model;

import lombok.Data;

/**
 * Agent 对话请求。Agent 自动具备：多轮记忆（按 sessionId）、知识库检索（RAG）、
 * 工具调用（Function Calling）。
 */
@Data
public class AiAgentRequest {

    /** 会话 ID，用于隔离不同用户/会话的对话记忆；为空时落到共享的 "default" 会话 */
    private String sessionId;

    /** 本轮用户消息 */
    private String message;
}
