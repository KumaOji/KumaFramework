package com.kuma.cloud.gateway.config;

import com.kuma.boot.logger.autoconfigure.LogbackAccessAutoConfiguration;
import com.kuma.boot.logger.autoconfigure.LoggerAutoConfiguration;
import com.kuma.boot.logger.autoconfigure.OpenTelemetryAppenderAutoConfiguration;
import com.kuma.boot.logger.autoconfigure.TlogAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/** Explicitly loads Kuma logging configurations used by the gateway. */
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        LogbackAccessAutoConfiguration.class,
        LoggerAutoConfiguration.class,
        OpenTelemetryAppenderAutoConfiguration.class,
        TlogAutoConfiguration.class
})
public class KumaStarterConfiguration {
}
