package com.kuma.cloud.uaa.config;

import com.kuma.boot.actuator.endpoint.audit.autoconfigure.AuditEndPointAutoConfiguration;
import com.kuma.boot.actuator.endpoint.druid.autoconfigure.DruidEndpointAutoConfiguration;
import com.kuma.boot.actuator.endpoint.kmc.autoconfigure.KmcEndPointAutoConfiguration;
import com.kuma.boot.actuator.endpoint.request.autoconfigure.RequestMappingEndPointAutoConfiguration;
import com.kuma.boot.actuator.endpoint.startup.autoconfigure.KmcStartupEndpointAutoConfiguration;
import com.kuma.boot.actuator.health.autoconfigure.KmcHealthAutoConfiguration;
import com.kuma.boot.actuator.info.autoconfigure.KmcInfoAutoConfiguration;
import com.kuma.boot.actuator.mbean.autoconfigure.ManagedResourceAutoConfiguration;
import com.kuma.boot.data.datasource.autoconfigure.TransactionExecutorAutoConfiguration;
import com.kuma.boot.data.datasource.autoconfigure.TransactionSupportAutoConfiguration;
import com.kuma.boot.logger.autoconfigure.LogbackAccessAutoConfiguration;
import com.kuma.boot.logger.autoconfigure.LoggerAutoConfiguration;
import com.kuma.boot.logger.autoconfigure.OpenTelemetryAppenderAutoConfiguration;
import com.kuma.boot.logger.autoconfigure.TlogAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/** Explicitly loads Kuma starter configurations used by UAA. */
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
        TransactionExecutorAutoConfiguration.class,
        TransactionSupportAutoConfiguration.class,
        LogbackAccessAutoConfiguration.class,
        LoggerAutoConfiguration.class,
        OpenTelemetryAppenderAutoConfiguration.class,
        TlogAutoConfiguration.class
})
public class KumaStarterConfiguration {
}
