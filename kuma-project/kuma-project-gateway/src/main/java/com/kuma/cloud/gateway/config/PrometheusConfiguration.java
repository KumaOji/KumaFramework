package com.kuma.cloud.gateway.config;

import com.kuma.boot.prometheus.autoconfigure.PrometheusAutoConfiguration;
import com.kuma.boot.prometheus.autoconfigure.PrometheusHealthAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        PrometheusAutoConfiguration.class,
        PrometheusHealthAutoConfiguration.class
})
public class PrometheusConfiguration {
}
