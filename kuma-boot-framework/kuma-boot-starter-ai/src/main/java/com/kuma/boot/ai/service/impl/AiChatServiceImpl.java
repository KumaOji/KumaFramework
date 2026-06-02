package com.kuma.boot.ai.service.impl;

import com.kuma.boot.ai.model.AiChatRequest;
import com.kuma.boot.ai.service.AiChatService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class AiChatServiceImpl implements AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatServiceImpl.class);

    private final ChatModel chatModel;
    private final StreamingChatModel streamingModel;
    private final String defaultModel;
    private final int maxMessages;
    private final Executor asyncExecutor;

    /** sessionId → 对话历史 */
    private final Map<String, ChatMemory> memories = new ConcurrentHashMap<>();

    public AiChatServiceImpl(ChatModel chatModel, StreamingChatModel streamingModel,
                              String defaultModel, int maxMessages, Executor asyncExecutor) {
        this.chatModel = chatModel;
        this.streamingModel = streamingModel;
        this.defaultModel = defaultModel;
        this.maxMessages = maxMessages;
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public Map<String, Object> chat(AiChatRequest request) {
        String model = resolve(request.getModel());
        ChatRequest chatRequest = buildRequest(request, model);
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
        ChatRequest chatRequest = buildRequest(request, model);
        SseEmitter emitter = new SseEmitter(600_000L);
        String sessionId = request.getSessionId();

        asyncExecutor.execute(() -> streamingModel.chat(chatRequest, new StreamingChatResponseHandler() {
            private final StringBuilder buffer = new StringBuilder();
            private volatile boolean done = false;

            @Override
            public void onPartialResponse(String token) {
                if (done) return;
                buffer.append(token);
                try {
                    emitter.send(SseEmitter.event().data(AiChatHelper.buildStreamChunk(model, token)));
                } catch (Exception e) {
                    done = true;
                    log.debug("流式发送中断（客户端可能已断开）: {}", e.getMessage());
                    emitter.complete();
                }
            }

            @Override
            public void onCompleteResponse(ChatResponse response) {
                if (done) return;
                done = true;
                logTokens(response.tokenUsage());
                if (sessionId != null && !sessionId.isBlank()) {
                    getMemory(sessionId).add(AiMessage.from(buffer.toString()));
                }
                try {
                    emitter.send(SseEmitter.event().data("[DONE]"));
                    emitter.complete();
                } catch (Exception e) {
                    emitter.complete();
                }
            }

            @Override
            public void onError(Throwable error) {
                if (done) return;
                done = true;
                if (isClientDisconnect(error)) {
                    log.debug("客户端断开连接，流式推理终止");
                } else {
                    log.error("流式推理异常: {}", error.getMessage(), error);
                    try {
                        emitter.send(SseEmitter.event().name("error").data("[ERROR]"));
                    } catch (Exception ignored) {
                    }
                }
                emitter.complete();
            }
        }));

        return emitter;
    }

    private static boolean isClientDisconnect(Throwable t) {
        if (t == null) return false;
        if (t instanceof java.nio.channels.ClosedChannelException) return true;
        if (t instanceof java.io.IOException) {
            String msg = t.getMessage();
            if (msg != null && (msg.contains("Broken pipe") || msg.contains("Connection reset")
                    || msg.contains("远程主机强迫关闭") || msg.contains("connection was reset"))) {
                return true;
            }
        }
        return isClientDisconnect(t.getCause());
    }

    @Override
    public void clearMemory(String sessionId) {
        memories.remove(sessionId);
    }

    // ── 私有方法 ─────────────────────────────────────────────────────────────

    private ChatRequest buildRequest(AiChatRequest request, String model) {
        if (hasSession(request)) {
            ChatMemory memory = getMemory(request.getSessionId());
            // 仅取请求中最后一条用户消息加入记忆
            String userText = AiChatHelper.extractLastUserMessage(request.getMessages());
            if (!userText.isBlank()) {
                memory.add(UserMessage.from(userText));
            }
            List<ChatMessage> history = memory.messages();
            return AiChatHelper.buildFromMessages(history, model);
        }
        return AiChatHelper.buildChatRequest(request.getMessages(), model);
    }

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
