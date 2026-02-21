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

package com.kuma.boot.web.error;

import com.kuma.boot.web.error.handler.*;
import com.kuma.boot.web.error.mapper.ErrorCodeMapper;
import com.kuma.boot.web.error.mapper.ErrorMessageMapper;
import com.kuma.boot.web.error.mapper.HttpStatusMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

/**
 * ErrorHandlingConfiguration
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-01-12 09:00:17
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(ErrorHandlingProperties.class)
@ConditionalOnProperty(value = "kuma.boot.web.error.handling.enabled", matchIfMissing = true)
@PropertySource("classpath:/kmc-web-error-handling.properties")
public class ErrorHandlingConfiguration {

    @Bean
    public ErrorHandlingControllerAdvice errorHandlingControllerAdvice(
            ErrorHandlingProperties properties,
            List<com.kuma.boot.web.error.ApiExceptionHandler> handlers,
            FallbackApiExceptionHandler fallbackApiExceptionHandler) {
        return new ErrorHandlingControllerAdvice(properties, handlers, fallbackApiExceptionHandler);
    }

    @Bean
    public com.kuma.boot.web.error.ApiErrorResponseSerializer apiErrorResponseSerializer(
            ErrorHandlingProperties properties) {
        return new com.kuma.boot.web.error.ApiErrorResponseSerializer(properties);
    }

    @Bean
    public HttpStatusMapper httpStatusMapper( ErrorHandlingProperties properties) {
        return new HttpStatusMapper(properties);
    }

    @Bean
    public ErrorCodeMapper errorCodeMapper( ErrorHandlingProperties properties) {
        return new ErrorCodeMapper(properties);
    }

    @Bean
    public ErrorMessageMapper errorMessageMapper( ErrorHandlingProperties properties) {
        return new ErrorMessageMapper(properties);
    }

    @Bean
    public FallbackApiExceptionHandler defaultHandler(
            HttpStatusMapper httpStatusMapper,
            ErrorCodeMapper errorCodeMapper,
            ErrorMessageMapper errorMessageMapper) {
        return new com.kuma.boot.web.error.DefaultFallbackApiExceptionHandler(
                httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Bean
    public com.kuma.boot.web.error.handler.TypeMismatchApiExceptionHandler typeMismatchApiExceptionHandler(
            ErrorHandlingProperties properties,
            HttpStatusMapper httpStatusMapper,
            ErrorCodeMapper errorCodeMapper,
            ErrorMessageMapper errorMessageMapper) {
        return new com.kuma.boot.web.error.handler.TypeMismatchApiExceptionHandler(
                properties, httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Bean
    public com.kuma.boot.web.error.handler.ConstraintViolationApiExceptionHandler constraintViolationApiExceptionHandler(
            ErrorHandlingProperties properties,
            HttpStatusMapper httpStatusMapper,
            ErrorCodeMapper errorCodeMapper,
            ErrorMessageMapper errorMessageMapper) {
        return new com.kuma.boot.web.error.handler.ConstraintViolationApiExceptionHandler(
                properties, httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Bean
    public com.kuma.boot.web.error.handler.HttpMessageNotReadableApiExceptionHandler httpMessageNotReadableApiExceptionHandler(
            ErrorHandlingProperties properties,
            HttpStatusMapper httpStatusMapper,
            ErrorCodeMapper errorCodeMapper,
            ErrorMessageMapper errorMessageMapper) {
        return new com.kuma.boot.web.error.handler.HttpMessageNotReadableApiExceptionHandler(
                properties, httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Bean
    public com.kuma.boot.web.error.handler.MethodArgumentNotValidApiExceptionHandler methodArgumentNotValidApiExceptionHandler(
            ErrorHandlingProperties properties,
            HttpStatusMapper httpStatusMapper,
            ErrorCodeMapper errorCodeMapper,
            ErrorMessageMapper errorMessageMapper) {
        return new com.kuma.boot.web.error.handler.MethodArgumentNotValidApiExceptionHandler(
                properties, httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.security.access.AccessDeniedException")
    public com.kuma.boot.web.error.handler.SpringSecurityApiExceptionHandler springSecurityApiExceptionHandler(
            ErrorHandlingProperties properties,
            HttpStatusMapper httpStatusMapper,
            ErrorCodeMapper errorCodeMapper,
            ErrorMessageMapper errorMessageMapper) {
        return new com.kuma.boot.web.error.handler.SpringSecurityApiExceptionHandler(
                properties, httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.orm.ObjectOptimisticLockingFailureException")
    public com.kuma.boot.web.error.handler.ObjectOptimisticLockingFailureApiExceptionHandler
    objectOptimisticLockingFailureApiExceptionHandler(
            ErrorHandlingProperties properties,
            HttpStatusMapper httpStatusMapper,
            ErrorCodeMapper errorCodeMapper,
            ErrorMessageMapper errorMessageMapper) {
        return new com.kuma.boot.web.error.handler.ObjectOptimisticLockingFailureApiExceptionHandler(
                properties, httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }
}
