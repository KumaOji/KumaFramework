package com.kuma.cloud.gateway.config;

import com.kuma.boot.actuator.health.KmcHealthIndicator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class StarterConfigurationTest {
    @Test
    void frameworkImportsWorkWithoutTheServletApi() {
        new ReactiveWebApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader("jakarta.servlet"))
                .withPropertyValues("spring.application.name=kuma-cloud-gateway")
                .withConfiguration(org.springframework.boot.autoconfigure.AutoConfigurations.of(
                        org.springframework.cloud.autoconfigure.RefreshAutoConfiguration.class))
                .withUserConfiguration(ActuatorConfiguration.class, LoggerConfiguration.class,
                        PrometheusConfiguration.class, Ip2regionConfiguration.class)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(KmcHealthIndicator.class);
                    assertThat(context).hasBean("prometheusThreadPoolTaskScheduler");
                    assertThat(context).doesNotHaveBean("oauth2ResourceSecurityFilterChain");
                    assertThat(context).doesNotHaveBean("webMvcAutoConfiguration");
                });
    }
}
