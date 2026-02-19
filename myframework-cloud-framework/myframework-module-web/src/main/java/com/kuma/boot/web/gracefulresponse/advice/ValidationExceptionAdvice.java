/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.annotation.Resource
 *  jakarta.validation.ConstraintViolationException
 *  jakarta.validation.ValidationException
 *  org.springframework.context.support.DefaultMessageSourceResolvable
 *  org.springframework.core.annotation.Order
 *  org.springframework.validation.BindException
 *  org.springframework.validation.FieldError
 *  org.springframework.web.bind.MethodArgumentNotValidException
 *  org.springframework.web.bind.annotation.ControllerAdvice
 *  org.springframework.web.bind.annotation.ExceptionHandler
 *  org.springframework.web.bind.annotation.ResponseBody
 *  org.springframework.web.context.request.RequestAttributes
 *  org.springframework.web.context.request.RequestContextHolder
 *  org.springframework.web.context.request.ServletRequestAttributes
 *  org.springframework.web.method.HandlerMethod
 *  org.springframework.web.servlet.HandlerExecutionChain
 *  org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
 */
package com.kuma.boot.web.gracefulresponse.advice;

import com.kuma.boot.web.gracefulresponse.GracefulResponseProperties;
import com.kuma.boot.web.gracefulresponse.api.ResponseFactory;
import com.kuma.boot.web.gracefulresponse.api.ResponseStatusFactory;
import com.kuma.boot.web.gracefulresponse.api.ValidationStatusCode;
import com.kuma.boot.web.gracefulresponse.data.Response;
import com.kuma.boot.web.gracefulresponse.data.ResponseStatus;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
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

@ControllerAdvice
@Order(value=100)
public class ValidationExceptionAdvice {
    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    @Resource
    private ResponseStatusFactory responseStatusFactory;
    @Resource
    private ResponseFactory responseFactory;
    @Resource
    private GracefulResponseProperties gracefulResponseProperties;

    @ExceptionHandler(value={BindException.class, ValidationException.class, MethodArgumentNotValidException.class})
    @ResponseBody
    public Response exceptionHandler(Exception e) throws Exception {
        if (e instanceof MethodArgumentNotValidException) {
            ResponseStatus responseStatus = this.fromMethodArgumentNotValidException(e);
            return this.responseFactory.newInstance(responseStatus);
        }
        if (e instanceof BindException) {
            ResponseStatus responseStatus = this.fromBindException(e);
            return this.responseFactory.newInstance(responseStatus);
        }
        if (e instanceof ConstraintViolationException) {
            ResponseStatus responseStatus = this.fromConstraintViolationException(e);
            return this.responseFactory.newInstance(responseStatus);
        }
        return this.responseFactory.newFailInstance();
    }

    private ResponseStatus fromMethodArgumentNotValidException(Exception e) throws Exception {
        MethodArgumentNotValidException me = (MethodArgumentNotValidException)e;
        List allErrors = me.getBindingResult().getAllErrors();
        String msg = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
        String code = this.determineErrorCode();
        return this.responseStatusFactory.newInstance(code, msg);
    }

    private String determineErrorCode() throws Exception {
        Method method = this.currentControllerMethod();
        ValidationStatusCode validateStatusCode = method.getAnnotation(ValidationStatusCode.class);
        if (validateStatusCode == null) {
            validateStatusCode = method.getDeclaringClass().getAnnotation(ValidationStatusCode.class);
        }
        if (validateStatusCode != null) {
            return validateStatusCode.code();
        }
        return this.gracefulResponseProperties.getDefaultValidateErrorCode();
    }

    private Method currentControllerMethod() throws Exception {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes)requestAttributes;
        HandlerExecutionChain handlerChain = this.requestMappingHandlerMapping.getHandler(sra.getRequest());
        assert (handlerChain != null);
        HandlerMethod handler = (HandlerMethod)handlerChain.getHandler();
        return handler.getMethod();
    }

    private ResponseStatus fromConstraintViolationException(Exception e) throws Exception {
        ConstraintViolationException exception = (ConstraintViolationException)e;
        Set violationSet = exception.getConstraintViolations();
        String msg = violationSet.stream().map(s -> s.getConstraintDescriptor().getMessageTemplate()).collect(Collectors.joining(";"));
        String code = this.determineErrorCode();
        return this.responseStatusFactory.newInstance(code, msg);
    }

    private ResponseStatus fromBindException(Exception e) throws NoSuchFieldException {
        BindException bindException = (BindException)e;
        FieldError fieldError = bindException.getFieldError();
        assert (fieldError != null);
        String fieldName = fieldError.getField();
        Object target = bindException.getTarget();
        assert (target != null);
        Field declaredField = target.getClass().getDeclaredField(fieldName);
        declaredField.setAccessible(true);
        ValidationStatusCode annotation = declaredField.getAnnotation(ValidationStatusCode.class);
        declaredField.setAccessible(false);
        if (annotation == null) {
            annotation = target.getClass().getAnnotation(ValidationStatusCode.class);
        }
        String code = annotation != null ? annotation.code() : this.gracefulResponseProperties.getDefaultValidateErrorCode();
        String msg = bindException.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
        return this.responseStatusFactory.newInstance(code, msg);
    }
}

