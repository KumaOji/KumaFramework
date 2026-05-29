package com.kuma.boot.ai.service.impl;

import com.kuma.boot.ai.autoconfigure.properties.AiChatProperties;
import com.kuma.boot.ai.model.AiChatRequest;
import com.kuma.boot.ai.service.AiChatService;
import com.kuma.boot.common.utils.json.JacksonUtils;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

public class AiChatServiceImpl implements AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatServiceImpl.class);

    private final ChatModel chatModel;
    private final StreamingChatModel streamingModel;
    private final String defaultModel;
    private final Executor asyncExecutor;
    private final RagComponent ragComponent;

    public AiChatServiceImpl(AiChatProperties properties, Executor asyncExecutor, RagComponent ragComponent) {
        this.defaultModel = properties.getModel();
        this.asyncExecutor = asyncExecutor;
        this.ragComponent = ragComponent;

        String baseUrl = properties.getBaseUrl();
        if (baseUrl.endsWith("/")) baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        String effectiveKey = properties.getApiKey().isBlank() ? "no-key" : properties.getApiKey();

        this.chatModel = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(effectiveKey)
                .modelName(defaultModel)
                .logRequests(false)
                .logResponses(false)
                .build();

        this.streamingModel = OpenAiStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(effectiveKey)
                .modelName(defaultModel)
                .logRequests(false)
                .logResponses(false)
                .build();
    }

    @Override
    public Map<String, Object> chat(AiChatRequest request) {
        String model = resolveModel(request.getModel());
        ChatRequest chatRequest = buildChatRequest(request.getMessages(), model);
        ChatResponse response = chatModel.chat(chatRequest);
        return buildCompletionResponse(model, response.aiMessage().text());
    }

    @Override
    public SseEmitter streamChat(AiChatRequest request) {
        String model = resolveModel(request.getModel());
        ChatRequest chatRequest = buildChatRequest(request.getMessages(), model);
        SseEmitter emitter = new SseEmitter(600_000L);

        asyncExecutor.execute(() -> streamingModel.chat(chatRequest, new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String token) {
                try {
                    emitter.send(SseEmitter.event().data(buildStreamChunk(model, token)));
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
                log.error("流式推理异常: {}", error.getMessage(), error);
                emitter.completeWithError(error);
            }
        }));

        return emitter;
    }

    @Override
    public Map<String, Object> ragChat(AiChatRequest request) {
        String model = resolveModel(request.getModel());
        String context = ragComponent.retrieveContext(extractLastUserMessage(request.getMessages()), 3);
        ChatRequest chatRequest = buildChatRequest(request.getMessages(), model, context);
        ChatResponse response = chatModel.chat(chatRequest);
        return buildCompletionResponse(model, response.aiMessage().text());
    }

    @Override
    public SseEmitter ragStreamChat(AiChatRequest request) {
        String model = resolveModel(request.getModel());
        String context = ragComponent.retrieveContext(extractLastUserMessage(request.getMessages()), 3);
        ChatRequest chatRequest = buildChatRequest(request.getMessages(), model, context);
        SseEmitter emitter = new SseEmitter(600_000L);

        asyncExecutor.execute(() -> streamingModel.chat(chatRequest, new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String token) {
                try {
                    emitter.send(SseEmitter.event().data(buildStreamChunk(model, token)));
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

    // ── helpers ──────────────────────────────────────────────────────────────

    private String resolveModel(String requested) {
        return (requested != null && !requested.isBlank()) ? requested : defaultModel;
    }

    private ChatRequest buildChatRequest(List<AiChatRequest.Message> raw, String model) {
        return buildChatRequest(raw, model, "");
    }

    private ChatRequest buildChatRequest(List<AiChatRequest.Message> raw, String model, String context) {
        List<ChatMessage> messages = new ArrayList<>(toMessages(raw));
        if (!context.isBlank()) {
            messages.add(0, SystemMessage.from("参考以下内容回答问题：\n\n" + context));
        }
        return ChatRequest.builder()
                .messages(messages)
                .parameters(ChatRequestParameters.builder().modelName(model).build())
                .build();
    }

    private String extractLastUserMessage(List<AiChatRequest.Message> messages) {
        if (messages == null) return "";
        return Stream.iterate(messages.size() - 1, i -> i >= 0, i -> i - 1)
                .map(messages::get)
                .filter(m -> "user".equals(m.getRole()))
                .map(AiChatRequest.Message::getContent)
                .findFirst()
                .orElse("");
    }

    private List<ChatMessage> toMessages(List<AiChatRequest.Message> raw) {
        if (raw == null) return List.of();
        return raw.stream().<ChatMessage>map(m -> switch (m.getRole()) {
            case "system"    -> SystemMessage.from(m.getContent());
            case "assistant" -> AiMessage.from(m.getContent());
            default          -> UserMessage.from(m.getContent());
        }).toList();
    }

    private Map<String, Object> buildCompletionResponse(String model, String content) {
        Map<String, Object> message = Map.of("role", "assistant", "content", content);
        Map<String, Object> choice  = Map.of("index", 0, "message", message, "finish_reason", "stop");
        Map<String, Object> result  = new LinkedHashMap<>();
        result.put("id",      "chatcmpl-" + UUID.randomUUID().toString().replace("-", ""));
        result.put("object",  "chat.completion");
        result.put("created", Instant.now().getEpochSecond());
        result.put("model",   model);
        result.put("choices", List.of(choice));
        return result;
    }

    private String buildStreamChunk(String model, String token) {
        Map<String, Object> delta  = Map.of("content", token);
        Map<String, Object> choice = Map.of("index", 0, "delta", delta);
        Map<String, Object> chunk  = new LinkedHashMap<>();
        chunk.put("id",      "chatcmpl-stream");
        chunk.put("object",  "chat.completion.chunk");
        chunk.put("created", Instant.now().getEpochSecond());
        chunk.put("model",   model);
        chunk.put("choices", List.of(choice));
        return JacksonUtils.toJSONString(chunk);
    }
}
