package com.kuma.boot.ai.service.internal;

import com.kuma.boot.ai.model.Sentiment;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;

/**
 * 文本处理声明式接口，展示 LangChain4j 的 <b>结构化输出</b> 能力：
 * 方法的返回类型直接决定 LLM 输出格式，由框架自动约束并解析——
 * 无需手写解析逻辑（如按行 split、toLowerCase 等）。
 *
 * <ul>
 *   <li>{@code List<String>} —— 框架引导模型输出 JSON 数组并反序列化</li>
 *   <li>{@code Sentiment}（枚举）—— 框架约束模型只能返回枚举值之一</li>
 * </ul>
 */
public interface TextAssistant {

    @UserMessage("请用不超过 {{maxWords}} 字概括以下内容，只返回概括本身：\n\n{{text}}")
    String summarize(@V("text") String text, @V("maxWords") int maxWords);

    @UserMessage("请将以下内容翻译成{{language}}，只返回译文：\n\n{{text}}")
    String translate(@V("text") String text, @V("language") String language);

    @UserMessage("请从以下内容中提取最多 {{count}} 个最重要的关键词：\n\n{{text}}")
    List<String> extractKeywords(@V("text") String text, @V("count") int count);

    @UserMessage("请分析以下内容的整体情感倾向：\n\n{{text}}")
    Sentiment sentiment(@V("text") String text);
}
