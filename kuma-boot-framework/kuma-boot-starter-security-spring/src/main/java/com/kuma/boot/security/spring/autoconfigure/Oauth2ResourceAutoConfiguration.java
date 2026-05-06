/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.security.spring.autoconfigure;

import static com.kuma.boot.security.spring.utils.SecurityUtils.toRequestMatchers;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.annotation.NotAuth;
import com.kuma.boot.security.spring.aot.SecuritySpringRuntimeHintsRegistrar;
import com.kuma.boot.security.spring.authentication.login.LoginCustomizer;
import com.kuma.boot.security.spring.authentication.response.denied.JsonAccessDeniedHandler;
import com.kuma.boot.security.spring.authentication.response.entrypoint.JsonAuthenticationEntryPoint;
import com.kuma.boot.security.spring.authorization.SecurityAuthorizationManager;
import com.kuma.boot.security.spring.authorization.SecurityMatcherConfigurer;
import com.kuma.boot.security.spring.autoconfigure.properties.SecurityProperties;
import com.kuma.boot.security.spring.oauth2.token1.SecurityTokenStrategyConfigurer;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

/**
 * Oauth2ResourceSecurityConfigurer
 *
 * @author kuma
 * @version 2022.03
 * @since 2021/8/25 09:57
 */
@AutoConfiguration(
        after = SecurityAuthorizationAutoConfiguration.class)
@ImportRuntimeHints(SecuritySpringRuntimeHintsRegistrar.class)
public class Oauth2ResourceAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(Oauth2ResourceAutoConfiguration.class, StarterNameConstants.SECURITY_SPRINGSECURITY_STARTER);
    }

    @Order(Ordered.LOWEST_PRECEDENCE)
    @Bean
    public SecurityFilterChain oauth2ResourceSecurityFilterChain(
            HttpSecurity http,
            SecurityMatcherConfigurer securityMatcherConfigurer,
            SecurityAuthorizationManager securityAuthorizationManager,
            SecurityTokenStrategyConfigurer securityTokenStrategyConfigurer,
            SecurityProperties securityProperties,
            ObjectProvider<LoginCustomizer> loginCustomizerObjectProvider)
            throws Exception {

        // 跨域过滤器一定要添加至security配置中，不然只注入ioc中对于security端点不生效！ 添加跨域过滤器
        // httpSecurity.addFilter(corsFilter());

        // 使用redis存储、读取登录的认证信息
        // httpSecurity.securityContext(context ->
        // context.securityContextRepository(redisSecurityContextRepository));

        http.sessionManagement((sessionManagementCustomizer) -> {
                    sessionManagementCustomizer.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS);
                })
                .headers(httpSecurityHeadersConfigurer ->
                        httpSecurityHeadersConfigurer.httpStrictTransportSecurity(
                                hsts ->
                                        hsts.includeSubDomains(true)
                                                .preload(true)
                                                .maxAgeInSeconds(31536000)))
                .requestCache(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .exceptionHandling(
                        exceptionHandlingCustomizer -> {
                            exceptionHandlingCustomizer.authenticationEntryPoint(
                                    new JsonAuthenticationEntryPoint());
                            exceptionHandlingCustomizer.accessDeniedHandler(
                                    new JsonAccessDeniedHandler());
                        })
                .authorizeHttpRequests(
                        authorizeHttpRequests -> {
                            permitAllUrls(
                                    authorizeHttpRequests,
                                    http.getSharedObject(ApplicationContext.class),
                                    securityMatcherConfigurer,
                                    securityProperties);

                            authorizeHttpRequests.anyRequest().access(securityAuthorizationManager);
                        })
                .logout(
                        logoutCustomizer -> {
                            logoutCustomizer
                                    .addLogoutHandler((request, response, authentication) -> {})
                                    .logoutSuccessHandler((request, response, authentication) -> {})
                                    .clearAuthentication(true)
                                    .invalidateHttpSession(true);
                        })
                .oauth2ResourceServer(securityTokenStrategyConfigurer::from);

        loginCustomizerObjectProvider.ifAvailable(
                (loginCustomizer) -> {
                    try {
                        loginCustomizer.loginCustomizer(http);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        return http.build();
    }

    private void permitAllUrls(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
                    authorizeHttpRequests,
            ApplicationContext ac,
            SecurityMatcherConfigurer securityMatcherConfigurer,
            SecurityProperties securityProperties) {
        List<String> permitAllUrls = securityProperties.getIgnoreUrl();

        RequestMappingHandlerMapping mapping =
                ac.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        map.keySet()
                .forEach(
                        info -> {
                            HandlerMethod handlerMethod = map.get(info);
                            Set<NotAuth> set = new HashSet<>();
                            set.add(
                                    AnnotationUtils.findAnnotation(
                                            handlerMethod.getBeanType(), NotAuth.class));
                            set.add(
                                    AnnotationUtils.findAnnotation(
                                            handlerMethod.getMethod(), NotAuth.class));
                            set.forEach(
                                    annotation ->
                                            Optional.ofNullable(annotation)
                                                    .flatMap(
                                                            inner ->
                                                                    Optional.ofNullable(
                                                                            info
                                                                                    .getPathPatternsCondition()))
                                                    .ifPresent(
                                                            pathPatternsRequestCondition -> {
                                                                permitAllUrls.addAll(
                                                                        pathPatternsRequestCondition
                                                                                .getPatterns()
                                                                                .stream()
                                                                                .map(
                                                                                        PathPattern
                                                                                                ::getPatternString)
                                                                                .toList());
                                                            }));
                        });

        /**
         * PathPatternRequestMatcher 是 Spring 中最常用的请求匹配器之一，它使用 Ant 风格的路径模式来匹配请求的 URI。
         * ?：匹配任何单个字符（除了路径分隔符）。
         * *：匹配任何字符的序列（除了路径分隔符），但不包括空字符串。
         * **：匹配任何字符的序列，包括空字符串。至少匹配一个字符的序列，并且可以跨越路径分隔符。
         * {}：表示一个通配符的选择，可以匹配多个逗号分隔的模式。例如，{,春夏秋冬} 可以匹配任何以春夏秋冬开头的字符串。
         * []：在某些实现中，可以用于匹配括号内的单个字符。
         * ()：在某些实现中，可以用于分组匹配
         * // 匹配 /admin 下的任何资源，包括子目录
         * RequestMatcher adminMatcher = new PathPatternRequestMatcher("/admin/**");
         * // 匹配 /files 目录下的任何 HTML 文件
         * RequestMatcher fileMatcher = new PathPatternRequestMatcher("/files/*.{html,htm}", "GET");
         *
         * RegexRequestMatcher 使用正则表达式来匹配请求的 URI 和 HTTP 方法。
         * // 匹配任何以 /api 开头的 URI
         * RequestMatcher apiMatcher = new RegexRequestMatcher("^/api/.*");
         * // 匹配任何 HTTP 方法
         * RequestMatcher anyMethodMatcher = new RegexRequestMatcher("^/.*", "GET|POST|PUT|DELETE");
         *
         * RequestHeaderRequestMatcher 用来匹配请求头中的键和值。
         *  http.authorizeHttpRequests(a -> a.requestMatchers(new RequestHeaderRequestMatcher("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")).permitAll())
         *                 .formLogin(Customizer.withDefaults())
         *                 .csrf(c -> c.disable());
         *
         * NegatedRequestMatcher 允许你否定一个已有的 RequestMatcher 的匹配结果。
         * http.authorizeHttpRequests(a -> a.requestMatchers(new NegatedRequestMatcher(new PathPatternRequestMatcher("/hello"))).permitAll())
         *                 .formLogin(Customizer.withDefaults())
         *                 .csrf(c -> c.disable());
         *
         * AndRequestMatcher 和 OrRequestMatcher
         * AndRequestMatcher 和 OrRequestMatcher 分别用来组合多个 RequestMatcher 实例，进行“与”或“或”的逻辑匹配。
         * // 组合多个 matcher 进行“与”匹配
         * RequestMatcher andMatcher = new AndRequestMatcher(apiMatcher, headerMatcher);
         * // 组合多个 matcher 进行“或”匹配
         * RequestMatcher orMatcher = new OrRequestMatcher(adminMatcher, fileMatcher);
         */
        authorizeHttpRequests
                .requestMatchers(toRequestMatchers(permitAllUrls))
                .permitAll()
                .requestMatchers(securityMatcherConfigurer.getStaticResourceArray())
                .permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint())
                .permitAll();

        LogUtils.info("permit all urls: {}", permitAllUrls.toString());
    }
}
