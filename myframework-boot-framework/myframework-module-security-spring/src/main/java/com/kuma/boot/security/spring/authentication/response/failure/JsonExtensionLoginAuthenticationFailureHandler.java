/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.common.utils.servlet.ResponseUtils
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.security.authentication.AccountExpiredException
 *  org.springframework.security.authentication.AuthenticationServiceException
 *  org.springframework.security.authentication.BadCredentialsException
 *  org.springframework.security.authentication.CredentialsExpiredException
 *  org.springframework.security.authentication.DisabledException
 *  org.springframework.security.authentication.InternalAuthenticationServiceException
 *  org.springframework.security.authentication.LockedException
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 *  org.springframework.security.web.authentication.AuthenticationFailureHandler
 */
package com.kuma.boot.security.spring.authentication.response.failure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.ResponseUtils;
import com.kuma.boot.security.spring.exception.IllegalParameterExtensionLoginException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class JsonExtensionLoginAuthenticationFailureHandler
implements AuthenticationFailureHandler {
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String exceptionMsg = "\u7528\u6237\u8ba4\u8bc1\u5931\u8d25";
        if (exception instanceof IllegalParameterExtensionLoginException) {
            IllegalParameterExtensionLoginException illegalParameterExtensionLoginException = (IllegalParameterExtensionLoginException)exception;
            LogUtils.error((String)"\u7528\u6237\u53c2\u6570\u6821\u9a8c\u5f02\u5e38", (Object[])new Object[]{exception});
            exceptionMsg = illegalParameterExtensionLoginException.getMessage();
        }
        if (exception instanceof AuthenticationServiceException) {
            AuthenticationServiceException authenticationServiceException = (AuthenticationServiceException)exception;
            LogUtils.error((String)"\u8bf7\u6c42\u65b9\u5f0f\u9519\u8bef\u5f02\u5e38", (Object[])new Object[]{exception});
            exceptionMsg = authenticationServiceException.getMessage();
        }
        if (exception instanceof UsernameNotFoundException) {
            UsernameNotFoundException usernameNotFoundException = (UsernameNotFoundException)((Object)exception);
            LogUtils.error((String)"\u7528\u6237\u672a\u627e\u5230", (Object[])new Object[]{exception});
            exceptionMsg = usernameNotFoundException.getMessage();
        }
        if (exception instanceof BadCredentialsException) {
            BadCredentialsException badCredentialsException = (BadCredentialsException)exception;
            LogUtils.error((String)"\u7528\u6237\u672a\u627e\u5230", (Object[])new Object[]{exception});
            exceptionMsg = badCredentialsException.getMessage();
        }
        if (exception instanceof InternalAuthenticationServiceException) {
            InternalAuthenticationServiceException internalAuthenticationServiceException = (InternalAuthenticationServiceException)((Object)exception);
            LogUtils.error((String)"\u8ba4\u8bc1\u670d\u52a1\u5185\u90e8\u9519\u8bef", (Object[])new Object[]{exception});
            exceptionMsg = internalAuthenticationServiceException.getMessage();
        }
        if (exception instanceof LockedException) {
            LockedException lockedException = (LockedException)exception;
            LogUtils.error((String)"\u7528\u6237\u5df2\u88ab\u9501\u5b9a", (Object[])new Object[]{exception});
            exceptionMsg = lockedException.getMessage();
        }
        if (exception instanceof DisabledException) {
            DisabledException disabledException = (DisabledException)exception;
            LogUtils.error((String)"\u7528\u6237\u672a\u542f\u7528", (Object[])new Object[]{exception});
            exceptionMsg = disabledException.getMessage();
        }
        if (exception instanceof AccountExpiredException) {
            AccountExpiredException accountExpiredException = (AccountExpiredException)exception;
            LogUtils.error((String)"\u7528\u6237\u8d26\u53f7\u5df2\u8fc7\u671f", (Object[])new Object[]{exception});
            exceptionMsg = accountExpiredException.getMessage();
        }
        if (exception instanceof CredentialsExpiredException) {
            CredentialsExpiredException credentialsExpiredException = (CredentialsExpiredException)exception;
            LogUtils.error((String)"\u7528\u6237\u8d26\u53f7\u5df2\u8fc7\u671f", (Object[])new Object[]{exception});
            exceptionMsg = credentialsExpiredException.getMessage();
        }
        ResponseUtils.fail((HttpServletResponse)response, (Object)exceptionMsg);
    }
}

