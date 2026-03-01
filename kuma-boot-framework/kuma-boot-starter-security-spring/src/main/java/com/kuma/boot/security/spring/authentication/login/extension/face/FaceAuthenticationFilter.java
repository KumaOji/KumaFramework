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

package com.kuma.boot.security.spring.authentication.login.extension.face;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.util.Assert;

/**
 * 面对身份验证过滤器
 *
 * @author kuma
 * @version 2023.04
 * @since 2023-06-29 14:31:01
 */
public class FaceAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String KMC_SECURITY_EXTENSIONS_FACE_IMG_BASE64_PARAMETER_KEY = "imgBase64";
    public static final String KMC_SECURITY_EXTENSIONS_FACE_LOGIN_URL = "/login/face";

    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
            PathPatternRequestMatcher.withDefaults()
                    .matcher(HttpMethod.POST, KMC_SECURITY_EXTENSIONS_FACE_LOGIN_URL);
    private Converter<HttpServletRequest, FaceAuthenticationToken> faceAuthenticationTokenConverter;
    private boolean postOnly = true;

    public FaceAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.faceAuthenticationTokenConverter = new com.kuma.boot.security.spring.authentication.login.extension.face.FaceAuthenticationConverter();
    }

    public FaceAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.faceAuthenticationTokenConverter = new com.kuma.boot.security.spring.authentication.login.extension.face.FaceAuthenticationConverter();
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (this.postOnly && !HttpMethod.POST.matches(request.getMethod())) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        FaceAuthenticationToken faceAuthenticationToken =
                faceAuthenticationTokenConverter.convert(request);
        // Allow subclasses to set the "details" property
        if (faceAuthenticationToken != null) {
            setDetails(request, faceAuthenticationToken);
        }
        return this.getAuthenticationManager().authenticate(faceAuthenticationToken);
    }

    protected void setDetails(HttpServletRequest request, FaceAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public void setConverter(Converter<HttpServletRequest, FaceAuthenticationToken> converter) {
        Assert.notNull(converter, "Converter must not be null");
        this.faceAuthenticationTokenConverter = converter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public void setImgBase64Parameter(String imgBase64Parameter) {
        if (this.faceAuthenticationTokenConverter
                instanceof com.kuma.boot.security.spring.authentication.login.extension.face.FaceAuthenticationConverter faceAuthenticationConverter) {
            faceAuthenticationConverter.setImgBase64Parameter(imgBase64Parameter);
        }
    }
}
