package com.kuma.cloud.project20.config;

import com.kuma.ai.agent.grpc.KumaAgentServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * AgentGrpcClientConfig —— 创建 gRPC ManagedChannel 及服务存根 Bean
 *
 * <p>配置项（application-dev.yml 中可覆盖）：
 * <ul>
 *   <li>{@code kuma.agent.grpc.host} — Agent gRPC 服务地址（默认 127.0.0.1）</li>
 *   <li>{@code kuma.agent.grpc.port} — Agent gRPC 端口（默认 50051）</li>
 * </ul>
 */
@Configuration
public class AgentGrpcClientConfig {

    private static final Logger log = LoggerFactory.getLogger(AgentGrpcClientConfig.class);

    @Value("${kuma.agent.grpc.host:127.0.0.1}")
    private String host;

    @Value("${kuma.agent.grpc.port:50051}")
    private int port;

    private ManagedChannel channel;

    /**
     * gRPC 托管通道（明文，无 TLS）。
     * 消息大小限制 50 MB，与 Python 服务端保持一致。
     */
    @Bean
    public ManagedChannel agentManagedChannel() {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .maxInboundMessageSize(50 * 1024 * 1024)
                .build();
        log.info("[Agent gRPC] Channel created → {}:{}", host, port);
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
