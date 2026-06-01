package com.kuma.boot.ai.service;

import com.kuma.boot.ai.model.AiChatRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.InputStream;
import java.util.Map;

public interface AiRagService {

    /** 将文本分段后写入向量库，返回写入段落数 */
    int ingest(String text);

    /** 将 Markdown 文件按标题切段后写入向量库，返回写入段落数 */
    int ingestMarkdown(String filename, String markdown);

    /** 通过 Tika 解析任意格式文件（PDF/Word/PPT/Excel/HTML 等）后写入向量库，返回写入段落数 */
    int ingestFile(String filename, InputStream inputStream);

    /** RAG 增强的非流式对话 */
    Map<String, Object> chat(AiChatRequest request);

    /** RAG 增强的流式对话（SSE） */
    SseEmitter streamChat(AiChatRequest request);

    /** 清除指定 session 的对话历史 */
    void clearMemory(String sessionId);
}
