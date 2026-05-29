package com.kuma.boot.ai.service.impl;

import com.kuma.boot.ai.service.AiTextService;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;

import java.util.Arrays;
import java.util.List;

public class AiTextServiceImpl implements AiTextService {

    private final ChatModel chatModel;
    private final String defaultModel;

    public AiTextServiceImpl(ChatModel chatModel, String defaultModel) {
        this.chatModel = chatModel;
        this.defaultModel = defaultModel;
    }

    @Override
    public String summarize(String text, int maxWords) {
        String prompt = "请用不超过" + maxWords + "字总结以下内容，只返回总结内容，不要额外说明：\n\n" + text;
        return call(prompt, defaultModel);
    }

    @Override
    public String translate(String text, String targetLanguage) {
        String prompt = "请将以下内容翻译为" + targetLanguage + "，只返回翻译结果，不要额外说明：\n\n" + text;
        return call(prompt, defaultModel);
    }

    @Override
    public List<String> extractKeywords(String text, int count) {
        String prompt = "请从以下内容中提取" + count + "个最重要的关键词，每行一个关键词，不要编号和额外说明：\n\n" + text;
        String result = call(prompt, defaultModel);
        return Arrays.stream(result.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .limit(count)
                .toList();
    }

    @Override
    public String sentiment(String text) {
        String prompt = "请分析以下内容的情感倾向，只返回 positive、negative 或 neutral 三个词之一，不要任何其他内容：\n\n" + text;
        return call(prompt, defaultModel).trim().toLowerCase();
    }

    @Override
    public String generate(String prompt) {
        return call(prompt, defaultModel);
    }

    @Override
    public String generate(String prompt, String model) {
        String m = (model != null && !model.isBlank()) ? model : defaultModel;
        return call(prompt, m);
    }

    private String call(String prompt, String model) {
        ChatRequest request = ChatRequest.builder()
                .messages(List.of(UserMessage.from(prompt)))
                .parameters(ChatRequestParameters.builder().modelName(model).build())
                .build();
        return chatModel.chat(request).aiMessage().text();
    }
}
