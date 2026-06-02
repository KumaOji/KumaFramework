package com.kuma.cloud.blog.grpc;

import com.kuma.boot.grpc.grpcorigin.client.GrpcClientProvide;
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Python gRPC 客户端 Channel 配置
 *
 * <p>依赖 {@code kuma.boot.grpc.client.host} 属性存在时才激活，
 * 由 {@link GrpcClientProvide} 统一管理连接参数（host / port / TLS / keepAlive / retry）。
 */
@Configuration
@ConditionalOnProperty(prefix = "kuma.boot.grpc.client", name = "host")
@RequiredArgsConstructor
public class PythonGrpcClientConfig {

    private final GrpcClientProvide grpcClientProvide;

    @Bean(destroyMethod = "shutdown")
    public ManagedChannel pythonServiceChannel() {
        return grpcClientProvide.channel();
    }

    // ── 在完成 generateProto 后取消注释，注册各服务的 BlockingStub ──────────

    // @Bean
    // public TextAnalysisServiceGrpc.TextAnalysisServiceBlockingStub textAnalysisStub() {
    //     return TextAnalysisServiceGrpc.newBlockingStub(pythonServiceChannel());
    // }

    // @Bean
    // public RecommendServiceGrpc.RecommendServiceBlockingStub recommendStub() {
    //     return RecommendServiceGrpc.newBlockingStub(pythonServiceChannel());
    // }
}
