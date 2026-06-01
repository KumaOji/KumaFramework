package com.kuma.boot.ai.model;

/**
 * 情感倾向枚举。作为 AiServices 方法的返回类型时，
 * LangChain4j 会自动约束 LLM 输出为枚举值之一并完成解析（结构化输出）。
 */
public enum Sentiment {
    POSITIVE,
    NEGATIVE,
    NEUTRAL
}
