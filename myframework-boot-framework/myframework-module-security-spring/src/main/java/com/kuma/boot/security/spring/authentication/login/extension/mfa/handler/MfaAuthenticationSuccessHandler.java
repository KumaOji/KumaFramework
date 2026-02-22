/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.http.HttpOutputMessage
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.server.ServletServerHttpResponse
 *  org.springframework.security.authentication.UsernamePasswordAuthenticationToken
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.oauth2.jwt.Jwt
 *  org.springframework.security.provisioning.UserDetailsManager
 *  org.springframework.security.web.authentication.AuthenticationSuccessHandler
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
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
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class MfaAuthenticationSuccessHandler
implements AuthenticationSuccessHandler {
    private MfaAuthenticationManager mfaAuthenticationManager = new DefaultTotpManager();
    private final HttpMessageConverter<MfaAuthenticationResponse> mfaAuthenticationHttpMessageConverter = new MfaAuthenticationHttpMessageConverter();
    private final TokenGenerator<Jwt> tokenGenerator;
    private final UserDetailsManager userDetailsManager;

    public MfaAuthenticationSuccessHandler(TokenGenerator<Jwt> tokenGenerator, UserDetailsManager userDetailsManager) {
        Assert.notNull(tokenGenerator, (String)"tokenGenerator can not be null");
        Assert.notNull((Object)userDetailsManager, (String)"userDetailsManager can not be null");
        this.tokenGenerator = tokenGenerator;
        this.userDetailsManager = userDetailsManager;
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)authentication;
        MfaUserDetails userDetails = (MfaUserDetails)authenticationToken.getPrincipal();
        if (userDetails.isEnableMfa()) {
            if (!StringUtils.hasText((String)userDetails.getSecret())) {
                String uriForImage;
                String secret = this.mfaAuthenticationManager.generateSecret();
                userDetails.setSecret(secret);
                this.userDetailsManager.updateUser((UserDetails)userDetails);
                try {
                    uriForImage = this.mfaAuthenticationManager.getUriForImage(userDetails.getUsername(), secret, "http://127.0.0.1:8080");
                }
                catch (Exception e) {
                    LogUtils.error((String)"Error getting QR code image", (Object[])new Object[]{e});
                    MfaAuthenticationResponse mfaAuthenticationResponse = MfaAuthenticationResponse.unauthenticated("Error getting QR code image", "bind", HttpStatus.BAD_REQUEST, null);
                    this.sendMfaResponse(request, response, mfaAuthenticationResponse);
                    return;
                }
                MfaAuthenticationResponse mfaAuthenticationResponse = MfaAuthenticationResponse.unauthenticated("The current account is not bound to the token app", "bind", HttpStatus.OK, uriForImage);
                this.sendMfaResponse(request, response, mfaAuthenticationResponse);
                return;
            }
            MfaTokenContext mfaTokenContext = MfaAuthenticationTokenContextHolder.getMfaTokenContext();
            if (mfaTokenContext == null || !mfaTokenContext.isMfa()) {
                MfaAuthenticationResponse mfaAuthenticationResponse = MfaAuthenticationResponse.unauthenticated("dynamic password error", "enable", HttpStatus.OK, null);
                this.sendMfaResponse(request, response, mfaAuthenticationResponse);
                return;
            }
        }
        Jwt jwt = this.tokenGenerator.generate(authentication);
        MfaAuthenticationResponse mfaAuthenticationResponse = MfaAuthenticationResponse.authenticated(userDetails.isEnableMfa() ? "enable" : "disabled", jwt.getTokenValue());
        this.sendMfaResponse(request, response, mfaAuthenticationResponse);
    }

    public void setMfaAuthenticationManager(MfaAuthenticationManager mfaAuthenticationManager) {
        this.mfaAuthenticationManager = mfaAuthenticationManager;
    }

    private void sendMfaResponse(HttpServletRequest request, HttpServletResponse response, MfaAuthenticationResponse mfaAuthenticationResponse) throws IOException {
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        this.mfaAuthenticationHttpMessageConverter.write((Object)mfaAuthenticationResponse, null, (HttpOutputMessage)httpResponse);
    }
}

