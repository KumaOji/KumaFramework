package com.kuma.cloud.reference.config;

import com.kuma.boot.web.autoconfigure.ServletAutoConfiguration;
import com.kuma.boot.web.autoconfigure.WebMdcAutoConfiguration;
import com.kuma.boot.web.autoconfigure.properties.WebMvcFilterProperties;
import com.kuma.boot.web.servlet.filter.TenantFilter;
import com.kuma.boot.web.servlet.filter.TraceFilter;
import com.kuma.boot.web.servlet.filter.VersionFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import static org.assertj.core.api.Assertions.assertThat;

class WebMdcConfigurationTest {
    private final WebApplicationContextRunner runner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(WebMdcAutoConfiguration.class));

    @Test
    void registersThreeAsyncFiltersWithoutOtherServletFeatures() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasBean("kuma.boot.web.filter-" + WebMvcFilterProperties.class.getName());
            assertThat(context).doesNotHaveBean(ServletAutoConfiguration.class);
            assertThat(context.getBeansOfType(FilterRegistrationBean.class)).hasSize(3);
            var trace = context.getBean("traceFilterFilterRegistrationBean", FilterRegistrationBean.class);
            assertThat(trace.getFilter()).isInstanceOf(TraceFilter.class);
            assertThat(trace.getOrder()).isEqualTo(Ordered.HIGHEST_PRECEDENCE + 4);
            assertThat(trace.isAsyncSupported()).isTrue();
            assertThat(context.getBean("tenantFilterFilterRegistrationBean", FilterRegistrationBean.class).getFilter())
                    .isInstanceOf(TenantFilter.class);
            assertThat(context.getBean("versionFilterFilterRegistrationBean", FilterRegistrationBean.class).getFilter())
                    .isInstanceOf(VersionFilter.class);
        });
    }

    @Test
    void respectsFilterSwitches() {
        runner.withPropertyValues("kuma.boot.web.filter.trace=false", "kuma.boot.web.filter.tenant=false",
                "kuma.boot.web.filter.version=false").run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context.getBeansOfType(FilterRegistrationBean.class)).isEmpty();
        });
    }

    @Test
    void backsOffForAnApplicationFilterRegistrationWithADifferentBeanName() {
        runner.withUserConfiguration(CustomRegistration.class).run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).doesNotHaveBean("traceFilterFilterRegistrationBean");
            assertThat(context.getBeansOfType(FilterRegistrationBean.class)).hasSize(3);
            assertThat(context.getBean("customTraceRegistration", FilterRegistrationBean.class).getOrder()).isEqualTo(20);
        });
    }

    @Test
    void backsOffForAnApplicationFilterBean() {
        runner.withBean(TraceFilter.class, () -> new TraceFilter(new WebMvcFilterProperties(), null)).run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).doesNotHaveBean("traceFilterFilterRegistrationBean");
            assertThat(context).hasSingleBean(TraceFilter.class);
        });
    }

    @Test
    void legacyServletConfigurationReusesTheSameRegistrations() {
        runner.withConfiguration(AutoConfigurations.of(ServletAutoConfiguration.class)).run(context -> {
            assertThat(context).hasNotFailed();
            for (var filterType : new Class<?>[]{TraceFilter.class, TenantFilter.class, VersionFilter.class}) {
                long count = context.getBeansOfType(FilterRegistrationBean.class).values().stream()
                        .filter(registration -> filterType.isInstance(registration.getFilter())).count();
                assertThat(count).isEqualTo(1);
            }
        });
    }

    @Test
    void doesNotLoadInANonWebApplication() {
        new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(WebMdcAutoConfiguration.class))
                .run(context -> assertThat(context).doesNotHaveBean(WebMdcAutoConfiguration.class));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomRegistration {
        @Bean
        FilterRegistrationBean<TraceFilter> customTraceRegistration() {
            var registration = new FilterRegistrationBean<>(new TraceFilter(new WebMvcFilterProperties(), null));
            registration.setOrder(20);
            return registration;
        }
    }
}
