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

package com.kuma.boot.security.spring.authentication.login.social.justauth;

import static com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils.getBeanProvider;

import com.kuma.boot.security.spring.authentication.login.social.justauth.filter.JsonRequestFilter;
import com.kuma.boot.security.spring.authentication.login.social.justauth.filter.login.Auth2LoginAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.social.justauth.filter.redirect.Auth2DefaultRequestRedirectFilter;
import com.kuma.boot.security.spring.authentication.login.social.justauth.properties.JustAuthProperties;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.UsersConnectionRepository;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.UsersConnectionTokenRepository;
import com.kuma.boot.security.spring.authentication.login.social.justauth.service.Auth2StateCoder;
import com.kuma.boot.security.spring.authentication.login.social.justauth.service.Auth2UserService;
import com.kuma.boot.security.spring.authentication.login.social.justauth.service.ConnectionService;
import com.kuma.boot.security.spring.authentication.login.social.justauth.service.DefaultAuth2UserService;
import com.kuma.boot.security.spring.authentication.login.social.justauth.service.DefaultConnectionService;
import com.kuma.boot.security.spring.authentication.login.social.justauth.service.UmsUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.social.justauth.userdetails.DefaultJustAuthUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.social.justauth.userdetails.JustAuthUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.social.justauth.userdetails.converter.AuthenticationToUserDetailsConverter;
import com.kuma.boot.security.spring.authentication.login.social.justauth.userdetails.converter.Oauth2TokenAuthenticationTokenToUserConverter;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

/**
 * 添加 OAuth2(JustAuth) 配置
 */
public class JustAuthLoginFilterSecurityConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<JustAuthLoginFilterSecurityConfigurer<H>, H> {

    private JustAuthUserDetailsService justAuthUserDetailsService;
    private Auth2UserService auth2UserService;
    private ConnectionService connectionService;
    private ExecutorService updateConnectionTaskExecutor;
    private RedisConnectionFactory redisConnectionFactory;
    private AuthenticationToUserDetailsConverter authenticationToUserDetailsConverter;
    private Auth2StateCoder auth2StateCoder;

    private AuthenticationFailureHandler authenticationFailureHandler;
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private RememberMeServices rememberMeServices;
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    @Override
    public void configure(H builder) {
        ApplicationContext applicationContext = builder.getSharedObject(ApplicationContext.class);
        JustAuthProperties justAuthProperties =
                applicationContext.getBean(JustAuthProperties.class);

        // 添加 JsonRequestFilter 增加对 Json 格式的解析,
        builder.addFilterBefore(new JsonRequestFilter(), LogoutFilter.class);

        Auth2StateCoder auth2StateCoder = applicationContext.getBean(Auth2StateCoder.class);
        // 添加第三方登录入口过滤器
        String authorizationRequestBaseUri = justAuthProperties.getAuthLoginUrlPrefix();
        Auth2DefaultRequestRedirectFilter auth2DefaultRequestRedirectFilter =
                new Auth2DefaultRequestRedirectFilter(
                        authorizationRequestBaseUri,
                        this.auth2StateCoder == null ? auth2StateCoder : this.auth2StateCoder,
                        this.authenticationFailureHandler);
        builder.addFilterAfter(
                auth2DefaultRequestRedirectFilter, AbstractPreAuthenticatedProcessingFilter.class);

        RedisConnectionFactory redisConnectionFactory =
                applicationContext.getBean(RedisConnectionFactory.class);
        // 添加第三方登录回调接口过滤器
        String filterProcessesUrl = justAuthProperties.getRedirectUrlPrefix();
        Auth2LoginAuthenticationFilter auth2LoginAuthenticationFilter =
                new Auth2LoginAuthenticationFilter(
                        filterProcessesUrl,
                        justAuthProperties.getSignUpUrl(),
                        authenticationDetailsSource,
                        this.redisConnectionFactory == null
                                ? redisConnectionFactory
                                : this.redisConnectionFactory);
        AuthenticationManager sharedObject = builder.getSharedObject(AuthenticationManager.class);
        auth2LoginAuthenticationFilter.setAuthenticationManager(sharedObject);

        // 添加 RememberMeServices
        if (rememberMeServices != null) {
            // 添加rememberMe功能配置
            auth2LoginAuthenticationFilter.setRememberMeServices(rememberMeServices);
        }

        builder.addFilterAfter(
                postProcess(auth2LoginAuthenticationFilter),
                Auth2DefaultRequestRedirectFilter.class);

        ObjectProvider<JustAuthUserDetailsService> justAuthUserDetailsServiceObjectProvider =
                getBeanProvider(applicationContext, JustAuthUserDetailsService.class);
        ObjectProvider<Auth2UserService> auth2UserServiceObjectProvider =
                getBeanProvider(applicationContext, Auth2UserService.class);
        ObjectProvider<ConnectionService> connectionServiceObjectProvider =
                getBeanProvider(applicationContext, ConnectionService.class);
        ObjectProvider<AuthenticationToUserDetailsConverter>
                authenticationToUserDetailsConverterObjectProvider =
                getBeanProvider(
                        applicationContext, AuthenticationToUserDetailsConverter.class);

        ExecutorService updateConnectionTaskExecutor =
                applicationContext.getBean("updateConnectionTaskExecutor", ExecutorService.class);

        JustAuthUserDetailsService justAuthUserDetailsService =
                this.justAuthUserDetailsService == null
                        ? justAuthUserDetailsServiceObjectProvider.getIfAvailable(
                        DefaultJustAuthUserDetailsService::new)
                        : this.justAuthUserDetailsService;

        UsersConnectionRepository usersConnectionRepository =
                applicationContext.getBean(UsersConnectionRepository.class);
        UmsUserDetailsService umsUserDetailsService =
                applicationContext.getBean(UmsUserDetailsService.class);
        UsersConnectionTokenRepository usersConnectionTokenRepository =
                applicationContext.getBean(UsersConnectionTokenRepository.class);
        // 添加 provider
        com.kuma.boot.security.spring.authentication.login.social.justauth.JustAuthLoginAuthenticationProvider justAuthLoginAuthenticationProvider =
                new com.kuma.boot.security.spring.authentication.login.social.justauth.JustAuthLoginAuthenticationProvider(
                        this.auth2UserService == null
                                ? auth2UserServiceObjectProvider.getIfAvailable(
                                DefaultAuth2UserService::new)
                                : this.auth2UserService,
                        this.connectionService == null
                                ? connectionServiceObjectProvider.getIfAvailable(
                                () ->
                                        new DefaultConnectionService(
                                                umsUserDetailsService,
                                                justAuthProperties,
                                                usersConnectionRepository,
                                                usersConnectionTokenRepository,
                                                this.auth2StateCoder == null
                                                        ? auth2StateCoder
                                                        : this.auth2StateCoder))
                                : this.connectionService,
                        justAuthUserDetailsService,
                        this.updateConnectionTaskExecutor == null
                                ? updateConnectionTaskExecutor
                                : this.updateConnectionTaskExecutor,
                        justAuthProperties.getAutoSignUp(),
                        justAuthProperties.getTemporaryUserAuthorities(),
                        justAuthProperties.getTemporaryUserPassword(),
                        this.authenticationToUserDetailsConverter == null
                                ? authenticationToUserDetailsConverterObjectProvider.getIfAvailable(
                                Oauth2TokenAuthenticationTokenToUserConverter::new)
                                : this.authenticationToUserDetailsConverter);

        // 添加到 http
        if (authenticationFailureHandler != null) {
            // 添加认证失败处理器
            auth2LoginAuthenticationFilter.setAuthenticationFailureHandler(
                    authenticationFailureHandler);
        }
        if (authenticationSuccessHandler != null) {
            // 添加认证成功处理器
            auth2LoginAuthenticationFilter.setAuthenticationSuccessHandler(
                    authenticationSuccessHandler);
        }
        builder.authenticationProvider(postProcess(justAuthLoginAuthenticationProvider));

        // 忽略非法反射警告  适用于jdk11
        if (justAuthProperties.getSuppressReflectWarning()) {
            disableAccessWarnings();
        }
    }

    /**
     * 忽略非法反射警告  适用于jdk11
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void disableAccessWarnings() {
        try {
            Class unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Object unsafe = field.get(null);

            Method putObjectVolatile =
                    unsafeClass.getDeclaredMethod(
                            "putObjectVolatile", Object.class, long.class, Object.class);
            Method staticFieldOffset =
                    unsafeClass.getDeclaredMethod("staticFieldOffset", Field.class);

            Class loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field loggerField = loggerClass.getDeclaredField("logger");
            Long offset = (Long) staticFieldOffset.invoke(unsafe, loggerField);
            putObjectVolatile.invoke(unsafe, loggerClass, offset, null);
        } catch (Exception ignored) {
        }
    }

    public JustAuthLoginFilterSecurityConfigurer<H> justAuthUserDetailsService(
            JustAuthUserDetailsService justAuthUserDetailsService) {
        this.justAuthUserDetailsService = justAuthUserDetailsService;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> auth2UserService(
            Auth2UserService auth2UserService) {
        this.auth2UserService = auth2UserService;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> connectionService(
            ConnectionService connectionService) {
        this.connectionService = connectionService;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> updateConnectionTaskExecutor(
            ExecutorService updateConnectionTaskExecutor) {
        this.updateConnectionTaskExecutor = updateConnectionTaskExecutor;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> redisConnectionFactory(
            RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> authenticationToUserDetailsConverter(
            AuthenticationToUserDetailsConverter authenticationToUserDetailsConverter) {
        this.authenticationToUserDetailsConverter = authenticationToUserDetailsConverter;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> auth2StateCoder(
            Auth2StateCoder auth2StateCoder) {
        this.auth2StateCoder = auth2StateCoder;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> authenticationFailureHandler(
            AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> authenticationSuccessHandler(
            AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> rememberMeServices(
            RememberMeServices rememberMeServices) {
        this.rememberMeServices = rememberMeServices;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> authenticationDetailsSource(
            AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        this.authenticationDetailsSource = authenticationDetailsSource;
        return this;
    }
}
