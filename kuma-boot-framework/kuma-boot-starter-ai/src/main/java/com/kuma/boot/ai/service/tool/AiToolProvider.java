package com.kuma.boot.ai.service.tool;

/**
 * AI 工具提供者标记接口。
 *
 * <p>任何 Spring Bean 实现本接口后，其内部用 {@link dev.langchain4j.agent.tool.Tool}
 * 注解标记的方法会被自动注册到 Agent，供 LLM 通过 Function Calling 调用。
 *
 * <p>示例：
 * <pre>{@code
 * @Component
 * public class WeatherTools implements AiToolProvider {
 *     @Tool("查询指定城市的天气")
 *     public String weather(@P("城市名") String city) { ... }
 * }
 * }</pre>
 */
public interface AiToolProvider {
}
