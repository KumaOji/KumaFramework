/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.github.xiaoymin.knife4j.extend.filter.basic.JakartaServletSecurityBasicAuthFilter
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.servlet.DispatcherType
 *  jakarta.servlet.Filter
 *  org.springdoc.core.properties.SpringDocConfigProperties
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.boot.web.servlet.FilterRegistrationBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.core.env.Environment
 *  org.springframework.web.cors.CorsConfiguration
 *  org.springframework.web.cors.CorsConfigurationSource
 *  org.springframework.web.cors.UrlBasedCorsConfigurationSource
 *  org.springframework.web.filter.CorsFilter
 */
package com.kuma.boot.springdoc.knife4j.spring.configuration;

import com.github.xiaoymin.knife4j.extend.filter.basic.JakartaServletSecurityBasicAuthFilter;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.springdoc.knife4j.spring.extension.Knife4jJakartaOperationCustomizer;
import com.kuma.boot.springdoc.knife4j.spring.extension.Knife4jOpenApiCustomizer;
import com.kuma.boot.springdoc.knife4j.spring.filter.JakartaProductionSecurityFilter;
import com.kuma.boot.springdoc.knife4j.spring.util.EnvironmentUtils;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableConfigurationProperties(value={Knife4jProperties.class, Knife4jSetting.class, Knife4jHttpBasic.class})
@ConditionalOnProperty(name={"knife4j.enable"}, havingValue="true")
public class Knife4jAutoConfiguration {
    private final Knife4jProperties properties;
    private final Environment environment;

    public Knife4jAutoConfiguration(Knife4jProperties properties, Environment environment) {
        this.properties = properties;
        this.environment = environment;
    }

    @Bean
    @ConditionalOnMissingBean
    public Knife4jOpenApiCustomizer knife4jOpenApiCustomizer(SpringDocConfigProperties docProperties) {
        LogUtils.debug((String)"Register Knife4jOpenApiCustomizer", (Object[])new Object[0]);
        return new Knife4jOpenApiCustomizer(this.properties, docProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public Knife4jJakartaOperationCustomizer knife4jJakartaOperationCustomizer() {
        return new Knife4jJakartaOperationCustomizer();
    }

    @Bean(value={"knife4jCorsFilter"})
    @ConditionalOnMissingBean(value={CorsFilter.class})
    @ConditionalOnProperty(name={"knife4j.cors"}, havingValue="true")
    public CorsFilter corsFilter() {
        LogUtils.info((String)"init CorsFilter...", (Object[])new Object[0]);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(Boolean.valueOf(true));
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setMaxAge(Long.valueOf(10000L));
        source.registerCorsConfiguration("/**", corsConfiguration);
        CorsFilter corsFilter = new CorsFilter((CorsConfigurationSource)source);
        return corsFilter;
    }

    @Bean
    @ConditionalOnMissingBean(value={JakartaServletSecurityBasicAuthFilter.class})
    @ConditionalOnExpression(value="${knife4j.production:false} && ${knife4j.basic.enable:true}")
    public FilterRegistrationBean<JakartaServletSecurityBasicAuthFilter> securityBasicAuthFilter(Knife4jProperties knife4jProperties) {
        JakartaServletSecurityBasicAuthFilter authFilter = new JakartaServletSecurityBasicAuthFilter();
        if (knife4jProperties == null) {
            authFilter.setEnableBasicAuth(EnvironmentUtils.resolveBool(this.environment, "knife4j.basic.enable", Boolean.FALSE).booleanValue());
            authFilter.setUserName(EnvironmentUtils.resolveString(this.environment, "knife4j.basic.username", "admin"));
            authFilter.setPassword(EnvironmentUtils.resolveString(this.environment, "knife4j.basic.password", "123321"));
        } else if (knife4jProperties.getBasic() == null) {
            authFilter.setEnableBasicAuth(Boolean.FALSE.booleanValue());
            authFilter.setUserName("admin");
            authFilter.setPassword("123321");
        } else {
            authFilter.setEnableBasicAuth(knife4jProperties.getBasic().isEnable());
            authFilter.setUserName(knife4jProperties.getBasic().getUsername());
            authFilter.setPassword(knife4jProperties.getBasic().getPassword());
            authFilter.addRule(knife4jProperties.getBasic().getInclude());
        }
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST, new DispatcherType[0]);
        registration.setFilter((Filter)authFilter);
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(value={JakartaProductionSecurityFilter.class})
    @ConditionalOnProperty(name={"knife4j.production"}, havingValue="true")
    public FilterRegistrationBean<JakartaProductionSecurityFilter> productionSecurityFilter(Environment environment) {
        boolean prod = false;
        JakartaProductionSecurityFilter p = null;
        if (this.properties == null) {
            if (environment != null) {
                String prodStr = environment.getProperty("knife4j.production");
                if (LogUtils.isDebugEnabled()) {
                    LogUtils.debug((String)"swagger.production:{}", (Object[])new Object[]{prodStr});
                }
                prod = Boolean.valueOf(prodStr);
            }
            p = new JakartaProductionSecurityFilter(prod);
        } else {
            p = new JakartaProductionSecurityFilter(this.properties.isProduction());
        }
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST, new DispatcherType[0]);
        registration.setFilter((Filter)p);
        registration.setOrder(0x7FFFFFFE);
        return registration;
    }
}

