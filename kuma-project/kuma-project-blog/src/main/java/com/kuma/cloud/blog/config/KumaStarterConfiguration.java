package com.kuma.cloud.blog.config;

import com.kuma.boot.ai.autoconfigure.AiChatAutoConfiguration;
import com.kuma.boot.ai.autoconfigure.AiMcpAutoConfiguration;
import com.kuma.boot.ai.mcp.autoconfigure.McpServerAutoConfiguration;
import com.kuma.boot.data.datasource.autoconfigure.TransactionExecutorAutoConfiguration;
import com.kuma.boot.data.datasource.autoconfigure.TransactionSupportAutoConfiguration;
import com.kuma.boot.frp.autoconfigure.FrpAutoConfiguration;
import com.kuma.boot.grpc.grpcorigin.client.configuration.GrpcClientAutoConfiguration;
import com.kuma.boot.grpc.spring.GrpcClientExtendsAutoConfiguration;
import com.kuma.boot.grpc.spring.GrpcServerExtendsAutoConfiguration;
import com.kuma.boot.grpc.spring.annotation.GrpcDiscoveryClientAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * Explicitly loads the Kuma starter configurations used by the blog application.
 */
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        AiChatAutoConfiguration.class,
        AiMcpAutoConfiguration.class,
        McpServerAutoConfiguration.class,
        TransactionExecutorAutoConfiguration.class,
        TransactionSupportAutoConfiguration.class,
        FrpAutoConfiguration.class,
        GrpcClientExtendsAutoConfiguration.class,
        GrpcServerExtendsAutoConfiguration.class,
        com.kuma.boot.grpc.spring.config.GrpcClientAutoConfiguration.class,
        GrpcDiscoveryClientAutoConfiguration.class,
        GrpcClientAutoConfiguration.class
})
public class KumaStarterConfiguration {
}
