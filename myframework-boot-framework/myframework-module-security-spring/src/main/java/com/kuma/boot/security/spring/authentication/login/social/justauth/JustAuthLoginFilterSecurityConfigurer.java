/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.Filter
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.context.ApplicationContext
 *  org.springframework.data.redis.connection.RedisConnectionFactory
 *  org.springframework.security.authentication.AuthenticationDetailsSource
 *  org.springframework.security.authentication.AuthenticationManager
 *  org.springframework.security.authentication.AuthenticationProvider
 *  org.springframework.security.config.annotation.web.HttpSecurityBuilder
 *  org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
 *  org.springframework.security.web.authentication.AuthenticationFailureHandler
 *  org.springframework.security.web.authentication.AuthenticationSuccessHandler
 *  org.springframework.security.web.authentication.RememberMeServices
 *  org.springframework.security.web.authentication.logout.LogoutFilter
 *  org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth;

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
import com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

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

    public void configure(H builder) {
        ApplicationContext applicationContext = (ApplicationContext)builder.getSharedObject(ApplicationContext.class);
        JustAuthProperties justAuthProperties = (JustAuthProperties)applicationContext.getBean(JustAuthProperties.class);
        builder.addFilterBefore((Filter)new JsonRequestFilter(), LogoutFilter.class);
        Auth2StateCoder auth2StateCoder = (Auth2StateCoder)applicationContext.getBean(Auth2StateCoder.class);
        String authorizationRequestBaseUri = justAuthProperties.getAuthLoginUrlPrefix();
        Auth2DefaultRequestRedirectFilter auth2DefaultRequestRedirectFilter = new Auth2DefaultRequestRedirectFilter(authorizationRequestBaseUri, this.auth2StateCoder == null ? auth2StateCoder : this.auth2StateCoder, this.authenticationFailureHandler);
        builder.addFilterAfter((Filter)auth2DefaultRequestRedirectFilter, AbstractPreAuthenticatedProcessingFilter.class);
        RedisConnectionFactory redisConnectionFactory = (RedisConnectionFactory)applicationContext.getBean(RedisConnectionFactory.class);
        String filterProcessesUrl = justAuthProperties.getRedirectUrlPrefix();
        Auth2LoginAuthenticationFilter auth2LoginAuthenticationFilter = new Auth2LoginAuthenticationFilter(filterProcessesUrl, justAuthProperties.getSignUpUrl(), this.authenticationDetailsSource, this.redisConnectionFactory == null ? redisConnectionFactory : this.redisConnectionFactory);
        AuthenticationManager sharedObject = (AuthenticationManager)builder.getSharedObject(AuthenticationManager.class);
        auth2LoginAuthenticationFilter.setAuthenticationManager(sharedObject);
        if (this.rememberMeServices != null) {
            auth2LoginAuthenticationFilter.setRememberMeServices(this.rememberMeServices);
        }
        builder.addFilterAfter((Filter)this.postProcess((Object)auth2LoginAuthenticationFilter), Auth2DefaultRequestRedirectFilter.class);
        ObjectProvider<JustAuthUserDetailsService> justAuthUserDetailsServiceObjectProvider = OAuth2AuthorizationUtils.getBeanProvider(applicationContext, JustAuthUserDetailsService.class);
        ObjectProvider<Auth2UserService> auth2UserServiceObjectProvider = OAuth2AuthorizationUtils.getBeanProvider(applicationContext, Auth2UserService.class);
        ObjectProvider<ConnectionService> connectionServiceObjectProvider = OAuth2AuthorizationUtils.getBeanProvider(applicationContext, ConnectionService.class);
        ObjectProvider<AuthenticationToUserDetailsConverter> authenticationToUserDetailsConverterObjectProvider = OAuth2AuthorizationUtils.getBeanProvider(applicationContext, AuthenticationToUserDetailsConverter.class);
        ExecutorService updateConnectionTaskExecutor = (ExecutorService)applicationContext.getBean("updateConnectionTaskExecutor", ExecutorService.class);
        JustAuthUserDetailsService justAuthUserDetailsService = this.justAuthUserDetailsService == null ? (JustAuthUserDetailsService)justAuthUserDetailsServiceObjectProvider.getIfAvailable(DefaultJustAuthUserDetailsService::new) : this.justAuthUserDetailsService;
        UsersConnectionRepository usersConnectionRepository = (UsersConnectionRepository)applicationContext.getBean(UsersConnectionRepository.class);
        UmsUserDetailsService umsUserDetailsService = (UmsUserDetailsService)applicationContext.getBean(UmsUserDetailsService.class);
        UsersConnectionTokenRepository usersConnectionTokenRepository = (UsersConnectionTokenRepository)applicationContext.getBean(UsersConnectionTokenRepository.class);
        JustAuthLoginAuthenticationProvider justAuthLoginAuthenticationProvider = new JustAuthLoginAuthenticationProvider(this.auth2UserService == null ? (Auth2UserService)auth2UserServiceObjectProvider.getIfAvailable(DefaultAuth2UserService::new) : this.auth2UserService, this.connectionService == null ? (ConnectionService)connectionServiceObjectProvider.getIfAvailable(() -> new DefaultConnectionService(umsUserDetailsService, justAuthProperties, usersConnectionRepository, usersConnectionTokenRepository, this.auth2StateCoder == null ? auth2StateCoder : this.auth2StateCoder)) : this.connectionService, justAuthUserDetailsService, this.updateConnectionTaskExecutor == null ? updateConnectionTaskExecutor : this.updateConnectionTaskExecutor, justAuthProperties.getAutoSignUp(), justAuthProperties.getTemporaryUserAuthorities(), justAuthProperties.getTemporaryUserPassword(), this.authenticationToUserDetailsConverter == null ? (AuthenticationToUserDetailsConverter)authenticationToUserDetailsConverterObjectProvider.getIfAvailable(Oauth2TokenAuthenticationTokenToUserConverter::new) : this.authenticationToUserDetailsConverter);
        if (this.authenticationFailureHandler != null) {
            auth2LoginAuthenticationFilter.setAuthenticationFailureHandler(this.authenticationFailureHandler);
        }
        if (this.authenticationSuccessHandler != null) {
            auth2LoginAuthenticationFilter.setAuthenticationSuccessHandler(this.authenticationSuccessHandler);
        }
        builder.authenticationProvider((AuthenticationProvider)this.postProcess(justAuthLoginAuthenticationProvider));
        if (justAuthProperties.getSuppressReflectWarning().booleanValue()) {
            JustAuthLoginFilterSecurityConfigurer.disableAccessWarnings();
        }
    }

    private static void disableAccessWarnings() {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Object unsafe = field.get(null);
            Method putObjectVolatile = unsafeClass.getDeclaredMethod("putObjectVolatile", Object.class, Long.TYPE, Object.class);
            Method staticFieldOffset = unsafeClass.getDeclaredMethod("staticFieldOffset", Field.class);
            Class<?> loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field loggerField = loggerClass.getDeclaredField("logger");
            Long offset = (Long)staticFieldOffset.invoke(unsafe, loggerField);
            putObjectVolatile.invoke(unsafe, loggerClass, offset, null);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public JustAuthLoginFilterSecurityConfigurer<H> justAuthUserDetailsService(JustAuthUserDetailsService justAuthUserDetailsService) {
        this.justAuthUserDetailsService = justAuthUserDetailsService;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> auth2UserService(Auth2UserService auth2UserService) {
        this.auth2UserService = auth2UserService;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> connectionService(ConnectionService connectionService) {
        this.connectionService = connectionService;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> updateConnectionTaskExecutor(ExecutorService updateConnectionTaskExecutor) {
        this.updateConnectionTaskExecutor = updateConnectionTaskExecutor;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> redisConnectionFactory(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> authenticationToUserDetailsConverter(AuthenticationToUserDetailsConverter authenticationToUserDetailsConverter) {
        this.authenticationToUserDetailsConverter = authenticationToUserDetailsConverter;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> auth2StateCoder(Auth2StateCoder auth2StateCoder) {
        this.auth2StateCoder = auth2StateCoder;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> authenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> authenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> rememberMeServices(RememberMeServices rememberMeServices) {
        this.rememberMeServices = rememberMeServices;
        return this;
    }

    public JustAuthLoginFilterSecurityConfigurer<H> authenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        this.authenticationDetailsSource = authenticationDetailsSource;
        return this;
    }
}

