package com.kuma.cloud.blog.config;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        com.kuma.boot.ai.autoconfigure.AiChatAutoConfiguration.class,
        com.kuma.boot.ai.autoconfigure.AiMcpAutoConfiguration.class,
        com.kuma.boot.ai.mcp.autoconfigure.McpServerAutoConfiguration.class
})
public class AiConfiguration {
}
