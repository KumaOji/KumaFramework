package com.kuma.boot.ai.service.impl;

import com.kuma.boot.ai.model.AiChatRequest;
import com.kuma.boot.common.utils.json.JacksonUtils;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

final class AiChatHelper {

    private AiChatHelper() {}

    /** 从 DTO 消息列表构造 ChatRequest（无上下文） */
    static ChatRequest buildChatRequest(List<AiChatRequest.Message> raw, String model) {
        return buildChatRequest(raw, model, "");
    }

    /** 从 DTO 消息列表构造 ChatRequest，注入 RAG 上下文作为首条 SystemMessage */
    static ChatRequest buildChatRequest(List<AiChatRequest.Message> raw, String model, String context) {
        List<ChatMessage> messages = new ArrayList<>(toMessages(raw));
        if (context != null && !context.isBlank()) {
            messages.add(0, SystemMessage.from("参考以下内容回答问题：\n\n" + context));
        }
        return buildFromMessages(messages, model);
    }

    /** 直接从 ChatMessage 列表构造 ChatRequest（供 Memory 模式使用） */
    static ChatRequest buildFromMessages(List<ChatMessage> messages, String model) {
        return ChatRequest.builder()
                .messages(messages)
                .parameters(ChatRequestParameters.builder().modelName(model).build())
                .build();
    }

    /**
     * 将 RAG 上下文注入为首条 SystemMessage，用于 Memory 模式下的 RAG 增强。
     * 若 context 为空则原样返回消息列表。
     */
    static List<ChatMessage> prependContext(List<ChatMessage> messages, String context) {
        if (context == null || context.isBlank()) return messages;
        List<ChatMessage> result = new ArrayList<>();
        result.add(SystemMessage.from("参考以下内容回答问题：\n\n" + context));
        result.addAll(messages);
        return result;
    }

    static List<ChatMessage> toMessages(List<AiChatRequest.Message> raw) {
        if (raw == null) return List.of();
        return raw.stream().<ChatMessage>map(m -> switch (m.getRole()) {
            case "system"    -> SystemMessage.from(m.getContent());
            case "assistant" -> AiMessage.from(m.getContent());
            default          -> UserMessage.from(m.getContent());
        }).toList();
    }

    static String extractLastUserMessage(List<AiChatRequest.Message> messages) {
        if (messages == null) return "";
        return Stream.iterate(messages.size() - 1, i -> i >= 0, i -> i - 1)
                .map(messages::get)
                .filter(m -> "user".equals(m.getRole()))
                .map(AiChatRequest.Message::getContent)
                .findFirst()
                .orElse("");
    }

    static Map<String, Object> buildCompletionResponse(String model, String content) {
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

    static String buildStreamChunk(String model, String token) {
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
