package com.kuma.boot.ai.service;

import com.kuma.boot.ai.model.AiChatRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface AiRagService {

    /** 将文本分段后写入向量库 */
    void ingest(String text);

    /** RAG 增强的非流式对话 */
    Map<String, Object> chat(AiChatRequest request);

    /** RAG 增强的流式对话（SSE） */
    SseEmitter streamChat(AiChatRequest request);
}
