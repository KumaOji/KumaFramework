/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.model.Result
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.security.authentication.InsufficientAuthenticationException
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.oauth2.core.OAuth2AuthenticationException
 *  org.springframework.security.oauth2.core.OAuth2Error
 *  org.springframework.validation.BindException
 *  org.springframework.validation.BindingResult
 *  org.springframework.validation.FieldError
 *  org.springframework.web.bind.MethodArgumentNotValidException
 *  org.springframework.web.bind.annotation.ExceptionHandler
 *  org.springframework.web.client.HttpClientErrorException
 *  org.springframework.web.client.HttpServerErrorException
 */
package com.kuma.boot.security.spring.exception;

import com.kuma.boot.common.model.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

public class SecurityGlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(SecurityGlobalExceptionHandler.class);
    private static final Map<String, Result<String>> EXCEPTION_DICTIONARY = new HashMap<String, Result<String>>();

    @ExceptionHandler(value={HttpClientErrorException.class, HttpServerErrorException.class})
    public static Result<String> restTemplateException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Result<String> result = SecurityGlobalExceptionHandler.resolveException(ex, request.getRequestURI());
        return result;
    }

    @ExceptionHandler(value={MethodArgumentNotValidException.class})
    public static Result<String> validationMethodArgumentException(MethodArgumentNotValidException ex, HttpServletRequest request, HttpServletResponse response) {
        return SecurityGlobalExceptionHandler.validationBindException((BindException)ex, request, response);
    }

    @ExceptionHandler(value={BindException.class})
    public static Result<String> validationBindException(BindException ex, HttpServletRequest request, HttpServletResponse response) {
        Result<String> result = SecurityGlobalExceptionHandler.resolveException((Exception)ex, request.getRequestURI());
        BindingResult bindingResult = ex.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        return result;
    }

    @ExceptionHandler(value={AuthenticationException.class, PlatformAuthenticationException.class})
    public static Result<String> authenticationException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Result<String> result = SecurityGlobalExceptionHandler.resolveSecurityException(ex, request.getRequestURI());
        return result;
    }

    @ExceptionHandler(value={OAuth2AuthenticationException.class})
    public static Result<String> oAuth2AuthenticationException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Result<String> result = SecurityGlobalExceptionHandler.resolveSecurityException(ex, request.getRequestURI());
        return result;
    }

    public static Result<String> exception(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Result<String> result = SecurityGlobalExceptionHandler.resolveException(ex, request.getRequestURI());
        return result;
    }

    public static Result<String> resolveException(Exception ex, String path) {
        return null;
    }

    public static Result<String> resolveSecurityException(Exception exception, String path) {
        Exception reason = new Exception();
        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2AuthenticationException oAuth2AuthenticationException = (OAuth2AuthenticationException)exception;
            OAuth2Error oAuth2Error = oAuth2AuthenticationException.getError();
            if (EXCEPTION_DICTIONARY.containsKey(oAuth2Error.getErrorCode())) {
                Result<String> result = EXCEPTION_DICTIONARY.get(oAuth2Error.getErrorCode());
                return result;
            }
        } else if (exception instanceof InsufficientAuthenticationException) {
            Throwable throwable = exception.getCause();
            reason = ObjectUtils.isNotEmpty((Object)throwable) ? new Exception(throwable) : exception;
            log.info("InsufficientAuthenticationException cause content is [{}]", (Object)reason.getClass().getSimpleName());
        } else {
            String exceptionName = exception.getClass().getSimpleName();
            if (StringUtils.isNotEmpty((CharSequence)exceptionName) && EXCEPTION_DICTIONARY.containsKey(exceptionName)) {
                return EXCEPTION_DICTIONARY.get(exceptionName);
            }
            reason = exception;
        }
        return SecurityGlobalExceptionHandler.resolveException(reason, path);
    }
}

