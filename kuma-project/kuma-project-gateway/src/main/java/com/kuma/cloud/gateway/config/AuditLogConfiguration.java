package com.kuma.cloud.gateway.config;

import com.kuma.boot.auditlog.core.AuditLogStore;
import com.kuma.boot.auditlog.core.MetadataOnlyAuditLogStore;
import com.kuma.cloud.gateway.filter.GatewayAuditWebFilter;
import com.kuma.cloud.gateway.properties.GatewayCloudProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Uses the stack-neutral record/store API; never imports the servlet audit aspect into WebFlux. */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "kuma.boot.audit-log", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AuditLogConfiguration {
    @Bean AuditLogStore auditLogStore() { return new MetadataOnlyAuditLogStore(); }
    @Bean GatewayAuditWebFilter gatewayAuditWebFilter(AuditLogStore store, GatewayCloudProperties properties) {
        return new GatewayAuditWebFilter(store, properties);
    }
}
