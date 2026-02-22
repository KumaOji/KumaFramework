/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.common.JsonUtils
 *  com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest
 *  jakarta.servlet.FilterChain
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.data.redis.connection.RedisConnection
 *  org.springframework.data.redis.connection.RedisConnectionFactory
 *  org.springframework.data.redis.connection.RedisStringCommands$SetOption
 *  org.springframework.data.redis.core.types.Expiration
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.security.authentication.AuthenticationDetailsSource
 *  org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.core.context.SecurityContextHolder
 *  org.springframework.security.oauth2.core.OAuth2AuthenticationException
 *  org.springframework.security.oauth2.core.OAuth2Error
 *  org.springframework.security.web.DefaultRedirectStrategy
 *  org.springframework.security.web.RedirectStrategy
 *  org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.filter.login;

import com.kuma.boot.common.utils.common.JsonUtils;
import com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest;
import com.kuma.boot.security.spring.authentication.login.social.justauth.JustAuthLoginAuthenticationToken;
import com.kuma.boot.security.spring.authentication.login.social.justauth.JustAuthRequestHolder;
import com.kuma.boot.security.spring.authentication.login.social.justauth.filter.redirect.Auth2DefaultRequestResolver;
import com.kuma.boot.security.spring.authentication.login.social.justauth.userdetails.TemporaryUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.springframework.context.ApplicationEvent;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

public class Auth2LoginAuthenticationFilter
extends AbstractAuthenticationProcessingFilter {
    public static final String TEMPORARY_USER_CACHE_KEY_PREFIX = "TEMPORARY_USER_REDIS_CACHE_KEY:";
    public static final String TEMPORARY_USERNAME_PARAM_NAME = "temporary_username";
    private static final String AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE = "authorization_request_not_found";
    private final Auth2DefaultRequestResolver authorizationRequestResolver;
    private final RedisConnectionFactory redisConnectionFactory;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final String signUpUrl;

    public Auth2LoginAuthenticationFilter(@NonNull String filterProcessesUrl, @Nullable String signUpUrl, @Nullable AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource, @Nullable RedisConnectionFactory redisConnectionFactory) {
        super(filterProcessesUrl + "/*");
        this.authorizationRequestResolver = new Auth2DefaultRequestResolver(filterProcessesUrl);
        this.signUpUrl = signUpUrl;
        this.redisConnectionFactory = redisConnectionFactory;
        if (authenticationDetailsSource != null) {
            this.setAuthenticationDetailsSource(authenticationDetailsSource);
        }
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        MultiValueMap<String, String> params = Auth2AuthorizationResponseUtils.toMultiMap(request.getParameterMap());
        if (!Auth2AuthorizationResponseUtils.isAuthorizationResponse(params)) {
            OAuth2Error oauth2Error = new OAuth2Error("invalid_request");
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        String registrationId = this.authorizationRequestResolver.resolveRegistrationId(request);
        Auth2DefaultRequest auth2DefaultRequest = null;
        if (StringUtils.hasText((String)registrationId)) {
            auth2DefaultRequest = JustAuthRequestHolder.getAuth2DefaultRequest(registrationId);
        }
        if (auth2DefaultRequest == null) {
            OAuth2Error oauth2Error = new OAuth2Error(AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE, "Client Registration not found with Id: " + registrationId, null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        JustAuthLoginAuthenticationToken authenticationRequest = new JustAuthLoginAuthenticationToken(auth2DefaultRequest, request);
        this.setDetails(request, authenticationRequest);
        return this.getAuthenticationManager().authenticate((Authentication)authenticationRequest);
    }

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        Object principal;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Authentication success. Updating SecurityContextHolder to contain: " + String.valueOf(authResult)));
        }
        SecurityContextHolder.getContext().setAuthentication(authResult);
        if (this.eventPublisher != null) {
            this.eventPublisher.publishEvent((ApplicationEvent)new InteractiveAuthenticationSuccessEvent(authResult, ((Object)((Object)this)).getClass()));
        }
        if ((principal = authResult.getPrincipal()) instanceof TemporaryUser) {
            TemporaryUser temporaryUser = (TemporaryUser)principal;
            if (StringUtils.hasText((String)this.signUpUrl)) {
                String username = temporaryUser.getUsername();
                String key = TEMPORARY_USER_CACHE_KEY_PREFIX + username;
                if (Objects.nonNull(this.redisConnectionFactory)) {
                    try (RedisConnection connection = this.redisConnectionFactory.getConnection();){
                        connection.stringCommands().set(key.getBytes(StandardCharsets.UTF_8), JsonUtils.toJson((Object)temporaryUser).getBytes(StandardCharsets.UTF_8), Expiration.from((long)86400L, (TimeUnit)TimeUnit.SECONDS), RedisStringCommands.SetOption.UPSERT);
                    }
                } else {
                    request.getSession().setAttribute(key, (Object)temporaryUser);
                }
                this.redirectStrategy.sendRedirect(request, response, this.signUpUrl + "?temporary_username=" + URLEncoder.encode(username, StandardCharsets.UTF_8));
                return;
            }
        }
        this.getRememberMeServices().loginSuccess(request, response, authResult);
        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }

    protected void setDetails(HttpServletRequest request, JustAuthLoginAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails((Object)request));
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }
}

