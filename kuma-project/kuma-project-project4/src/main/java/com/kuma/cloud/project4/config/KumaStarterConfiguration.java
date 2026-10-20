package com.kuma.cloud.project4.config;

import com.kuma.boot.actuator.endpoint.audit.autoconfigure.AuditEndPointAutoConfiguration;
import com.kuma.boot.actuator.endpoint.druid.autoconfigure.DruidEndpointAutoConfiguration;
import com.kuma.boot.actuator.endpoint.kmc.autoconfigure.KmcEndPointAutoConfiguration;
import com.kuma.boot.actuator.endpoint.request.autoconfigure.RequestMappingEndPointAutoConfiguration;
import com.kuma.boot.actuator.endpoint.startup.autoconfigure.KmcStartupEndpointAutoConfiguration;
import com.kuma.boot.actuator.health.autoconfigure.KmcHealthAutoConfiguration;
import com.kuma.boot.actuator.info.autoconfigure.KmcInfoAutoConfiguration;
import com.kuma.boot.actuator.mbean.autoconfigure.ManagedResourceAutoConfiguration;
import com.kuma.boot.cache.caffeine.autoconfigure.CaffeineCacheAutoConfiguration;
import com.kuma.boot.cache.jetcache.autoconfigure.JetCacheAutoConfiguration;
import com.kuma.boot.data.datasource.autoconfigure.TransactionExecutorAutoConfiguration;
import com.kuma.boot.data.datasource.autoconfigure.TransactionSupportAutoConfiguration;
import com.kuma.boot.eventbus.atlas.config.EventAutoConfiguration;
import com.kuma.boot.eventbus.autoconfigure.EventBusAutoConfiguration;
import com.kuma.boot.eventbus.autoconfigure.EventBusPublisherAutoConfiguration;
import com.kuma.boot.frp.autoconfigure.FrpAutoConfiguration;
import com.kuma.boot.grpc.grpcorigin.client.configuration.GrpcClientAutoConfiguration;
import com.kuma.boot.grpc.spring.GrpcClientExtendsAutoConfiguration;
import com.kuma.boot.grpc.spring.GrpcServerExtendsAutoConfiguration;
import com.kuma.boot.grpc.spring.annotation.GrpcDiscoveryClientAutoConfiguration;
import com.kuma.boot.logger.autoconfigure.LogbackAccessAutoConfiguration;
import com.kuma.boot.logger.autoconfigure.LoggerAutoConfiguration;
import com.kuma.boot.logger.autoconfigure.OpenTelemetryAppenderAutoConfiguration;
import com.kuma.boot.logger.autoconfigure.KlogAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/** Explicitly loads configurations for Kuma starters used by the full demo. */
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        AuditEndPointAutoConfiguration.class,
        DruidEndpointAutoConfiguration.class,
        KmcEndPointAutoConfiguration.class,
        RequestMappingEndPointAutoConfiguration.class,
        KmcStartupEndpointAutoConfiguration.class,
        KmcHealthAutoConfiguration.class,
        KmcInfoAutoConfiguration.class,
        ManagedResourceAutoConfiguration.class,
        CaffeineCacheAutoConfiguration.class,
        JetCacheAutoConfiguration.class,
        TransactionExecutorAutoConfiguration.class,
        TransactionSupportAutoConfiguration.class,
        EventAutoConfiguration.class,
        EventBusAutoConfiguration.class,
        EventBusPublisherAutoConfiguration.class,
        FrpAutoConfiguration.class,
        GrpcClientExtendsAutoConfiguration.class,
        GrpcServerExtendsAutoConfiguration.class,
        com.kuma.boot.grpc.spring.config.GrpcClientAutoConfiguration.class,
        GrpcDiscoveryClientAutoConfiguration.class,
        GrpcClientAutoConfiguration.class,
        LogbackAccessAutoConfiguration.class,
        LoggerAutoConfiguration.class,
        OpenTelemetryAppenderAutoConfiguration.class,
        KlogAutoConfiguration.class
})
public class KumaStarterConfiguration {
}
