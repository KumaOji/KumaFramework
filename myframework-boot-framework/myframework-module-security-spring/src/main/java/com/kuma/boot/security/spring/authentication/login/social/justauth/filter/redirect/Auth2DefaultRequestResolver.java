/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.lang.Nullable
 *  org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.filter.redirect;

import com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest;
import com.kuma.boot.security.spring.authentication.login.social.justauth.JustAuthRequestHolder;
import com.kuma.boot.security.spring.enums.ErrorCodeEnum;
import com.kuma.boot.security.spring.exception.Auth2Exception;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public final class Auth2DefaultRequestResolver
implements Auth2AuthorizationRequestResolver {
    private static final String REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId";
    private final PathPatternRequestMatcher authorizationRequestMatcher;

    public Auth2DefaultRequestResolver(String authorizationRequestBaseUri) {
        Assert.hasText((String)authorizationRequestBaseUri, (String)"authorizationRequestBaseUri cannot be empty");
        this.authorizationRequestMatcher = PathPatternRequestMatcher.withDefaults().matcher(authorizationRequestBaseUri + "/{registrationId}");
    }

    @Override
    public Auth2DefaultRequest resolve(HttpServletRequest request) throws Auth2Exception {
        if (StringUtils.hasText((String)request.getParameter("state"))) {
            return null;
        }
        String registrationId = this.resolveRegistrationId(request);
        return this.getAuth2DefaultRequest(registrationId);
    }

    @Override
    public Auth2DefaultRequest resolve(HttpServletRequest request, String registrationId) throws Auth2Exception {
        if (StringUtils.hasText((String)request.getParameter("state"))) {
            return null;
        }
        return this.getAuth2DefaultRequest(registrationId);
    }

    public String resolveRegistrationId(HttpServletRequest request) {
        if (this.authorizationRequestMatcher.matches(request)) {
            return (String)this.authorizationRequestMatcher.matcher(request).getVariables().get(REGISTRATION_ID_URI_VARIABLE_NAME);
        }
        return null;
    }

    @Nullable
    private Auth2DefaultRequest getAuth2DefaultRequest(@Nullable String registrationId) throws Auth2Exception {
        if (registrationId == null) {
            return null;
        }
        Auth2DefaultRequest auth2DefaultRequest = JustAuthRequestHolder.getAuth2DefaultRequest(registrationId);
        if (auth2DefaultRequest == null) {
            throw new Auth2Exception(ErrorCodeEnum.AUTH2_PROVIDER_NOT_SUPPORT, registrationId);
        }
        return auth2DefaultRequest;
    }
}

