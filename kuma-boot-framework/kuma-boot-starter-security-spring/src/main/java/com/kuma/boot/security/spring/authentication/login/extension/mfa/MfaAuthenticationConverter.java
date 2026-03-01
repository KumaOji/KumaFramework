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

package com.kuma.boot.security.spring.authentication.login.extension.mfa;

import com.kuma.boot.security.spring.authentication.login.extension.mfa.authentication.MfaAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

/**
 * @author: ReLive27
 * @since: 2023/1/8 21:11
 */
public class MfaAuthenticationConverter implements AuthenticationConverter {
    public static final String SPRING_SECURITY_MFA_PARAM_NAME = "code";
    private RequestMatcher requestMatcher = createLoginRequestMatcher();

    @Nullable
    @Override
    public Authentication convert(HttpServletRequest request) {

        String username = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            username = authentication.getName();
            authentication.setAuthenticated(false);
        } else {

            if (requestMatcher.matches(request)) {
                username = request.getParameter("username");
            }
        }

        if (!StringUtils.hasText(username)) {
            return null;
        }

        String secret = this.obtainSecret(request);
        if (StringUtils.hasText(secret)) {
            if (authentication != null) {
                authentication.setAuthenticated(true);
            }
            return new MfaAuthenticationToken(username, secret);
        }

        return null;
    }

    @Nullable
    protected String obtainSecret(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_MFA_PARAM_NAME);
    }

    private static RequestMatcher createLoginRequestMatcher() {
        return PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login");
    }

    public void setRequestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }
}
