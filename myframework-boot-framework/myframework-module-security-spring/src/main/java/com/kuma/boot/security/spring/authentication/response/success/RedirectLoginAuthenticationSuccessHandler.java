/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.servlet.ResponseUtils
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  jakarta.servlet.http.HttpSession
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.web.authentication.AuthenticationSuccessHandler
 *  org.springframework.security.web.savedrequest.HttpSessionRequestCache
 *  org.springframework.security.web.savedrequest.RequestCache
 *  org.springframework.security.web.savedrequest.SavedRequest
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.response.success;

import com.kuma.boot.common.utils.servlet.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.Assert;

public class RedirectLoginAuthenticationSuccessHandler
implements AuthenticationSuccessHandler {
    private RequestCache requestCache;
    private static final String defaultTargetUrl = "/";
    private final String redirect;

    public RedirectLoginAuthenticationSuccessHandler() {
        this(defaultTargetUrl, (RequestCache)new HttpSessionRequestCache());
    }

    public RedirectLoginAuthenticationSuccessHandler(String redirect, RequestCache requestCache) {
        Assert.notNull((Object)requestCache, (String)"requestCache must not be null");
        this.redirect = redirect;
        this.requestCache = requestCache;
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        SavedRequest savedRequest = this.requestCache.getRequest(request, response);
        String targetUrl = savedRequest == null ? this.redirect : savedRequest.getRedirectUrl();
        this.clearAuthenticationAttributes(request);
        ResponseUtils.success((HttpServletResponse)response, (Object)targetUrl);
    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }

    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        }
    }
}

