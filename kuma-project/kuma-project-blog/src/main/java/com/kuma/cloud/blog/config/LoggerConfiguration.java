package com.kuma.cloud.blog.config;

import com.kuma.boot.logger.autoconfigure.LogbackAccessAutoConfiguration;
import com.kuma.boot.logger.autoconfigure.LoggerAutoConfiguration;
import com.kuma.boot.logger.autoconfigure.OpenTelemetryAppenderAutoConfiguration;
import com.kuma.boot.logger.autoconfigure.KlogAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/** Explicitly enable the Kuma logger starter features used by this application. */
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        LogbackAccessAutoConfiguration.class,
        LoggerAutoConfiguration.class,
        OpenTelemetryAppenderAutoConfiguration.class,
        KlogAutoConfiguration.class
})
public class LoggerConfiguration {}
