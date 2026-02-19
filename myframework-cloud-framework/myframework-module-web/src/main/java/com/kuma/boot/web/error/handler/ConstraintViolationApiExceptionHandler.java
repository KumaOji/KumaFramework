/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.ConstraintViolation
 *  jakarta.validation.ConstraintViolationException
 *  jakarta.validation.ElementKind
 *  jakarta.validation.Path
 *  jakarta.validation.Path$Node
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.http.HttpStatus
 */
package com.kuma.boot.web.error.handler;

import com.kuma.boot.web.error.ApiErrorResponse;
import com.kuma.boot.web.error.ApiFieldError;
import com.kuma.boot.web.error.ApiGlobalError;
import com.kuma.boot.web.error.ApiParameterError;
import com.kuma.boot.web.error.ErrorHandlingProperties;
import com.kuma.boot.web.error.mapper.ErrorCodeMapper;
import com.kuma.boot.web.error.mapper.ErrorMessageMapper;
import com.kuma.boot.web.error.mapper.HttpStatusMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class ConstraintViolationApiExceptionHandler
extends AbstractApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConstraintViolationApiExceptionHandler.class);

    public ConstraintViolationApiExceptionHandler(ErrorHandlingProperties properties, HttpStatusMapper httpStatusMapper, ErrorCodeMapper errorCodeMapper, ErrorMessageMapper errorMessageMapper) {
        super(httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof ConstraintViolationException;
    }

    @Override
    public ApiErrorResponse handle(Throwable exception) {
        ConstraintViolationException ex = (ConstraintViolationException)exception;
        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.BAD_REQUEST, this.getErrorCode(exception), this.getMessage(ex));
        Set violations = ex.getConstraintViolations();
        violations.stream().map(constraintViolation -> {
            Optional<Path.Node> leafNode = this.getLeafNode(constraintViolation.getPropertyPath());
            if (leafNode.isPresent()) {
                Path.Node node = leafNode.get();
                ElementKind elementKind = node.getKind();
                if (elementKind == ElementKind.PROPERTY) {
                    return new ApiFieldError(this.getCode((ConstraintViolation<?>)constraintViolation), node.toString(), this.getMessage((ConstraintViolation<?>)constraintViolation), constraintViolation.getInvalidValue());
                }
                if (elementKind == ElementKind.BEAN) {
                    return new ApiGlobalError(this.getCode((ConstraintViolation<?>)constraintViolation), this.getMessage((ConstraintViolation<?>)constraintViolation));
                }
                if (elementKind == ElementKind.PARAMETER) {
                    return new ApiParameterError(this.getCode((ConstraintViolation<?>)constraintViolation), node.toString(), this.getMessage((ConstraintViolation<?>)constraintViolation), constraintViolation.getInvalidValue());
                }
                LOGGER.warn("Unable to convert constraint violation with element kind {}: {}", (Object)elementKind, constraintViolation);
                return null;
            }
            LOGGER.warn("Unable to convert constraint violation: {}", constraintViolation);
            return null;
        }).forEach(error -> {
            if (error instanceof ApiFieldError) {
                response.addFieldError((ApiFieldError)error);
            } else if (error instanceof ApiGlobalError) {
                response.addGlobalError((ApiGlobalError)error);
            } else if (error instanceof ApiParameterError) {
                response.addParameterError((ApiParameterError)error);
            }
        });
        return response;
    }

    private Optional<Path.Node> getLeafNode(Path path) {
        return StreamSupport.stream(path.spliterator(), false).reduce((a, b) -> b);
    }

    private String getCode(ConstraintViolation<?> constraintViolation) {
        String code = constraintViolation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
        String fieldSpecificCode = constraintViolation.getPropertyPath().toString() + "." + code;
        return this.errorCodeMapper.getErrorCode(fieldSpecificCode, code);
    }

    private String getMessage(ConstraintViolation<?> constraintViolation) {
        String code = constraintViolation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
        String fieldSpecificCode = constraintViolation.getPropertyPath().toString() + "." + code;
        return this.errorMessageMapper.getErrorMessage(fieldSpecificCode, code, constraintViolation.getMessage());
    }

    private String getMessage(ConstraintViolationException exception) {
        return "Validation failed. Error count: " + exception.getConstraintViolations().size();
    }
}

