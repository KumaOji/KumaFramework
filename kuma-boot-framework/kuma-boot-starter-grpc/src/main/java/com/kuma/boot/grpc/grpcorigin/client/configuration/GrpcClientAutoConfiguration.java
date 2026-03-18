package com.kuma.boot.grpc.grpcorigin.client.configuration;

import com.kuma.boot.grpc.grpcorigin.client.GrpcClientProvide;
import com.kuma.boot.grpc.grpcorigin.client.interceptor.GrpcClientTraceIdInterceptor;
import com.kuma.boot.grpc.grpcorigin.client.properties.GrpcClientProperties;
import io.grpc.ClientInterceptor;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({GrpcClientProperties.class})
public class GrpcClientAutoConfiguration {
   private final GrpcClientProperties properties;

   public GrpcClientAutoConfiguration(GrpcClientProperties properties) {
      this.properties = properties;
   }

   @Bean
   @ConditionalOnMissingBean
   public GrpcClientProvide grpcClientProvide(List interceptors) {
      return new GrpcClientProvide(this.properties, interceptors);
   }

   @Bean
   @ConditionalOnMissingBean
   public GrpcClientTraceIdInterceptor grpcClientTraceIdInterceptor() {
      return new GrpcClientTraceIdInterceptor(this.properties);
   }
}
