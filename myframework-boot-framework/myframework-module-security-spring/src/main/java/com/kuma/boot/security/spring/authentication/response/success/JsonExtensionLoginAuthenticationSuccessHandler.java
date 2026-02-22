/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.context.ContextUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.http.HttpOutputMessage
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.server.ServletServerHttpResponse
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
 *  org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter
 *  org.springframework.security.web.authentication.AuthenticationSuccessHandler
 *  org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
 */
package com.kuma.boot.security.spring.authentication.response.success;

import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import com.kuma.boot.security.spring.oauth2.token.OAuth2AccessTokenStore;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class JsonExtensionLoginAuthenticationSuccessHandler
implements AuthenticationSuccessHandler {
    private final HttpMessageConverter<OAuth2AccessTokenResponse> accessTokenHttpResponseConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
    private final SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final OAuth2AccessTokenStore oAuth2AccessTokenStore;

    public JsonExtensionLoginAuthenticationSuccessHandler(SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler, JwtTokenGenerator jwtTokenGenerator) {
        this.savedRequestAwareAuthenticationSuccessHandler = savedRequestAwareAuthenticationSuccessHandler;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.oAuth2AccessTokenStore = (OAuth2AccessTokenStore)ContextUtils.getBean(OAuth2AccessTokenStore.class, (boolean)true);
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if ("1".equals(request.getHeader("ajax"))) {
            this.savedRequestAwareAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        } else {
            LogUtils.error((String)"\u7528\u6237\u8ba4\u8bc1\u6210\u529f", (Object[])new Object[]{authentication});
            UserDetails userDetails = (UserDetails)authentication.getPrincipal();
            OAuth2AccessTokenResponse accessTokenResponse = this.jwtTokenGenerator.tokenResponse(userDetails);
            ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
            this.oAuth2AccessTokenStore.addToken(userDetails, accessTokenResponse, 6000L, TimeUnit.SECONDS);
            this.accessTokenHttpResponseConverter.write((Object)accessTokenResponse, null, (HttpOutputMessage)httpResponse);
        }
    }
}

