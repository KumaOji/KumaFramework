package com.kuma.cloud.blog.config;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        com.kuma.boot.grpc.grpcorigin.client.configuration.GrpcClientAutoConfiguration.class,
        com.kuma.boot.grpc.spring.GrpcClientExtendsAutoConfiguration.class,
        com.kuma.boot.grpc.spring.GrpcServerExtendsAutoConfiguration.class,
        com.kuma.boot.grpc.spring.annotation.GrpcDiscoveryClientAutoConfiguration.class,
        com.kuma.boot.grpc.spring.config.GrpcClientAutoConfiguration.class
})
public class GrpcConfiguration {
}
