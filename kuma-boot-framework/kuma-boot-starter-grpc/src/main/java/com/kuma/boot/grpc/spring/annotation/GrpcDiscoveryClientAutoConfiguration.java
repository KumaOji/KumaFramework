package com.kuma.boot.grpc.spring.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration(
   proxyBeanMethods = false
)
@ConditionalOnBean({DiscoveryClient.class})
public class GrpcDiscoveryClientAutoConfiguration {
   @ConditionalOnMissingBean
   @Lazy
   @Bean
   DiscoveryClientResolverFactory grpcDiscoveryClientResolverFactory(final DiscoveryClient client) {
      return new DiscoveryClientResolverFactory(client);
   }
}
