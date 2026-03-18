package com.kuma.boot.grpc.spring.config;

import io.grpc.ChannelCredentials;
import io.grpc.netty.NettyChannelBuilder;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.springframework.grpc.client.ClientInterceptorsConfigurer;
import org.springframework.grpc.client.GrpcChannelBuilderCustomizer;
import org.springframework.grpc.client.NettyGrpcChannelFactory;

final class DiscoveryGrpcChannelFactory extends NettyGrpcChannelFactory {
   public DiscoveryGrpcChannelFactory(List globalCustomizers, ClientInterceptorsConfigurer interceptorsConfigurer) {
      super(globalCustomizers, interceptorsConfigurer);
      this.setVirtualTargets((p) -> p.substring(12));
   }

   public boolean supports(String target) {
      return target.startsWith("discovery:");
   }

   protected @NonNull NettyChannelBuilder newChannelBuilder(@NonNull String target, @NonNull ChannelCredentials credentials) {
      return NettyChannelBuilder.forTarget(String.format("discovery://%s", target), credentials);
   }
}
