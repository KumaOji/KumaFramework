package com.kuma.boot.mcp.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.mcp.autoconfigure.properties.McpServerProperties;
import com.kuma.boot.mcp.prompt.McpPrompt;
import com.kuma.boot.mcp.prompt.McpPromptRegistry;
import com.kuma.boot.mcp.protocol.McpSchema;
import com.kuma.boot.mcp.resource.McpResource;
import com.kuma.boot.mcp.resource.McpResourceRegistry;
import com.kuma.boot.mcp.server.McpServer;
import com.kuma.boot.mcp.tool.McpTool;
import com.kuma.boot.mcp.tool.McpToolRegistry;
import com.kuma.boot.mcp.transport.McpHttpController;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * MCP 服务端自动装配。
 *
 * <p>装配三类注册表（tools / resources / prompts）与核心分发器 {@link McpServer};Web 环境下
 * 额外装配 HTTP 传输层 {@link McpHttpController}。业务侧只需将 {@link McpTool} /
 * {@link McpResource} / {@link McpPrompt} 实现注册为 Spring Bean 即可被自动收集暴露。
 */
@AutoConfiguration
@EnableConfigurationProperties(McpServerProperties.class)
@ConditionalOnProperty(prefix = McpServerProperties.PREFIX, name = "enabled",
        havingValue = "true", matchIfMissing = true)
public class McpServerAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(McpServerAutoConfiguration.class, StarterNameConstants.MCP_STARTER);
    }

    @Bean
    @ConditionalOnMissingBean
    public McpToolRegistry mcpToolRegistry(List<McpTool> tools) {
        return new McpToolRegistry(tools);
    }

    @Bean
    @ConditionalOnMissingBean
    public McpResourceRegistry mcpResourceRegistry(List<McpResource> resources) {
        return new McpResourceRegistry(resources);
    }

    @Bean
    @ConditionalOnMissingBean
    public McpPromptRegistry mcpPromptRegistry(List<McpPrompt> prompts) {
        return new McpPromptRegistry(prompts);
    }

    @Bean
    @ConditionalOnMissingBean
    public McpServer mcpServer(McpServerProperties props, McpToolRegistry toolRegistry,
                               McpResourceRegistry resourceRegistry, McpPromptRegistry promptRegistry) {
        McpSchema.Implementation serverInfo = new McpSchema.Implementation(props.getName(), props.getVersion());
        return new McpServer(serverInfo, props.getProtocolVersion(), props.getInstructions(),
                toolRegistry, resourceRegistry, promptRegistry);
    }

    /**
     * HTTP 传输层:仅在 Spring MVC 存在且 {@code http-enabled=true} 时装配。
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "org.springframework.web.servlet.config.annotation.WebMvcConfigurer")
    @ConditionalOnProperty(prefix = McpServerProperties.PREFIX, name = "http-enabled",
            havingValue = "true", matchIfMissing = true)
    static class WebTransportConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public McpHttpController mcpHttpController(McpServer mcpServer, McpServerProperties props) {
            return new McpHttpController(mcpServer, props.getEndpoint());
        }
    }
}
