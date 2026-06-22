package com.kuma.boot.ai.autoconfigure;

import com.kuma.boot.ai.mcp.AiToolMcpRegistrar;
import com.kuma.boot.mcp.tool.McpTool;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * AI 工具 → MCP 桥接自动装配。
 *
 * <p>当类路径存在 MCP 模块（{@link McpTool}）且 {@code kuma.boot.ai.mcp.enabled=true}（默认开启）时,
 * 注册 {@link AiToolMcpRegistrar},把所有 langchain4j {@code @Tool} 方法自动暴露为 MCP 工具。
 * 排在 MCP 自动装配之前,确保桥接出的工具能被 MCP 工具注册表收集。
 */
@AutoConfiguration(before = com.kuma.boot.mcp.autoconfigure.McpServerAutoConfiguration.class)
@ConditionalOnClass(McpTool.class)
@ConditionalOnProperty(prefix = "kuma.boot.ai.mcp", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AiMcpAutoConfiguration {

    /** 须为 static,以便在 Bean 实例化前完成工具定义注册 */
    @Bean
    static AiToolMcpRegistrar aiToolMcpRegistrar() {
        return new AiToolMcpRegistrar();
    }
}
