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

package com.kuma.boot.web.gracefulresponse.advice;

import com.kuma.boot.web.gracefulresponse.GracefulResponseProperties;
import com.kuma.boot.web.gracefulresponse.api.ResponseFactory;
import com.kuma.boot.web.gracefulresponse.api.ResponseStatusFactory;
import com.kuma.boot.web.gracefulresponse.api.ValidationStatusCode;
import com.kuma.boot.web.gracefulresponse.data.Response;
import com.kuma.boot.web.gracefulresponse.data.ResponseStatus;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
@Order(100)
public class ValidationExceptionAdvice {

    @Resource private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Resource private ResponseStatusFactory responseStatusFactory;

    @Resource private ResponseFactory responseFactory;

    @Resource private GracefulResponseProperties gracefulResponseProperties;

    @ExceptionHandler(
            value = {
                    BindException.class,
                    ValidationException.class,
                    MethodArgumentNotValidException.class
            })
    @ResponseBody
    public Response exceptionHandler(Exception e) throws Exception {

        if (e instanceof MethodArgumentNotValidException) {
            ResponseStatus responseStatus = this.fromMethodArgumentNotValidException(e);
            return responseFactory.newInstance(responseStatus);
        }

        if (e instanceof BindException) {
            ResponseStatus responseStatus = this.fromBindException(e);
            return responseFactory.newInstance(responseStatus);
        }

        if (e instanceof ConstraintViolationException) {
            ResponseStatus responseStatus = this.fromConstraintViolationException(e);
            return responseFactory.newInstance(responseStatus);
        }

        return responseFactory.newFailInstance();
    }

    private ResponseStatus fromMethodArgumentNotValidException(Exception e) throws Exception {
        MethodArgumentNotValidException me = (MethodArgumentNotValidException) e;
        List<ObjectError> allErrors = me.getBindingResult().getAllErrors();
        String msg =
                allErrors.stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(";"));
        String code = this.determineErrorCode();
        return responseStatusFactory.newInstance(code, msg);
    }

    private String determineErrorCode() throws Exception {
        Method method = this.currentControllerMethod();
        ValidationStatusCode validateStatusCode = method.getAnnotation(ValidationStatusCode.class);
        if (validateStatusCode == null) {
            validateStatusCode =
                    method.getDeclaringClass().getAnnotation(ValidationStatusCode.class);
        }
        if (validateStatusCode != null) {
            return validateStatusCode.code();
        }
        return gracefulResponseProperties.getDefaultValidateErrorCode();
    }

    private Method currentControllerMethod() throws Exception {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
        HandlerExecutionChain handlerChain =
                requestMappingHandlerMapping.getHandler(sra.getRequest());
        assert handlerChain != null;
        HandlerMethod handler = (HandlerMethod) handlerChain.getHandler();
        return handler.getMethod();
    }

    private ResponseStatus fromConstraintViolationException(Exception e) throws Exception {

        ConstraintViolationException exception = (ConstraintViolationException) e;
        Set<ConstraintViolation<?>> violationSet = exception.getConstraintViolations();
        String msg =
                violationSet.stream()
                        .map(s -> s.getConstraintDescriptor().getMessageTemplate())
                        .collect(Collectors.joining(";"));
        String code = this.determineErrorCode();
        return responseStatusFactory.newInstance(code, msg);
    }

    private ResponseStatus fromBindException(Exception e) throws NoSuchFieldException {

        String code;

        BindException bindException = (BindException) e;
        FieldError fieldError = bindException.getFieldError();
        assert fieldError != null;
        String fieldName = fieldError.getField();
        Object target = bindException.getTarget();
        assert target != null;
        Field declaredField = target.getClass().getDeclaredField(fieldName);
        declaredField.setAccessible(true);
        ValidationStatusCode annotation = declaredField.getAnnotation(ValidationStatusCode.class);
        declaredField.setAccessible(false);

        // 属性上找不到注解，尝试获取类上的注解
        if (annotation == null) {
            annotation = target.getClass().getAnnotation(ValidationStatusCode.class);
        }

        if (annotation != null) {
            code = annotation.code();
        } else {
            code = gracefulResponseProperties.getDefaultValidateErrorCode();
        }

        String msg =
                bindException.getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(";"));

        return responseStatusFactory.newInstance(code, msg);
    }
}
