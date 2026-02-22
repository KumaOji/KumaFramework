/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.Filter
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.context.ApplicationContext
 *  org.springframework.http.MediaType
 *  org.springframework.security.authentication.AuthenticationDetailsSource
 *  org.springframework.security.authentication.AuthenticationManager
 *  org.springframework.security.authentication.AuthenticationProvider
 *  org.springframework.security.config.annotation.SecurityConfigurerAdapter
 *  org.springframework.security.config.annotation.web.HttpSecurityBuilder
 *  org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
 *  org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
 *  org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer$AuthorizedUrl
 *  org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
 *  org.springframework.security.web.AuthenticationEntryPoint
 *  org.springframework.security.web.DefaultSecurityFilterChain
 *  org.springframework.security.web.PortMapper
 *  org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
 *  org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler
 *  org.springframework.security.web.authentication.AuthenticationFailureHandler
 *  org.springframework.security.web.authentication.AuthenticationSuccessHandler
 *  org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
 *  org.springframework.security.web.authentication.RememberMeServices
 *  org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
 *  org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
 *  org.springframework.security.web.authentication.logout.LogoutFilter
 *  org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
 *  org.springframework.security.web.savedrequest.RequestCache
 *  org.springframework.security.web.util.matcher.AndRequestMatcher
 *  org.springframework.security.web.util.matcher.MediaTypeRequestMatcher
 *  org.springframework.security.web.util.matcher.NegatedRequestMatcher
 *  org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher
 *  org.springframework.security.web.util.matcher.RequestMatcher
 *  org.springframework.util.Assert
 *  org.springframework.web.accept.ContentNegotiationStrategy
 *  org.springframework.web.accept.HeaderContentNegotiationStrategy
 */
package com.kuma.boot.security.spring.authentication.login.extension;

import com.kuma.boot.security.spring.authentication.response.entrypoint.JsonAuthenticationEntryPoint;
import com.kuma.boot.security.spring.authentication.response.failure.JsonExtensionLoginAuthenticationFailureHandler;
import com.kuma.boot.security.spring.authentication.response.success.JsonExtensionLoginAuthenticationSuccessHandler;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.PortMapper;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

public abstract class AbstractExtensionLoginFilterConfigurer<H extends HttpSecurityBuilder<H>, C extends AbstractExtensionLoginFilterConfigurer<H, C, F, A>, F extends AbstractAuthenticationProcessingFilter, A extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, H>>
extends AbstractHttpConfigurer<AbstractExtensionLoginFilterConfigurer<H, C, F, A>, H> {
    private final A configurerAdapter;
    private F authFilter;
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;
    private AuthenticationSuccessHandler successHandler;
    private AuthenticationEntryPoint authenticationEntryPoint;
    private String loginProcessingUrl;
    private AuthenticationFailureHandler failureHandler;
    private boolean permitAll;
    private String failureUrl;
    private JwtTokenGenerator jwtTokenGenerator;

    public AbstractExtensionLoginFilterConfigurer(A configurerAdapter, F authenticationFilter, String defaultLoginProcessingUrl) {
        this.configurerAdapter = configurerAdapter;
        this.authFilter = authenticationFilter;
        if (defaultLoginProcessingUrl != null) {
            this.loginProcessingUrl(defaultLoginProcessingUrl);
        }
    }

    public final C defaultSuccessUrl(String defaultSuccessUrl) {
        return this.defaultSuccessUrl(defaultSuccessUrl, false);
    }

    public final C defaultSuccessUrl(String defaultSuccessUrl, boolean alwaysUse) {
        SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl(defaultSuccessUrl);
        handler.setAlwaysUseDefaultTargetUrl(alwaysUse);
        return this.successHandler((AuthenticationSuccessHandler)handler);
    }

    public C loginProcessingUrl(String loginProcessingUrl) {
        this.loginProcessingUrl = loginProcessingUrl;
        this.authFilter.setRequiresAuthenticationRequestMatcher(this.createLoginProcessingUrlMatcher(loginProcessingUrl));
        return this.getSelf();
    }

    protected abstract RequestMatcher createLoginProcessingUrlMatcher(String var1);

    public final C authenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        this.authenticationDetailsSource = authenticationDetailsSource;
        return this.getSelf();
    }

    public final C successHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
        return this.getSelf();
    }

    public final C authenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        return this.getSelf();
    }

    public final C permitAll() {
        return this.permitAll(true);
    }

    public final C permitAll(boolean permitAll) {
        this.permitAll = permitAll;
        return this.getSelf();
    }

    public final C failureUrl(String authenticationFailureUrl) {
        C result = this.failureHandler((AuthenticationFailureHandler)new SimpleUrlAuthenticationFailureHandler(authenticationFailureUrl));
        this.failureUrl = authenticationFailureUrl;
        return result;
    }

    public final C failureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.failureUrl = null;
        this.failureHandler = authenticationFailureHandler;
        return this.getSelf();
    }

    public A with() {
        return this.configurerAdapter;
    }

    public void init(H http) {
        this.updateAccessDefaults(http);
        this.registerDefaultAuthenticationEntryPoint(http);
        AuthenticationProvider authenticationProvider = this.authenticationProvider(http);
        http.authenticationProvider((AuthenticationProvider)this.postProcess(authenticationProvider));
        if (this.successHandler == null) {
            this.successHandler(this.defaultSuccessHandler(http));
        }
        if (this.failureHandler == null) {
            this.failureHandler(this.defaultFailureHandler(http));
        }
    }

    protected abstract AuthenticationProvider authenticationProvider(H var1);

    protected AuthenticationSuccessHandler defaultSuccessHandler(H http) {
        if (this.jwtTokenGenerator == null) {
            ApplicationContext applicationContext = (ApplicationContext)http.getSharedObject(ApplicationContext.class);
            this.jwtTokenGenerator = this.getBeanOrNull(applicationContext, JwtTokenGenerator.class);
        }
        Assert.notNull((Object)this.jwtTokenGenerator, (String)"jwtTokenGenerator is required");
        SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        savedRequestAwareAuthenticationSuccessHandler.setDefaultTargetUrl("/login");
        savedRequestAwareAuthenticationSuccessHandler.setAlwaysUseDefaultTargetUrl(false);
        return new JsonExtensionLoginAuthenticationSuccessHandler(savedRequestAwareAuthenticationSuccessHandler, this.jwtTokenGenerator);
    }

    public JwtTokenGenerator getJwtTokenGenerator() {
        return this.jwtTokenGenerator;
    }

    public void setJwtTokenGenerator(JwtTokenGenerator jwtTokenGenerator) {
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    protected AuthenticationFailureHandler defaultFailureHandler(H http) {
        return new JsonExtensionLoginAuthenticationFailureHandler();
    }

    protected final void updateAuthenticationDefaults() {
        if (this.failureHandler == null) {
            this.failureHandler((AuthenticationFailureHandler)new AuthenticationEntryPointFailureHandler((AuthenticationEntryPoint)new JsonAuthenticationEntryPoint()));
        }
    }

    protected final void registerDefaultAuthenticationEntryPoint(H http) {
        if (this.authenticationEntryPoint != null) {
            this.authenticationEntryPoint = new JsonAuthenticationEntryPoint();
        }
        this.registerAuthenticationEntryPoint(http, this.authenticationEntryPoint);
    }

    protected final void registerAuthenticationEntryPoint(H http, AuthenticationEntryPoint authenticationEntryPoint) {
        ExceptionHandlingConfigurer exceptionHandling = (ExceptionHandlingConfigurer)http.getConfigurer(ExceptionHandlingConfigurer.class);
        if (exceptionHandling == null) {
            return;
        }
        exceptionHandling.defaultAuthenticationEntryPointFor((AuthenticationEntryPoint)this.postProcess(authenticationEntryPoint), this.getAuthenticationEntryPointMatcher(http));
    }

    protected final RequestMatcher getAuthenticationEntryPointMatcher(H http) {
        ContentNegotiationStrategy contentNegotiationStrategy = (ContentNegotiationStrategy)http.getSharedObject(ContentNegotiationStrategy.class);
        if (contentNegotiationStrategy == null) {
            contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
        }
        MediaTypeRequestMatcher mediaMatcher = new MediaTypeRequestMatcher(contentNegotiationStrategy, new MediaType[]{MediaType.APPLICATION_XHTML_XML, new MediaType("image", "*"), MediaType.TEXT_HTML, MediaType.TEXT_PLAIN});
        mediaMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
        NegatedRequestMatcher notXRequestedWith = new NegatedRequestMatcher((RequestMatcher)new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));
        return new AndRequestMatcher(Arrays.asList(notXRequestedWith, mediaMatcher));
    }

    public void configure(H http) throws Exception {
        RememberMeServices rememberMeServices;
        SessionAuthenticationStrategy sessionAuthenticationStrategy;
        RequestCache requestCache;
        PortMapper portMapper = (PortMapper)http.getSharedObject(PortMapper.class);
        if (portMapper != null && this.authenticationEntryPoint instanceof LoginUrlAuthenticationEntryPoint) {
            ((LoginUrlAuthenticationEntryPoint)this.authenticationEntryPoint).setPortMapper(portMapper);
        }
        if ((requestCache = (RequestCache)http.getSharedObject(RequestCache.class)) != null && this.successHandler instanceof SavedRequestAwareAuthenticationSuccessHandler) {
            ((SavedRequestAwareAuthenticationSuccessHandler)this.successHandler).setRequestCache(requestCache);
        }
        this.authFilter.setAuthenticationManager((AuthenticationManager)http.getSharedObject(AuthenticationManager.class));
        this.authFilter.setAuthenticationSuccessHandler(this.successHandler);
        this.authFilter.setAuthenticationFailureHandler(this.failureHandler);
        if (this.authenticationDetailsSource != null) {
            this.authFilter.setAuthenticationDetailsSource(this.authenticationDetailsSource);
        }
        if ((sessionAuthenticationStrategy = (SessionAuthenticationStrategy)http.getSharedObject(SessionAuthenticationStrategy.class)) != null) {
            this.authFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }
        if ((rememberMeServices = (RememberMeServices)http.getSharedObject(RememberMeServices.class)) != null) {
            this.authFilter.setRememberMeServices(rememberMeServices);
        }
        AbstractAuthenticationProcessingFilter filter = (AbstractAuthenticationProcessingFilter)this.postProcess(this.authFilter);
        http.addFilterBefore((Filter)filter, LogoutFilter.class);
    }

    public final <T> T getBeanOrNull(ApplicationContext applicationContext, Class<T> beanType) {
        String[] beanNames = applicationContext.getBeanNamesForType(beanType);
        if (beanNames.length == 1) {
            return (T)applicationContext.getBean(beanNames[0], beanType);
        }
        return null;
    }

    protected final F getAuthenticationFilter() {
        return this.authFilter;
    }

    protected final void setAuthenticationFilter(F authFilter) {
        this.authFilter = authFilter;
    }

    protected final AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return this.authenticationEntryPoint;
    }

    protected final String getLoginProcessingUrl() {
        return this.loginProcessingUrl;
    }

    protected final String getFailureUrl() {
        return this.failureUrl;
    }

    protected final void updateAccessDefaults(H http) {
        if (this.permitAll) {
            PermitAllSupport.permitAll(http, new String[]{this.loginProcessingUrl, this.failureUrl});
        }
    }

    private C getSelf() {
        return (C)((Object)this);
    }

    static final class PermitAllSupport {
        private PermitAllSupport() {
        }

        private static void permitAll(HttpSecurityBuilder<? extends HttpSecurityBuilder<?>> http, String ... urls) {
            for (String url : urls) {
                if (url == null) continue;
                PermitAllSupport.permitAll(http, new ExactUrlRequestMatcher(url));
            }
        }

        static void permitAll(HttpSecurityBuilder<? extends HttpSecurityBuilder<?>> http, RequestMatcher ... requestMatchers) {
            AuthorizeHttpRequestsConfigurer configurer = (AuthorizeHttpRequestsConfigurer)http.getConfigurer(AuthorizeHttpRequestsConfigurer.class);
            Assert.state((configurer != null ? 1 : 0) != 0, (String)"permitAll only works with HttpSecurity.authorizeRequests()");
            ((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)configurer.getRegistry().requestMatchers(requestMatchers)).permitAll();
        }

        private static final class ExactUrlRequestMatcher
        implements RequestMatcher {
            private final String processUrl;

            private ExactUrlRequestMatcher(String processUrl) {
                this.processUrl = processUrl;
            }

            public boolean matches(HttpServletRequest request) {
                Object uri = request.getRequestURI();
                String query = request.getQueryString();
                if (query != null) {
                    uri = (String)uri + "?" + query;
                }
                if ("".equals(request.getContextPath())) {
                    return ((String)uri).equals(this.processUrl);
                }
                return ((String)uri).equals(request.getContextPath() + this.processUrl);
            }

            public String toString() {
                return "ExactUrl [processUrl='" + this.processUrl + "']";
            }
        }
    }
}

