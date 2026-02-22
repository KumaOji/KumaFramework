/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.model.Result
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  jakarta.servlet.http.HttpSession
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.http.HttpStatus
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.web.DefaultRedirectStrategy
 *  org.springframework.security.web.RedirectStrategy
 *  org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
 *  org.springframework.security.web.util.UrlUtils
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.response.failure;

import com.kuma.boot.common.model.Result;
import com.kuma.boot.security.spring.exception.SecurityGlobalExceptionHandler;
import com.kuma.boot.security.spring.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

public class FormLoginAuthenticationFailureHandler
extends SimpleUrlAuthenticationFailureHandler {
    private static final Logger log = LoggerFactory.getLogger(FormLoginAuthenticationFailureHandler.class);
    private String defaultFailureUrl;
    private boolean forwardToDestination = false;
    private boolean allowSessionCreation = true;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public FormLoginAuthenticationFailureHandler() {
    }

    public FormLoginAuthenticationFailureHandler(String defaultFailureUrl) {
        this.setDefaultFailureUrl(defaultFailureUrl);
    }

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        if (this.defaultFailureUrl == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)"Sending 401 Unauthorized error since no failure URL is set");
            } else {
                this.logger.debug((Object)"Sending 401 Unauthorized error");
            }
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
            return;
        }
        String errorMessage = "\u8bf7\u5237\u65b0\u91cd\u8bd5\uff01";
        Result<String> result = SecurityGlobalExceptionHandler.resolveSecurityException((Exception)((Object)e), request.getRequestURI());
        if (ObjectUtils.isNotEmpty(result) && StringUtils.isNotBlank((CharSequence)result.getMessage())) {
            errorMessage = result.getMessage();
        } else {
            errorMessage = ((Object)((Object)e)).getClass().getSimpleName();
            log.info("Form Login Authentication Failure Handler,  Can not find the exception name [{}] in dictionary, please do optimize ", (Object)errorMessage);
        }
        this.saveException(request, errorMessage);
        if (this.isUseForward()) {
            log.info("Forwarding to " + this.defaultFailureUrl);
            request.getRequestDispatcher(this.defaultFailureUrl).forward((ServletRequest)request, (ServletResponse)response);
        } else {
            this.redirectStrategy.sendRedirect(request, response, this.defaultFailureUrl);
        }
    }

    protected final void saveException(HttpServletRequest request, String message) {
        if (this.isUseForward()) {
            request.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", (Object)message);
            return;
        }
        HttpSession session = WebUtils.getSession(request);
        if (session != null || this.isAllowSessionCreation()) {
            request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", (Object)message);
        }
    }

    public void setDefaultFailureUrl(String defaultFailureUrl) {
        Assert.isTrue((boolean)UrlUtils.isValidRedirectUrl((String)defaultFailureUrl), () -> "'" + defaultFailureUrl + "' is not a valid redirect URL");
        this.defaultFailureUrl = defaultFailureUrl;
    }

    protected boolean isUseForward() {
        return this.forwardToDestination;
    }

    public void setUseForward(boolean forwardToDestination) {
        this.forwardToDestination = forwardToDestination;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return this.redirectStrategy;
    }

    protected boolean isAllowSessionCreation() {
        return this.allowSessionCreation;
    }

    public void setAllowSessionCreation(boolean allowSessionCreation) {
        this.allowSessionCreation = allowSessionCreation;
    }
}

