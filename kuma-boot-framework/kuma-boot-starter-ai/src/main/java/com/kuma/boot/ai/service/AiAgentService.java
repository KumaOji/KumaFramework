package com.kuma.boot.ai.service;

import com.kuma.boot.ai.model.AiAgentRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * 智能体（Agent）服务：在一次对话中同时具备多轮记忆、知识库检索（RAG）
 * 与工具调用（Function Calling）。基于 LangChain4j {@code AiServices} 实现。
 */
public interface AiAgentService {

    /** 非流式智能体对话，返回 OpenAI 兼容格式 */
    Map<String, Object> chat(AiAgentRequest request);

    /** 流式智能体对话（SSE） */
    SseEmitter streamChat(AiAgentRequest request);

    /** 清除指定 session 的对话记忆 */
    void clearMemory(String sessionId);
}
