package com.kuma.boot.ai.service.impl;

import com.kuma.boot.ai.model.AiChatRequest;
import com.kuma.boot.ai.service.AiRagService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.Executor;

public class AiRagServiceImpl implements AiRagService {

    private static final Logger log = LoggerFactory.getLogger(AiRagServiceImpl.class);

    private final ChatModel chatModel;
    private final StreamingChatModel streamingModel;
    private final String defaultModel;
    private final RagComponent ragComponent;
    private final Executor asyncExecutor;

    public AiRagServiceImpl(ChatModel chatModel, StreamingChatModel streamingModel,
                             String defaultModel, RagComponent ragComponent, Executor asyncExecutor) {
        this.chatModel = chatModel;
        this.streamingModel = streamingModel;
        this.defaultModel = defaultModel;
        this.ragComponent = ragComponent;
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public void ingest(String text) {
        ragComponent.ingest(text);
    }

    @Override
    public Map<String, Object> chat(AiChatRequest request) {
        String model = resolve(request.getModel());
        String context = ragComponent.retrieveContext(AiChatHelper.extractLastUserMessage(request.getMessages()), 3);
        ChatRequest chatRequest = AiChatHelper.buildChatRequest(request.getMessages(), model, context);
        ChatResponse response = chatModel.chat(chatRequest);
        return AiChatHelper.buildCompletionResponse(model, response.aiMessage().text());
    }

    @Override
    public SseEmitter streamChat(AiChatRequest request) {
        String model = resolve(request.getModel());
        String context = ragComponent.retrieveContext(AiChatHelper.extractLastUserMessage(request.getMessages()), 3);
        ChatRequest chatRequest = AiChatHelper.buildChatRequest(request.getMessages(), model, context);
        SseEmitter emitter = new SseEmitter(600_000L);

        asyncExecutor.execute(() -> streamingModel.chat(chatRequest, new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String token) {
                try {
                    emitter.send(SseEmitter.event().data(AiChatHelper.buildStreamChunk(model, token)));
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }
            }

            @Override
            public void onCompleteResponse(ChatResponse ignored) {
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

    private String resolve(String requested) {
        return (requested != null && !requested.isBlank()) ? requested : defaultModel;
    }
}
