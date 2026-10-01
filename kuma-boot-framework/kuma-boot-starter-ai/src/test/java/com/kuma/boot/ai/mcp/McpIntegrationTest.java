package com.kuma.boot.ai.mcp;

import com.kuma.boot.ai.autoconfigure.AiMcpAutoConfiguration;
import com.kuma.boot.ai.mcp.autoconfigure.McpServerAutoConfiguration;
import com.kuma.boot.ai.mcp.protocol.JsonRpc;
import com.kuma.boot.ai.mcp.server.McpServer;
import com.kuma.boot.ai.mcp.tool.McpToolRegistry;
import com.kuma.boot.ai.mcp.transport.McpHttpController;
import com.kuma.boot.ai.service.tool.BuiltinTools;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class McpIntegrationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AiMcpAutoConfiguration.class,
                    McpServerAutoConfiguration.class))
            .withBean(BuiltinTools.class, BuiltinTools::new);

    @Test
    void registersServerAndBridgesAiToolsByDefault() {
        runner.run(context -> {
            assertThat(context).hasNotFailed().hasSingleBean(McpServer.class)
                    .hasSingleBean(McpHttpController.class);
            McpToolRegistry tools = context.getBean(McpToolRegistry.class);
            assertThat(tools.size()).isEqualTo(2);
            assertThat(tools.find("calculate")).isPresent();
            JsonRpc.Response response = context.getBean(McpServer.class).handle(
                    new JsonRpc.Request("2.0", 1, "tools/call", Map.of(
                            "name", "calculate", "arguments", Map.of("a", 2, "operator", "+", "b", 3))));
            assertThat(response.error()).isNull();
            assertThat(response.result()).isNotNull();
        });
    }

    @Test
    void disablingServerAlsoDisablesToolBridging() {
        runner.withPropertyValues("kuma.boot.mcp.enabled=false").run(context ->
                assertThat(context).hasNotFailed().doesNotHaveBean(McpServer.class)
                        .doesNotHaveBean(McpHttpController.class).doesNotHaveBean(AiToolMcpRegistrar.class)
                        .doesNotHaveBean(LangchainToolMcpAdapter.class));
    }

    @Test
    void bridgingCanBeDisabledIndependently() {
        runner.withPropertyValues("kuma.boot.ai.mcp.enabled=false").run(context -> {
            assertThat(context).hasNotFailed().hasSingleBean(McpServer.class)
                    .doesNotHaveBean(AiToolMcpRegistrar.class);
            assertThat(context.getBean(McpToolRegistry.class).size()).isZero();
        });
    }

    @Test
    void httpTransportCanBeDisabledIndependently() {
        runner.withPropertyValues("kuma.boot.mcp.http-enabled=false").run(context ->
                assertThat(context).hasNotFailed().hasSingleBean(McpServer.class)
                        .doesNotHaveBean(McpHttpController.class));
    }
}
