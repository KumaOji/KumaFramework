package com.kuma.cloud.blog.config;

import com.kuma.boot.prometheus.autoconfigure.PrometheusAutoConfiguration;
import com.kuma.boot.prometheus.autoconfigure.PrometheusHealthAutoConfiguration;
import com.kuma.boot.prometheus.autoconfigure.PrometheusInterceptorAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        PrometheusAutoConfiguration.class,
        PrometheusHealthAutoConfiguration.class,
        PrometheusInterceptorAutoConfiguration.class
})
public class PrometheusConfiguration {
}
