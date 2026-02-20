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

package com.kuma.boot.web.error.handler;

import com.kuma.boot.web.error.*;
import com.kuma.boot.web.error.mapper.ErrorCodeMapper;
import com.kuma.boot.web.error.mapper.ErrorMessageMapper;
import com.kuma.boot.web.error.mapper.HttpStatusMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

/**
 * {@link ApiExceptionHandler} for {@link ConstraintViolationException}. This typically happens when
 * there is validation on Spring services that gets triggered.
 *
 * @see MethodArgumentNotValidApiExceptionHandler
 */
public class ConstraintViolationApiExceptionHandler extends com.kuma.boot.web.error.handler.AbstractApiExceptionHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ConstraintViolationApiExceptionHandler.class);

    public ConstraintViolationApiExceptionHandler(
            ErrorHandlingProperties properties,
            HttpStatusMapper httpStatusMapper,
            ErrorCodeMapper errorCodeMapper,
            ErrorMessageMapper errorMessageMapper) {
        super(httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof ConstraintViolationException;
    }

    @Override
    public ApiErrorResponse handle(Throwable exception) {

        ConstraintViolationException ex = (ConstraintViolationException) exception;
        ApiErrorResponse response =
                new ApiErrorResponse(
                        HttpStatus.BAD_REQUEST, getErrorCode(exception), getMessage(ex));
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        violations.stream()
                .map(
                        constraintViolation -> {
                            Optional<Path.Node> leafNode =
                                    getLeafNode(constraintViolation.getPropertyPath());
                            if (leafNode.isPresent()) {
                                Path.Node node = leafNode.get();
                                ElementKind elementKind = node.getKind();
                                if (elementKind == ElementKind.PROPERTY) {
                                    return new ApiFieldError(
                                            getCode(constraintViolation),
                                            node.toString(),
                                            getMessage(constraintViolation),
                                            constraintViolation.getInvalidValue());
                                } else if (elementKind == ElementKind.BEAN) {
                                    return new ApiGlobalError(
                                            getCode(constraintViolation),
                                            getMessage(constraintViolation));
                                } else if (elementKind == ElementKind.PARAMETER) {
                                    return new ApiParameterError(
                                            getCode(constraintViolation),
                                            node.toString(),
                                            getMessage(constraintViolation),
                                            constraintViolation.getInvalidValue());
                                } else {
                                    LOGGER.warn(
                                            "Unable to convert constraint violation with element"
                                                    + " kind {}: {}",
                                            elementKind,
                                            constraintViolation);
                                    return null;
                                }
                            } else {
                                LOGGER.warn(
                                        "Unable to convert constraint violation: {}",
                                        constraintViolation);
                                return null;
                            }
                        })
                .forEach(
                        error -> {
                            if (error instanceof ApiFieldError) {
                                response.addFieldError((ApiFieldError) error);
                            } else if (error instanceof ApiGlobalError) {
                                response.addGlobalError((ApiGlobalError) error);
                            } else if (error instanceof ApiParameterError) {
                                response.addParameterError((ApiParameterError) error);
                            }
                        });

        return response;
    }

    private Optional<Path.Node> getLeafNode(Path path) {
        return StreamSupport.stream(path.spliterator(), false).reduce((a, b) -> b);
    }

    private String getCode(ConstraintViolation<?> constraintViolation) {
        String code =
                constraintViolation
                        .getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .getSimpleName();
        String fieldSpecificCode = constraintViolation.getPropertyPath().toString() + "." + code;
        return errorCodeMapper.getErrorCode(fieldSpecificCode, code);
    }

    private String getMessage(ConstraintViolation<?> constraintViolation) {
        String code =
                constraintViolation
                        .getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .getSimpleName();
        String fieldSpecificCode = constraintViolation.getPropertyPath().toString() + "." + code;
        return errorMessageMapper.getErrorMessage(
                fieldSpecificCode, code, constraintViolation.getMessage());
    }

    private String getMessage(ConstraintViolationException exception) {
        return "Validation failed. Error count: " + exception.getConstraintViolations().size();
    }
}
