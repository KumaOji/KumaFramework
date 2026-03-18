package com.kuma.boot.grpc.spring;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.grpc.client.GrpcChannelBuilderCustomizer;

@AutoConfiguration
public class GrpcClientExtendsAutoConfiguration {
   @Bean
   @Order(100)
   GrpcChannelBuilderCustomizer flowControlCustomizer() {
      return (name, builder) -> builder.flowControlWindow(1048576);
   }

   @Bean
   @Order(200)
   GrpcChannelBuilderCustomizer retryChannelCustomizer() {
      return (name, builder) -> builder.enableRetry().maxRetryAttempts(5);
   }
}
