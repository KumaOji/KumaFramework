package com.kuma.cloud.blog.service.impl;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.cloud.blog.domain.vo.AiChatRequest;
import com.kuma.cloud.blog.service.AiChatService;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executor;

@Service
public class AiChatServiceImpl implements AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatServiceImpl.class);

    private final ChatModel chatModel;
    private final StreamingChatModel streamingModel;
    /** 保留 RestClient 仅用于 /api/models —— LangChain4j 不抽象模型列表接口 */
    private final RestClient restClient;
    private final String defaultModel;
    private final Executor asyncExecutor;

    public AiChatServiceImpl(
            @Value("${ai-chat.base-url:http://blog-ai-ui:8080}") String baseUrl,
            @Value("${ai-chat.api-key:}") String apiKey,
            @Value("${ai-chat.model:llama3}") String defaultModel,
            @Qualifier("asyncThreadPoolTaskExecutor") Executor asyncExecutor) {

        this.defaultModel = defaultModel;
        this.asyncExecutor = asyncExecutor;

        String chatBaseUrl = baseUrl.endsWith("/") ? baseUrl + "api" : baseUrl + "/api";
        String effectiveKey = apiKey.isBlank() ? "no-key" : apiKey;

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();

        this.chatModel = OpenAiChatModel.builder()
                .baseUrl(chatBaseUrl)
                .apiKey(effectiveKey)
                .modelName(defaultModel)
                .logRequests(false)
                .logResponses(false)
                .build();

        this.streamingModel = OpenAiStreamingChatModel.builder()
                .baseUrl(chatBaseUrl)
                .apiKey(effectiveKey)
                .modelName(defaultModel)
                .logRequests(false)
                .logResponses(false)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> listModels() {
        return restClient.get()
                .uri("/api/models")
                .retrieve()
                .body(Map.class);
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

    // ── helpers ──────────────────────────────────────────────────────────────

    private String resolveModel(String requested) {
        return (requested != null && !requested.isBlank()) ? requested : defaultModel;
    }

    private ChatRequest buildChatRequest(List<AiChatRequest.Message> raw, String model) {
        return ChatRequest.builder()
                .messages(toMessages(raw))
                .parameters(ChatRequestParameters.builder().modelName(model).build())
                .build();
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
