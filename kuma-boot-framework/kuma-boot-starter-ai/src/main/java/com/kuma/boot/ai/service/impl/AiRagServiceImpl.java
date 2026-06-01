package com.kuma.boot.ai.service.impl;

import com.kuma.boot.ai.model.AiChatRequest;
import com.kuma.boot.ai.service.AiRagService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.output.TokenUsage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class AiRagServiceImpl implements AiRagService {

    private static final Logger log = LoggerFactory.getLogger(AiRagServiceImpl.class);

    private final ChatModel chatModel;
    private final StreamingChatModel streamingModel;
    private final String defaultModel;
    private final RagComponent ragComponent;
    private final int maxMessages;
    private final Executor asyncExecutor;

    /** sessionId → 对话历史（RAG 模式下同样支持多轮记忆） */
    private final Map<String, ChatMemory> memories = new ConcurrentHashMap<>();

    public AiRagServiceImpl(ChatModel chatModel, StreamingChatModel streamingModel,
                             String defaultModel, RagComponent ragComponent,
                             int maxMessages, Executor asyncExecutor) {
        this.chatModel = chatModel;
        this.streamingModel = streamingModel;
        this.defaultModel = defaultModel;
        this.ragComponent = ragComponent;
        this.maxMessages = maxMessages;
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public int ingest(String text) {
        return ragComponent.ingest(text);
    }

    @Override
    public int ingestMarkdown(String filename, String markdown) {
        return ragComponent.ingestMarkdown(filename, markdown);
    }

    @Override
    public int ingestFile(String filename, java.io.InputStream inputStream) {
        return ragComponent.ingestStream(filename, inputStream);
    }

    @Override
    public Map<String, Object> chat(AiChatRequest request) {
        String model = resolve(request.getModel());
        String userQuery = AiChatHelper.extractLastUserMessage(request.getMessages());
        String context = ragComponent.retrieveContext(userQuery);

        ChatRequest chatRequest;
        if (hasSession(request)) {
            ChatMemory memory = getMemory(request.getSessionId());
            if (!userQuery.isBlank()) memory.add(UserMessage.from(userQuery));
            // 将 RAG 上下文注入 SystemMessage 前缀
            chatRequest = AiChatHelper.buildFromMessages(
                    AiChatHelper.prependContext(memory.messages(), context), model);
        } else {
            chatRequest = AiChatHelper.buildChatRequest(request.getMessages(), model, context);
        }

        ChatResponse response = chatModel.chat(chatRequest);
        logTokens(response.tokenUsage());
        if (hasSession(request)) {
            getMemory(request.getSessionId()).add(response.aiMessage());
        }
        return AiChatHelper.buildCompletionResponse(model, response.aiMessage().text());
    }

    @Override
    public SseEmitter streamChat(AiChatRequest request) {
        String model = resolve(request.getModel());
        String userQuery = AiChatHelper.extractLastUserMessage(request.getMessages());
        String context = ragComponent.retrieveContext(userQuery);
        String sessionId = request.getSessionId();

        ChatRequest chatRequest;
        if (hasSession(request)) {
            ChatMemory memory = getMemory(sessionId);
            if (!userQuery.isBlank()) memory.add(UserMessage.from(userQuery));
            chatRequest = AiChatHelper.buildFromMessages(
                    AiChatHelper.prependContext(memory.messages(), context), model);
        } else {
            chatRequest = AiChatHelper.buildChatRequest(request.getMessages(), model, context);
        }

        SseEmitter emitter = new SseEmitter(600_000L);

        asyncExecutor.execute(() -> streamingModel.chat(chatRequest, new StreamingChatResponseHandler() {
            private final StringBuilder buffer = new StringBuilder();

            @Override
            public void onPartialResponse(String token) {
                buffer.append(token);
                try {
                    emitter.send(SseEmitter.event().data(AiChatHelper.buildStreamChunk(model, token)));
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }
            }

            @Override
            public void onCompleteResponse(ChatResponse response) {
                logTokens(response.tokenUsage());
                if (sessionId != null && !sessionId.isBlank()) {
                    getMemory(sessionId).add(AiMessage.from(buffer.toString()));
                }
                try {
                    emitter.send(SseEmitter.event().data("[DONE]"));
                    emitter.complete();
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }
            }

            @Override
            public void onError(Throwable error) {
                log.error("RAG 流式推理异常: {}", error.getMessage(), error);
                emitter.completeWithError(error);
            }
        }));

        return emitter;
    }

    @Override
    public void clearMemory(String sessionId) {
        memories.remove(sessionId);
    }

    // ── 私有方法 ─────────────────────────────────────────────────────────────

    private boolean hasSession(AiChatRequest request) {
        return request.getSessionId() != null && !request.getSessionId().isBlank();
    }

    private ChatMemory getMemory(String sessionId) {
        return memories.computeIfAbsent(sessionId,
                id -> MessageWindowChatMemory.withMaxMessages(maxMessages));
    }

    private static void logTokens(TokenUsage usage) {
        if (usage != null) {
            log.debug("token usage — input={} output={} total={}",
                    usage.inputTokenCount(), usage.outputTokenCount(), usage.totalTokenCount());
        }
    }

    private String resolve(String requested) {
        return (requested != null && !requested.isBlank()) ? requested : defaultModel;
    }
}
