package com.kuma.boot.web.autoconfigure;

import com.kuma.boot.web.autoconfigure.properties.WebMvcFilterProperties;
import com.kuma.boot.web.servlet.filter.TenantFilter;
import com.kuma.boot.web.servlet.filter.TraceFilter;
import com.kuma.boot.web.servlet.filter.VersionFilter;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/** Registers request MDC filters without enabling unrelated servlets or listeners. */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(WebMvcFilterProperties.class)
public class WebMdcAutoConfiguration {
    @Bean
    @ConditionalOnProperty(prefix = WebMvcFilterProperties.PREFIX, name = "trace", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(value = TraceFilter.class, parameterizedContainer = FilterRegistrationBean.class)
    public FilterRegistrationBean<TraceFilter> traceFilterFilterRegistrationBean(
            WebMvcFilterProperties properties, ObjectProvider<Tracer> tracers) {
        return registration(new TraceFilter(properties, tracers.getIfAvailable()), 4);
    }

    @Bean
    @ConditionalOnProperty(prefix = WebMvcFilterProperties.PREFIX, name = "tenant", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(value = TenantFilter.class, parameterizedContainer = FilterRegistrationBean.class)
    public FilterRegistrationBean<TenantFilter> tenantFilterFilterRegistrationBean() {
        return registration(new TenantFilter(), 5);
    }

    @Bean
    @ConditionalOnProperty(prefix = WebMvcFilterProperties.PREFIX, name = "version", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(value = VersionFilter.class, parameterizedContainer = FilterRegistrationBean.class)
    public FilterRegistrationBean<VersionFilter> versionFilterFilterRegistrationBean() {
        return registration(new VersionFilter(), 6);
    }

    private static <T extends Filter> FilterRegistrationBean<T> registration(T filter, int orderOffset) {
        var registration = new FilterRegistrationBean<>(filter);
        registration.setName(filter.getClass().getName());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + orderOffset);
        registration.setAsyncSupported(true);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);
        return registration;
    }
}
