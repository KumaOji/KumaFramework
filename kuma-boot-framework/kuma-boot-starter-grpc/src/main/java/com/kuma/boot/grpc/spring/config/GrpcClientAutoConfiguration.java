package com.kuma.boot.grpc.spring.config;

import com.kuma.boot.grpc.spring.annotation.GrpcClientBeanPostProcessor;
import io.grpc.NameResolverRegistry;
import io.grpc.netty.NettyChannelBuilder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.ClientInterceptorsConfigurer;
import org.springframework.grpc.client.GrpcChannelBuilderCustomizer;
import org.springframework.grpc.client.GrpcClientFactory;

@AutoConfiguration
@ConditionalOnClass({DiscoveryClient.class})
public class GrpcClientAutoConfiguration {
   @Bean(
      name = {"grpcVirtualThreadExecutor"}
   )
   public static ExecutorService grpcVirtualThreadExecutor() {
      return Executors.newVirtualThreadPerTaskExecutor();
   }

   @Bean
   @ConditionalOnBean({DiscoveryClient.class})
   public static GrpcClientBeanPostProcessor grpcClientBeanPostProcessor(GrpcClientFactory grpcClientFactory, DiscoveryClient discoveryClient, @Qualifier("grpcVirtualThreadExecutor") ExecutorService grpcVirtualThreadExecutor) {
      NameResolverRegistry.getDefaultRegistry().register(new DiscoveryNameResolverProvider(discoveryClient, grpcVirtualThreadExecutor));
      return new GrpcClientBeanPostProcessor(grpcClientFactory, discoveryClient);
   }

   @Bean
   @ConditionalOnBean({DiscoveryClient.class})
   DiscoveryGrpcChannelFactory discoveryGrpcChannelFactory(List globalCustomizers, ClientInterceptorsConfigurer interceptorsConfigurer) {
      return new DiscoveryGrpcChannelFactory(globalCustomizers, interceptorsConfigurer);
   }
}
