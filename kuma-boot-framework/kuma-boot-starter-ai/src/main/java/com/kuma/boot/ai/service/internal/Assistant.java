package com.kuma.boot.ai.service.internal;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

/**
 * 声明式 AI 助手接口，由 {@code AiServices} 在运行时生成代理实现。
 *
 * <p>该接口本身不写任何实现代码，LangChain4j 自动完成：
 * <ul>
 *   <li><b>对话记忆</b>：按 {@link MemoryId} 隔离，自动拼接历史消息</li>
 *   <li><b>RAG 检索</b>：构建时若绑定 ContentRetriever，自动检索并注入上下文</li>
 *   <li><b>工具调用</b>：构建时若绑定 @Tool 对象，自动完成 Function Calling 多轮编排</li>
 * </ul>
 */
public interface Assistant {

    /** 非流式对话 */
    String chat(@MemoryId String sessionId, @UserMessage String message);

    /** 流式对话，返回 TokenStream（逐 token 回调） */
    TokenStream chatStream(@MemoryId String sessionId, @UserMessage String message);
}
