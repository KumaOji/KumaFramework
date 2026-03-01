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

package com.kuma.boot.security.spring.exception;

import com.kuma.boot.common.model.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
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

/**
 * <p>统一异常处理器
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:07:23
 */
// @RestControllerAdvice
public class SecurityGlobalExceptionHandler {

    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(SecurityGlobalExceptionHandler.class);

    /**
     * 除了字典
     */
    private static final Map<String, Result<String>> EXCEPTION_DICTIONARY = new HashMap<>();

    static {
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.ACCESS_DENIED,
        // FeedbackFactory.getUnauthorizedResult(ResultErrorCodes.ACCESS_DENIED));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.INSUFFICIENT_SCOPE,
        // FeedbackFactory.getForbiddenResult(ResultErrorCodes.INSUFFICIENT_SCOPE));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.INVALID_CLIENT,
        // FeedbackFactory.getUnauthorizedResult(ResultErrorCodes.INVALID_CLIENT));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.INVALID_GRANT,
        // FeedbackFactory.getUnauthorizedResult(ResultErrorCodes.INVALID_GRANT));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.INVALID_REDIRECT_URI,
        // FeedbackFactory.getPreconditionFailedResult(ResultErrorCodes.INVALID_REDIRECT_URI));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.INVALID_REQUEST,
        // FeedbackFactory.getPreconditionFailedResult(ResultErrorCodes.INVALID_REQUEST));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.INVALID_SCOPE,
        // FeedbackFactory.getPreconditionFailedResult(ResultErrorCodes.INVALID_SCOPE));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.INVALID_TOKEN,
        // FeedbackFactory.getUnauthorizedResult(ResultErrorCodes.INVALID_TOKEN));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.SERVER_ERROR,
        // FeedbackFactory.getInternalServerErrorResult(ResultErrorCodes.SERVER_ERROR));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.TEMPORARILY_UNAVAILABLE,
        // FeedbackFactory.getServiceUnavailableResult(ResultErrorCodes.TEMPORARILY_UNAVAILABLE));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.UNAUTHORIZED_CLIENT,
        // FeedbackFactory.getUnauthorizedResult(ResultErrorCodes.UNAUTHORIZED_CLIENT));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.UNSUPPORTED_GRANT_TYPE,
        // FeedbackFactory.getNotAcceptableResult(ResultErrorCodes.UNSUPPORTED_GRANT_TYPE));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.UNSUPPORTED_RESPONSE_TYPE,
        // FeedbackFactory.getNotAcceptableResult(ResultErrorCodes.UNSUPPORTED_RESPONSE_TYPE));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.UNSUPPORTED_TOKEN_TYPE,
        // FeedbackFactory.getNotAcceptableResult(ResultErrorCodes.UNSUPPORTED_TOKEN_TYPE));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.ACCOUNT_EXPIRED,
        // FeedbackFactory.getUnauthorizedResult(ResultErrorCodes.ACCOUNT_EXPIRED));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.BAD_CREDENTIALS,
        // FeedbackFactory.getUnauthorizedResult(ResultErrorCodes.BAD_CREDENTIALS));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.CREDENTIALS_EXPIRED,
        // FeedbackFactory.getUnauthorizedResult(ResultErrorCodes.CREDENTIALS_EXPIRED));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.ACCOUNT_DISABLED,
        // FeedbackFactory.getUnauthorizedResult(ResultErrorCodes.ACCOUNT_DISABLED));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.ACCOUNT_LOCKED,
        // FeedbackFactory.getUnauthorizedResult(ResultErrorCodes.ACCOUNT_LOCKED));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.ACCOUNT_ENDPOINT_LIMITED,
        // FeedbackFactory.getUnauthorizedResult(ResultErrorCodes.ACCOUNT_ENDPOINT_LIMITED));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.USERNAME_NOT_FOUND,
        // FeedbackFactory.getUnauthorizedResult(ResultErrorCodes.USERNAME_NOT_FOUND));
        //		EXCEPTION_DICTIONARY.put(OAuth2ErrorKeys.SESSION_EXPIRED,
        // FeedbackFactory.getUnauthorizedResult(ResultErrorCodes.SESSION_EXPIRED));
    }

    /**
     * Rest Template 错误处理
     *
     * @param ex       错误
     * @param request  请求
     * @param response 响应
     * @return {@link Result }<{@link String }>
     * @since 2023-07-04 10:07:24
     */
    @ExceptionHandler({HttpClientErrorException.class, HttpServerErrorException.class})
    public static Result<String> restTemplateException(
            Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Result<String> result = resolveException(ex, request.getRequestURI());
        //		response.setStatus(result.getStatus());
        return result;
    }

    /**
     * 验证方法参数异常
     *
     * @param ex       前女友
     * @param request  请求
     * @param response 响应
     * @return {@link Result }<{@link String }>
     * @since 2023-07-04 10:07:24
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public static Result<String> validationMethodArgumentException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request,
            HttpServletResponse response) {
        return validationBindException(ex, request, response);
    }

    /**
     * 验证绑定异常
     *
     * @param ex       前女友
     * @param request  请求
     * @param response 响应
     * @return {@link Result }<{@link String }>
     * @since 2023-07-04 10:07:24
     */
    @ExceptionHandler({BindException.class})
    public static Result<String> validationBindException(
            BindException ex, HttpServletRequest request, HttpServletResponse response) {
        Result<String> result = resolveException(ex, request.getRequestURI());

        BindingResult bindingResult = ex.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        // 返回第一个错误的信息
        //		if (ObjectUtils.isNotEmpty(fieldError)) {
        //			result.validation(fieldError.getDefaultMessage(), fieldError.getCode(),
        // fieldError.getField());
        //		}
        //
        //		response.setStatus(result.getStatus());
        return result;
    }

    /**
     * 统一异常处理
     * AuthenticationException
     *
     * @param ex       错误
     * @param request  请求
     * @param response 响应
     * @return {@link Result }<{@link String }>
     * @since 2023-07-04 10:07:24
     */
    @ExceptionHandler({AuthenticationException.class, com.kuma.boot.security.spring.exception.PlatformAuthenticationException.class})
    public static Result<String> authenticationException(
            Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Result<String> result = resolveSecurityException(ex, request.getRequestURI());
        //		response.setStatus(result.getStatus());
        return result;
    }

    /**
     * o auth2身份验证异常
     *
     * @param ex       前女友
     * @param request  请求
     * @param response 响应
     * @return {@link Result }<{@link String }>
     * @since 2023-07-04 10:07:24
     */
    @ExceptionHandler({OAuth2AuthenticationException.class})
    public static Result<String> oAuth2AuthenticationException(
            Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Result<String> result = resolveSecurityException(ex, request.getRequestURI());
        //		response.setStatus(result.getStatus());
        return result;
    }

    /**
     * 异常
     *
     * @param ex       前女友
     * @param request  请求
     * @param response 响应
     * @return {@link Result }<{@link String }>
     * @since 2023-07-04 10:07:24
     */
    //	@ExceptionHandler({Exception.class, PlatformException.class})
    public static Result<String> exception(
            Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Result<String> result = resolveException(ex, request.getRequestURI());
        //		response.setStatus(result.getStatus());
        return result;
    }

    /**
     * 解决异常
     *
     * @param ex   前女友
     * @param path 路径
     * @return {@link Result }<{@link String }>
     * @since 2023-07-04 10:07:24
     */
    public static Result<String> resolveException(Exception ex, String path) {
        //		return GlobalExceptionHandler.resolveException(ex, path);
        return null;
    }

    /**
     * 静态解析认证异常
     *
     * @param exception 错误信息
     * @param path      路径
     * @return {@link Result }<{@link String }>
     * @since 2023-07-04 10:07:24
     */
    public static Result<String> resolveSecurityException(Exception exception, String path) {

        Exception reason = new Exception();
        if (exception instanceof OAuth2AuthenticationException oAuth2AuthenticationException) {
            OAuth2Error oAuth2Error = oAuth2AuthenticationException.getError();
            if (EXCEPTION_DICTIONARY.containsKey(oAuth2Error.getErrorCode())) {
                Result<String> result = EXCEPTION_DICTIONARY.get(oAuth2Error.getErrorCode());
                //				result.path(oAuth2Error.getUri());
                //				result.stackTrace(exception.getStackTrace());
                //				result.detail(exception.getMessage());
                return result;
            }
        } else if (exception instanceof InsufficientAuthenticationException) {
            Throwable throwable = exception.getCause();
            if (ObjectUtils.isNotEmpty(throwable)) {
                reason = new Exception(throwable);
            } else {
                reason = exception;
            }
            log.info(
                    "InsufficientAuthenticationException cause content is [{}]",
                    reason.getClass().getSimpleName());
        } else {
            String exceptionName = exception.getClass().getSimpleName();
            if (StringUtils.isNotEmpty(exceptionName)
                    && EXCEPTION_DICTIONARY.containsKey(exceptionName)) {
                return EXCEPTION_DICTIONARY.get(exceptionName);
            } else {
                reason = exception;
            }
        }

        return resolveException(reason, path);
    }
}
