package com.kuma.cloud.uaa.config;

import com.kuma.boot.web.autoconfigure.ActuatorHttpExchangeAutoConfiguration;
import com.kuma.boot.web.autoconfigure.WebMvcAutoConfiguration;
import com.kuma.boot.web.autoconfigure.WebMdcAutoConfiguration;
import com.kuma.boot.web.error.ErrorHandlingConfiguration;
import com.kuma.boot.web.observability.ServletObservabilityConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@Import(ServletObservabilityConfiguration.class)
@ImportAutoConfiguration({
        WebMvcAutoConfiguration.class,
        WebMdcAutoConfiguration.class,
        ActuatorHttpExchangeAutoConfiguration.class,
        ErrorHandlingConfiguration.class
})
public class WebConfiguration {}
