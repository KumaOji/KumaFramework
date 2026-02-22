/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.annotation.Bean
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.core.annotation.Order
 *  org.springframework.security.authorization.AuthorizationManager
 *  org.springframework.security.config.annotation.web.builders.HttpSecurity
 *  org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
 *  org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer$AuthorizationManagerRequestMatcherRegistry
 *  org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer$AuthorizedUrl
 *  org.springframework.security.config.http.SessionCreationPolicy
 *  org.springframework.security.web.AuthenticationEntryPoint
 *  org.springframework.security.web.SecurityFilterChain
 *  org.springframework.security.web.access.AccessDeniedHandler
 *  org.springframework.security.web.util.matcher.RequestMatcher
 *  org.springframework.web.method.HandlerMethod
 *  org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
 *  org.springframework.web.util.pattern.PathPattern
 */
package com.kuma.boot.security.spring.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.annotation.NotAuth;
import com.kuma.boot.security.spring.authentication.login.LoginCustomizer;
import com.kuma.boot.security.spring.authentication.response.denied.JsonAccessDeniedHandler;
import com.kuma.boot.security.spring.authentication.response.entrypoint.JsonAuthenticationEntryPoint;
import com.kuma.boot.security.spring.authorization.SecurityAuthorizationManager;
import com.kuma.boot.security.spring.authorization.SecurityMatcherConfigurer;
import com.kuma.boot.security.spring.oauth2.token1.SecurityTokenStrategyConfigurer;
import com.kuma.boot.security.spring.properties.SecurityProperties;
import com.kuma.boot.security.spring.utils.SecurityUtils;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

@AutoConfiguration(after={SecurityAuthorizationAutoConfiguration.class})
public class Oauth2ResourceAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(Oauth2ResourceAutoConfiguration.class, (String)"kuma-boot-starter-security-spring", (String[])new String[0]);
    }

    @Order(value=0x7FFFFFFF)
    @Bean
    public SecurityFilterChain oauth2ResourceSecurityFilterChain(HttpSecurity http, SecurityMatcherConfigurer securityMatcherConfigurer, SecurityAuthorizationManager securityAuthorizationManager, SecurityTokenStrategyConfigurer securityTokenStrategyConfigurer, SecurityProperties securityProperties, ObjectProvider<LoginCustomizer> loginCustomizerObjectProvider) throws Exception {
        http.sessionManagement(sessionManagementCustomizer -> sessionManagementCustomizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).preload(true).maxAgeInSeconds(31536000L))).requestCache(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable).formLogin(AbstractHttpConfigurer::disable).httpBasic(AbstractHttpConfigurer::disable).cors(AbstractHttpConfigurer::disable).exceptionHandling(exceptionHandlingCustomizer -> {
            exceptionHandlingCustomizer.authenticationEntryPoint((AuthenticationEntryPoint)new JsonAuthenticationEntryPoint());
            exceptionHandlingCustomizer.accessDeniedHandler((AccessDeniedHandler)new JsonAccessDeniedHandler());
        }).authorizeHttpRequests(authorizeHttpRequests -> {
            this.permitAllUrls((AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry)authorizeHttpRequests, (ApplicationContext)http.getSharedObject(ApplicationContext.class), securityMatcherConfigurer, securityProperties);
            ((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)authorizeHttpRequests.anyRequest()).access((AuthorizationManager)securityAuthorizationManager);
        }).logout(logoutCustomizer -> logoutCustomizer.addLogoutHandler((request, response, authentication) -> {}).logoutSuccessHandler((request, response, authentication) -> {}).clearAuthentication(true).invalidateHttpSession(true)).oauth2ResourceServer(securityTokenStrategyConfigurer::from);
        loginCustomizerObjectProvider.ifAvailable(loginCustomizer -> {
            try {
                loginCustomizer.loginCustomizer(http);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return (SecurityFilterChain)http.build();
    }

    private void permitAllUrls(AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry authorizeHttpRequests, ApplicationContext ac, SecurityMatcherConfigurer securityMatcherConfigurer, SecurityProperties securityProperties) {
        List<String> permitAllUrls = securityProperties.getIgnoreUrl();
        RequestMappingHandlerMapping mapping = (RequestMappingHandlerMapping)ac.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map map = mapping.getHandlerMethods();
        map.keySet().forEach(info -> {
            HandlerMethod handlerMethod = (HandlerMethod)map.get(info);
            HashSet<NotAuth> set = new HashSet<NotAuth>();
            set.add((NotAuth)AnnotationUtils.findAnnotation((Class)handlerMethod.getBeanType(), NotAuth.class));
            set.add((NotAuth)AnnotationUtils.findAnnotation((Method)handlerMethod.getMethod(), NotAuth.class));
            set.forEach(annotation -> Optional.ofNullable(annotation).flatMap(inner -> Optional.ofNullable(info.getPathPatternsCondition())).ifPresent(pathPatternsRequestCondition -> permitAllUrls.addAll(pathPatternsRequestCondition.getPatterns().stream().map(PathPattern::getPatternString).toList())));
        });
        ((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)authorizeHttpRequests.requestMatchers(SecurityUtils.toRequestMatchers(permitAllUrls))).permitAll().requestMatchers(securityMatcherConfigurer.getStaticResourceArray())).permitAll().requestMatchers(new RequestMatcher[]{EndpointRequest.toAnyEndpoint()})).permitAll();
        LogUtils.info((String)"permit all urls: {}", (Object[])new Object[]{permitAllUrls.toString()});
    }
}

