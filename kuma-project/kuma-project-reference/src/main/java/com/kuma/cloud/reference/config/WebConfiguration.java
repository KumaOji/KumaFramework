package com.kuma.cloud.reference.config;

import com.kuma.boot.web.autoconfigure.ActuatorHttpExchangeAutoConfiguration;
import com.kuma.boot.web.autoconfigure.WebMvcAutoConfiguration;
import com.kuma.boot.web.error.ErrorHandlingConfiguration;
import com.kuma.boot.web.autoconfigure.properties.WebMvcFilterProperties;
import com.kuma.boot.web.servlet.filter.TraceFilter;
import com.kuma.boot.web.servlet.filter.TenantFilter;
import com.kuma.boot.web.servlet.filter.VersionFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        WebMvcAutoConfiguration.class,
        ActuatorHttpExchangeAutoConfiguration.class,
        ErrorHandlingConfiguration.class
})
public class WebConfiguration {
    @Bean
    @ConditionalOnProperty(prefix = WebMvcFilterProperties.PREFIX, name = "tenant", matchIfMissing = true)
    public FilterRegistrationBean<TenantFilter> referenceTenantFilter() {
        var registration = new FilterRegistrationBean<>(new TenantFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 5);
        registration.setAsyncSupported(true);
        return registration;
    }

    @Bean
    @ConditionalOnProperty(prefix = WebMvcFilterProperties.PREFIX, name = "version", matchIfMissing = true)
    public FilterRegistrationBean<VersionFilter> referenceVersionFilter() {
        var registration = new FilterRegistrationBean<>(new VersionFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 6);
        registration.setAsyncSupported(true);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<TraceFilter> referenceTraceFilter(
            WebMvcFilterProperties properties, ObjectProvider<Tracer> tracers) {
        var registration = new FilterRegistrationBean<>(new TraceFilter(properties, tracers.getIfAvailable()));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 4);
        registration.setAsyncSupported(true);
        return registration;
    }
}
