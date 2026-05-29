package com.kuma.boot.ai.service;

import java.util.List;

public interface AiTextService {

    /** 摘要，限制字数 */
    String summarize(String text, int maxWords);

    default String summarize(String text) {
        return summarize(text, 200);
    }

    /** 翻译为目标语言，例如 "中文"、"English" */
    String translate(String text, String targetLanguage);

    /** 提取关键词列表 */
    List<String> extractKeywords(String text, int count);

    default List<String> extractKeywords(String text) {
        return extractKeywords(text, 5);
    }

    /** 情感分析，返回 positive / negative / neutral */
    String sentiment(String text);

    /** 自由生成：直接以 prompt 作为用户消息发给 LLM */
    String generate(String prompt);

    /** 指定模型的自由生成 */
    String generate(String prompt, String model);
}
