/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest
 *  jakarta.servlet.FilterChain
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.core.log.LogMessage
 *  org.springframework.http.HttpStatus
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.web.DefaultRedirectStrategy
 *  org.springframework.security.web.RedirectStrategy
 *  org.springframework.security.web.authentication.AuthenticationFailureHandler
 *  org.springframework.security.web.savedrequest.HttpSessionRequestCache
 *  org.springframework.security.web.savedrequest.RequestCache
 *  org.springframework.security.web.util.ThrowableAnalyzer
 *  org.springframework.util.Assert
 *  org.springframework.web.filter.OncePerRequestFilter
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.filter.redirect;

import com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest;
import com.kuma.boot.security.spring.authentication.login.social.justauth.service.Auth2StateCoder;
import com.kuma.boot.security.spring.exception.Auth2Exception;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

public class Auth2DefaultRequestRedirectFilter
extends OncePerRequestFilter {
    private final ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();
    private final RedirectStrategy authorizationRedirectStrategy = new DefaultRedirectStrategy();
    private final Auth2DefaultRequestResolver authorizationRequestResolver;
    private final Auth2StateCoder auth2StateCoder;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private RequestCache requestCache = new HttpSessionRequestCache();

    public Auth2DefaultRequestRedirectFilter(@NonNull String authorizationRequestBaseUri, @Nullable Auth2StateCoder auth2StateCoder, @NonNull AuthenticationFailureHandler authenticationFailureHandler) {
        Assert.hasText((String)authorizationRequestBaseUri, (String)"authorizationRequestBaseUri cannot be empty");
        this.authorizationRequestResolver = new Auth2DefaultRequestResolver(authorizationRequestBaseUri);
        this.auth2StateCoder = auth2StateCoder;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    public Auth2DefaultRequestRedirectFilter(Auth2DefaultRequestResolver authorizationRequestResolver, @Nullable Auth2StateCoder auth2StateCoder, @NonNull AuthenticationFailureHandler authenticationFailureHandler) {
        Assert.notNull((Object)authorizationRequestResolver, (String)"authorizationRequestResolver cannot be null");
        this.authorizationRequestResolver = authorizationRequestResolver;
        this.auth2StateCoder = auth2StateCoder;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    public final void setRequestCache(RequestCache requestCache) {
        Assert.notNull((Object)requestCache, (String)"requestCache cannot be null");
        this.requestCache = requestCache;
    }

    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            Auth2DefaultRequest authorizationRequest = this.authorizationRequestResolver.resolve(request);
            if (authorizationRequest != null) {
                this.sendRedirectForAuthorization(request, response, authorizationRequest);
                return;
            }
        }
        catch (Auth2Exception ex) {
            this.authenticationFailureHandler.onAuthenticationFailure(request, response, (AuthenticationException)ex);
            return;
        }
        catch (Exception ex) {
            this.logger.error((Object)ex.getMessage(), (Throwable)ex);
            this.unsuccessfulRedirectForAuthorization(request, response, ex);
            return;
        }
        try {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
        catch (IOException ex) {
            throw ex;
        }
        catch (Exception ex) {
            Throwable[] causeChain = this.throwableAnalyzer.determineCauseChain((Throwable)ex);
            AuthenticationException authzEx = (AuthenticationException)this.throwableAnalyzer.getFirstThrowableOfType(AuthenticationException.class, causeChain);
            if (authzEx != null) {
                throw authzEx;
            }
            if (ex instanceof ServletException) {
                throw (ServletException)ex;
            }
            if (ex instanceof RuntimeException) {
                throw (RuntimeException)ex;
            }
            throw new RuntimeException(ex);
        }
    }

    private void sendRedirectForAuthorization(HttpServletRequest request, HttpServletResponse response, Auth2DefaultRequest authorizationRequest) throws IOException {
        String state = authorizationRequest.generateState();
        if (this.auth2StateCoder != null) {
            state = this.auth2StateCoder.encode(state, request);
        }
        String authorize = authorizationRequest.authorize(state);
        this.authorizationRedirectStrategy.sendRedirect(request, response, authorize);
    }

    private void unsuccessfulRedirectForAuthorization(HttpServletRequest request, HttpServletResponse response, Exception ex) throws IOException {
        this.logger.error((Object)LogMessage.format((String)"Authorization Request failed: %s", (Object)ex, (Object)ex));
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    private static final class DefaultThrowableAnalyzer
    extends ThrowableAnalyzer {
        private DefaultThrowableAnalyzer() {
        }

        protected void initExtractorMap() {
            super.initExtractorMap();
            this.registerExtractor(ServletException.class, throwable -> {
                ThrowableAnalyzer.verifyThrowableHierarchy((Throwable)throwable, ServletException.class);
                return ((ServletException)throwable).getRootCause();
            });
        }
    }
}

