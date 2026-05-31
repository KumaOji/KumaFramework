package com.kuma.boot.grpc.grpcorigin.server.configuration;

import com.kuma.boot.grpc.grpcorigin.server.GrpcServer;
import com.kuma.boot.grpc.grpcorigin.server.interceptor.GrpcServerTraceIdInterceptor;
import com.kuma.boot.grpc.grpcorigin.server.properties.GrpcServerProperties;
import io.grpc.BindableService;
import io.grpc.ServerInterceptor;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "kuma.boot.grpc.server", name = "port")
@EnableConfigurationProperties({GrpcServerProperties.class})
public class GrpcServerAutoConfiguration {
   private final GrpcServerProperties properties;

   public GrpcServerAutoConfiguration(GrpcServerProperties properties) {
      this.properties = properties;
   }

   @Bean
   @ConditionalOnMissingBean
   public GrpcServer grpcServer(List<ServerInterceptor> interceptors, List<BindableService> services) {
      return new GrpcServer(this.properties, interceptors, services);
   }

   @Bean
   @ConditionalOnMissingBean
   public GrpcServerTraceIdInterceptor grpcServerTraceIdInterceptor() {
      return new GrpcServerTraceIdInterceptor(this.properties);
   }
}
