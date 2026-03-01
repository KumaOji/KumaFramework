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

package com.kuma.boot.security.spring.authentication.login.extension.mfa.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.context.MfaAuthenticationTokenContextHolder;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.context.MfaTokenContext;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.convert.MfaAuthenticationHttpMessageConverter;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.jwt.TokenGenerator;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.totp.DefaultTotpManager;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.totp.MfaAuthenticationManager;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.userdetails.MfaUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author: ReLive27
 * @since: 2023/1/9 21:41
 */
public class MfaAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private MfaAuthenticationManager mfaAuthenticationManager = new DefaultTotpManager();
    private final HttpMessageConverter<com.kuma.boot.security.spring.authentication.login.extension.mfa.handler.MfaAuthenticationResponse>
            mfaAuthenticationHttpMessageConverter = new MfaAuthenticationHttpMessageConverter();
    private final TokenGenerator<Jwt> tokenGenerator;
    private final UserDetailsManager userDetailsManager;

    public MfaAuthenticationSuccessHandler(
            TokenGenerator<Jwt> tokenGenerator, UserDetailsManager userDetailsManager) {
        Assert.notNull(tokenGenerator, "tokenGenerator can not be null");
        Assert.notNull(userDetailsManager, "userDetailsManager can not be null");
        this.tokenGenerator = tokenGenerator;
        this.userDetailsManager = userDetailsManager;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) authentication;
        MfaUserDetails userDetails = (MfaUserDetails) authenticationToken.getPrincipal();
        if (userDetails.isEnableMfa()) {

            if (!StringUtils.hasText(userDetails.getSecret())) {
                String secret = mfaAuthenticationManager.generateSecret();
                userDetails.setSecret(secret);
                this.userDetailsManager.updateUser(userDetails);
                String uriForImage;
                try {
                    uriForImage =
                            mfaAuthenticationManager.getUriForImage(
                                    userDetails.getUsername(), secret, "http://127.0.0.1:8080");
                } catch (Exception e) {
                    LogUtils.error("Error getting QR code image", e);
                    com.kuma.boot.security.spring.authentication.login.extension.mfa.handler.MfaAuthenticationResponse mfaAuthenticationResponse =
                            com.kuma.boot.security.spring.authentication.login.extension.mfa.handler.MfaAuthenticationResponse.unauthenticated(
                                    "Error getting QR code image",
                                    "bind",
                                    HttpStatus.BAD_REQUEST,
                                    null);
                    this.sendMfaResponse(request, response, mfaAuthenticationResponse);
                    return;
                }
                com.kuma.boot.security.spring.authentication.login.extension.mfa.handler.MfaAuthenticationResponse mfaAuthenticationResponse =
                        com.kuma.boot.security.spring.authentication.login.extension.mfa.handler.MfaAuthenticationResponse.unauthenticated(
                                "The current account is not bound to the token app",
                                "bind",
                                HttpStatus.OK,
                                uriForImage);
                this.sendMfaResponse(request, response, mfaAuthenticationResponse);
                return;
            }
            MfaTokenContext mfaTokenContext =
                    MfaAuthenticationTokenContextHolder.getMfaTokenContext();
            if (mfaTokenContext == null || !mfaTokenContext.isMfa()) {
                com.kuma.boot.security.spring.authentication.login.extension.mfa.handler.MfaAuthenticationResponse mfaAuthenticationResponse =
                        com.kuma.boot.security.spring.authentication.login.extension.mfa.handler.MfaAuthenticationResponse.unauthenticated(
                                "dynamic password error", "enable", HttpStatus.OK, null);
                this.sendMfaResponse(request, response, mfaAuthenticationResponse);
                return;
            }
        }

        Jwt jwt = this.tokenGenerator.generate(authentication);
        com.kuma.boot.security.spring.authentication.login.extension.mfa.handler.MfaAuthenticationResponse mfaAuthenticationResponse =
                com.kuma.boot.security.spring.authentication.login.extension.mfa.handler.MfaAuthenticationResponse.authenticated(
                        userDetails.isEnableMfa() ? "enable" : "disabled", jwt.getTokenValue());
        this.sendMfaResponse(request, response, mfaAuthenticationResponse);
    }

    public void setMfaAuthenticationManager(MfaAuthenticationManager mfaAuthenticationManager) {
        this.mfaAuthenticationManager = mfaAuthenticationManager;
    }

    private void sendMfaResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            com.kuma.boot.security.spring.authentication.login.extension.mfa.handler.MfaAuthenticationResponse mfaAuthenticationResponse)
            throws IOException {
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        this.mfaAuthenticationHttpMessageConverter.write(
                mfaAuthenticationResponse, null, httpResponse);
    }
}
