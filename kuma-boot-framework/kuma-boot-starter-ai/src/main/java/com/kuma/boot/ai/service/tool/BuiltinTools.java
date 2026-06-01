package com.kuma.boot.ai.service.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 内置示例工具集，演示 Function Calling。
 * 通过 {@code kuma.boot.ai.agent.builtin-tools-enabled=false} 可关闭。
 *
 * <p>LLM 在对话中判断需要时，会自动以结构化参数调用这些方法，
 * 并将返回值作为后续推理的依据——解决大模型「不知道当前时间」「算不准数学」等固有缺陷。
 */
public class BuiltinTools implements AiToolProvider {

    private static final Logger log = LoggerFactory.getLogger(BuiltinTools.class);

    @Tool("获取服务器当前的日期和时间")
    public String currentDateTime() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.debug("[tool] currentDateTime -> {}", now);
        return now;
    }

    @Tool("精确计算两个数字的四则运算，运算符仅支持 + - * /")
    public double calculate(@P("第一个操作数") double a,
                            @P("运算符，取值 + - * / 之一") String operator,
                            @P("第二个操作数") double b) {
        double result = switch (operator.trim()) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> b == 0 ? Double.NaN : a / b;
            default  -> Double.NaN;
        };
        log.debug("[tool] calculate {} {} {} -> {}", a, operator, b, result);
        return result;
    }
}
