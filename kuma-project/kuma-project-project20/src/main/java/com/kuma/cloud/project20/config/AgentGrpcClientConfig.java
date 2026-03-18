package com.kuma.cloud.project20.config;

import com.kuma.ai.agent.grpc.KumaAgentServiceGrpc;
import com.kuma.boot.grpc.grpcorigin.client.GrpcClientProvide;
import com.kuma.boot.grpc.grpcorigin.client.properties.GrpcClientProperties;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * AgentGrpcClientConfig —— 创建 gRPC ManagedChannel 及服务存根 Bean
 *
 * <p>连接配置通过 {@code kuma.boot.grpc.client.*} 统一管理：
 * <ul>
 *   <li>{@code kuma.boot.grpc.client.host} — Agent gRPC 服务地址</li>
 *   <li>{@code kuma.boot.grpc.client.port} — Agent gRPC 端口</li>
 *   <li>{@code kuma.boot.grpc.client.use-plaintext} — 是否明文（无 TLS）</li>
 * </ul>
 */
@Configuration
public class AgentGrpcClientConfig {

    private static final Logger log = LoggerFactory.getLogger(AgentGrpcClientConfig.class);

    private ManagedChannel channel;

    /**
     * gRPC 托管通道，通过 {@link GrpcClientProvide} 统一构建。
     * 消息大小限制 50 MB，与 Python 服务端保持一致。
     */
    @Bean
    public ManagedChannel agentManagedChannel(GrpcClientProvide grpcClientProvide,
                                              GrpcClientProperties grpcClientProperties) {
        channel = grpcClientProvide.channel(
                grpcClientProperties.getHost(),
                grpcClientProperties.getPort(),
                builder -> ((ManagedChannelBuilder<?>) builder).maxInboundMessageSize(50 * 1024 * 1024)
        );
        log.info("[Agent gRPC] Channel created → {}:{}", grpcClientProperties.getHost(), grpcClientProperties.getPort());
        return channel;
    }

    /**
     * 阻塞式存根：用于普通 RPC 和服务端流（chatStream）。
     */
    @Bean
    public KumaAgentServiceGrpc.KumaAgentServiceBlockingStub agentBlockingStub(ManagedChannel agentManagedChannel) {
        return KumaAgentServiceGrpc.newBlockingStub(agentManagedChannel);
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        if (channel != null && !channel.isShutdown()) {
            log.info("[Agent gRPC] Shutting down channel …");
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
