/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication$Type
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.PropertySource
 */
package com.kuma.boot.web.error;

import com.kuma.boot.web.error.handler.ConstraintViolationApiExceptionHandler;
import com.kuma.boot.web.error.handler.HttpMessageNotReadableApiExceptionHandler;
import com.kuma.boot.web.error.handler.MethodArgumentNotValidApiExceptionHandler;
import com.kuma.boot.web.error.handler.ObjectOptimisticLockingFailureApiExceptionHandler;
import com.kuma.boot.web.error.handler.SpringSecurityApiExceptionHandler;
import com.kuma.boot.web.error.handler.TypeMismatchApiExceptionHandler;
import com.kuma.boot.web.error.mapper.ErrorCodeMapper;
import com.kuma.boot.web.error.mapper.ErrorMessageMapper;
import com.kuma.boot.web.error.mapper.HttpStatusMapper;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@AutoConfiguration
@ConditionalOnWebApplication(type=ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(value={ErrorHandlingProperties.class})
@ConditionalOnProperty(value={"kuma.boot.web.error.handling.enabled"}, matchIfMissing=true)
@PropertySource(value={"classpath:/kmc-web-error-handling.properties"})
public class ErrorHandlingConfiguration {
    @Bean
    public ErrorHandlingControllerAdvice errorHandlingControllerAdvice(ErrorHandlingProperties properties, List<ApiExceptionHandler> handlers, FallbackApiExceptionHandler fallbackApiExceptionHandler) {
        return new ErrorHandlingControllerAdvice(properties, handlers, fallbackApiExceptionHandler);
    }

    @Bean
    public ApiErrorResponseSerializer apiErrorResponseSerializer(ErrorHandlingProperties properties) {
        return new ApiErrorResponseSerializer(properties);
    }

    @Bean
    public HttpStatusMapper httpStatusMapper(ErrorHandlingProperties properties) {
        return new HttpStatusMapper(properties);
    }

    @Bean
    public ErrorCodeMapper errorCodeMapper(ErrorHandlingProperties properties) {
        return new ErrorCodeMapper(properties);
    }

    @Bean
    public ErrorMessageMapper errorMessageMapper(ErrorHandlingProperties properties) {
        return new ErrorMessageMapper(properties);
    }

    @Bean
    public FallbackApiExceptionHandler defaultHandler(HttpStatusMapper httpStatusMapper, ErrorCodeMapper errorCodeMapper, ErrorMessageMapper errorMessageMapper) {
        return new DefaultFallbackApiExceptionHandler(httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Bean
    public TypeMismatchApiExceptionHandler typeMismatchApiExceptionHandler(ErrorHandlingProperties properties, HttpStatusMapper httpStatusMapper, ErrorCodeMapper errorCodeMapper, ErrorMessageMapper errorMessageMapper) {
        return new TypeMismatchApiExceptionHandler(properties, httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Bean
    public ConstraintViolationApiExceptionHandler constraintViolationApiExceptionHandler(ErrorHandlingProperties properties, HttpStatusMapper httpStatusMapper, ErrorCodeMapper errorCodeMapper, ErrorMessageMapper errorMessageMapper) {
        return new ConstraintViolationApiExceptionHandler(properties, httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Bean
    public HttpMessageNotReadableApiExceptionHandler httpMessageNotReadableApiExceptionHandler(ErrorHandlingProperties properties, HttpStatusMapper httpStatusMapper, ErrorCodeMapper errorCodeMapper, ErrorMessageMapper errorMessageMapper) {
        return new HttpMessageNotReadableApiExceptionHandler(properties, httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Bean
    public MethodArgumentNotValidApiExceptionHandler methodArgumentNotValidApiExceptionHandler(ErrorHandlingProperties properties, HttpStatusMapper httpStatusMapper, ErrorCodeMapper errorCodeMapper, ErrorMessageMapper errorMessageMapper) {
        return new MethodArgumentNotValidApiExceptionHandler(properties, httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Bean
    @ConditionalOnClass(name={"org.springframework.security.access.AccessDeniedException"})
    public SpringSecurityApiExceptionHandler springSecurityApiExceptionHandler(ErrorHandlingProperties properties, HttpStatusMapper httpStatusMapper, ErrorCodeMapper errorCodeMapper, ErrorMessageMapper errorMessageMapper) {
        return new SpringSecurityApiExceptionHandler(properties, httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Bean
    @ConditionalOnClass(name={"org.springframework.orm.ObjectOptimisticLockingFailureException"})
    public ObjectOptimisticLockingFailureApiExceptionHandler objectOptimisticLockingFailureApiExceptionHandler(ErrorHandlingProperties properties, HttpStatusMapper httpStatusMapper, ErrorCodeMapper errorCodeMapper, ErrorMessageMapper errorMessageMapper) {
        return new ObjectOptimisticLockingFailureApiExceptionHandler(properties, httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }
}

