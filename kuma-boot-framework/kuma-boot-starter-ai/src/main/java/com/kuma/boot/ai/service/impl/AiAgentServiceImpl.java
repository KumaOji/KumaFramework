package com.kuma.boot.ai.service.impl;

import com.kuma.boot.ai.autoconfigure.properties.AiChatProperties;
import com.kuma.boot.ai.model.AiAgentRequest;
import com.kuma.boot.ai.service.AiAgentService;
import com.kuma.boot.ai.service.internal.Assistant;
import com.kuma.boot.ai.service.tool.AiToolProvider;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于 {@code AiServices} 的智能体实现。
 * 构造时一次性组装好「记忆 + 检索 + 工具」三大能力，运行时由 LangChain4j 自动编排。
 */
public class AiAgentServiceImpl implements AiAgentService {

    private static final Logger log = LoggerFactory.getLogger(AiAgentServiceImpl.class);
    private static final String DEFAULT_SESSION = "default";

    private final Assistant assistant;
    private final String defaultModel;

    /** sessionId → 对话记忆，供 AiServices 的 chatMemoryProvider 回调使用，并支持手动清除 */
    private final Map<String, ChatMemory> memories = new ConcurrentHashMap<>();

    public AiAgentServiceImpl(ChatModel chatModel, StreamingChatModel streamingModel,
                              AiChatProperties props, RagComponent ragComponent,
                              List<AiToolProvider> toolProviders) {
        this.defaultModel = props.getModel();
        AiChatProperties.Agent agent = props.getAgent();
        int maxMessages = props.getMemory().getMaxMessages();

        AiServices<Assistant> builder = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingModel)
                .systemMessageProvider(memoryId -> agent.getSystemPrompt())
                .chatMemoryProvider(memoryId ->
                        memories.computeIfAbsent(String.valueOf(memoryId),
                                id -> MessageWindowChatMemory.withMaxMessages(maxMessages)));

        // RAG：将知识库检索器绑定到 Agent，每轮自动检索并注入上下文
        if (agent.isRagEnabled()) {
            builder.contentRetriever(ragComponent.contentRetriever());
        }

        // 工具：注册所有 AiToolProvider Bean 的 @Tool 方法，启用 Function Calling
        if (agent.isToolsEnabled() && !toolProviders.isEmpty()) {
            List<Object> tools = new ArrayList<>(toolProviders);
            builder.tools(tools);
            log.info("Agent 已注册 {} 个工具提供者", tools.size());
        }

        this.assistant = builder.build();
    }

    @Override
    public Map<String, Object> chat(AiAgentRequest request) {
        String session = resolveSession(request.getSessionId());
        String answer = assistant.chat(session, request.getMessage());
        return AiChatHelper.buildCompletionResponse(defaultModel, answer);
    }

    @Override
    public SseEmitter streamChat(AiAgentRequest request) {
        String session = resolveSession(request.getSessionId());
        SseEmitter emitter = new SseEmitter(600_000L);

        TokenStream stream = assistant.chatStream(session, request.getMessage());
        stream.onPartialResponse(token -> {
                    try {
                        emitter.send(SseEmitter.event().data(AiChatHelper.buildStreamChunk(defaultModel, token)));
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                })
                .onCompleteResponse(response -> {
                    try {
                        emitter.send(SseEmitter.event().data("[DONE]"));
                        emitter.complete();
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                })
                .onError(error -> {
                    log.error("Agent 流式推理异常: {}", error.getMessage(), error);
                    emitter.completeWithError(error);
                })
                .start();

        return emitter;
    }

    @Override
    public void clearMemory(String sessionId) {
        memories.remove(resolveSession(sessionId));
    }

    private static String resolveSession(String sessionId) {
        return (sessionId != null && !sessionId.isBlank()) ? sessionId : DEFAULT_SESSION;
    }
}
