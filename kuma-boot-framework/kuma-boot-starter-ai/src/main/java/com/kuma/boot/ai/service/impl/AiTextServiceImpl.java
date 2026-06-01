package com.kuma.boot.ai.service.impl;

import com.kuma.boot.ai.model.Sentiment;
import com.kuma.boot.ai.service.AiTextService;
import com.kuma.boot.ai.service.internal.TextAssistant;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;

import java.util.List;

/**
 * 文本服务实现。
 * <p>摘要 / 翻译 / 关键词 / 情感分析 委托给声明式 {@link TextAssistant}，
 * 借助 AiServices 的结构化输出能力，免去手写解析逻辑；
 * <p>{@code generate} 保留直连 {@link ChatModel} 的路径，以支持按调用动态指定模型。
 */
public class AiTextServiceImpl implements AiTextService {

    private final TextAssistant textAssistant;
    private final ChatModel chatModel;
    private final String defaultModel;

    public AiTextServiceImpl(TextAssistant textAssistant, ChatModel chatModel, String defaultModel) {
        this.textAssistant = textAssistant;
        this.chatModel = chatModel;
        this.defaultModel = defaultModel;
    }

    @Override
    public String summarize(String text, int maxWords) {
        return textAssistant.summarize(text, maxWords);
    }

    @Override
    public String translate(String text, String targetLanguage) {
        return textAssistant.translate(text, targetLanguage);
    }

    @Override
    public List<String> extractKeywords(String text, int count) {
        return textAssistant.extractKeywords(text, count);
    }

    @Override
    public String sentiment(String text) {
        Sentiment sentiment = textAssistant.sentiment(text);
        return sentiment.name().toLowerCase();
    }

    @Override
    public String generate(String prompt) {
        return generate(prompt, null);
    }

    @Override
    public String generate(String prompt, String model) {
        String m = (model != null && !model.isBlank()) ? model : defaultModel;
        ChatRequest request = ChatRequest.builder()
                .messages(List.of(UserMessage.from(prompt)))
                .parameters(ChatRequestParameters.builder().modelName(m).build())
                .build();
        return chatModel.chat(request).aiMessage().text();
    }
}
