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

package com.kuma.boot.security.spring.authentication.login.form.captcha;

import com.kuma.boot.security.spring.utils.SymmetricUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * <p>OAuth2 表单登录过滤器 </p>
 *
 * @since : 2022/4/12 11:08
 */
public class FormCaptchaLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private boolean postOnly = true;

    private static final Logger log =
            LoggerFactory.getLogger(FormCaptchaLoginAuthenticationFilter.class);

    public FormCaptchaLoginAuthenticationFilter() {
        super();
    }

    public FormCaptchaLoginAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (this.postOnly && !"POST".equals(request.getMethod())) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        FormCaptchaLoginAuthenticationToken formCaptchaLoginAuthenticationToken =
                getAuthenticationToken(request);

        // Allow subclasses to set the "details" property
        setDetails(request, formCaptchaLoginAuthenticationToken);

        return this.getAuthenticationManager().authenticate(formCaptchaLoginAuthenticationToken);
    }

    private FormCaptchaLoginAuthenticationToken getAuthenticationToken(HttpServletRequest request) {

        String username = obtainUsername(request);
        String password = obtainPassword(request);
        String key = request.getParameter("symmetric");

        if (StringUtils.isBlank(username)) {
            username = "";
        }

        if (StringUtils.isBlank(password)) {
            password = "";
        }

        if (StringUtils.isNotBlank(key)
                && StringUtils.isNotBlank(username)
                && StringUtils.isNotBlank(password)) {
            byte[] byteKey = SymmetricUtils.getDecryptedSymmetricKey(key);
            username = SymmetricUtils.decrypt(username, byteKey);
            password = SymmetricUtils.decrypt(password, byteKey);
            log.info("Decrypt Username is : [{}], Password is : [{}]", username, password);
        }

        return new FormCaptchaLoginAuthenticationToken(username, password);
    }

    @Override
    public void setPostOnly(boolean postOnly) {
        super.setPostOnly(postOnly);
        this.postOnly = postOnly;
    }
}
