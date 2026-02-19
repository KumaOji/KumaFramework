/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.csp.sentinel.slots.block.BlockException
 *  com.alibaba.csp.sentinel.slots.block.authority.AuthorityException
 *  com.alibaba.csp.sentinel.slots.block.degrade.DegradeException
 *  com.alibaba.csp.sentinel.slots.block.flow.FlowException
 *  com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException
 *  com.alibaba.csp.sentinel.slots.system.SystemBlockException
 *  com.kuma.boot.common.enums.ResultEnum
 *  com.kuma.boot.common.exception.BaseException
 *  com.kuma.boot.common.exception.BusinessException
 *  com.kuma.boot.common.exception.IdempotencyException
 *  com.kuma.boot.common.exception.InnerException
 *  com.kuma.boot.common.exception.LockException
 *  com.kuma.boot.common.holder.TraceContextHolder
 *  com.kuma.boot.common.model.Code
 *  com.kuma.boot.common.model.result.Result
 *  com.kuma.boot.common.utils.id.IdGeneratorUtils
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.common.utils.servlet.RequestUtils
 *  com.kuma.boot.common.utils.servlet.TraceUtils
 *  com.kuma.boot.idempotent.exception.IdempotentException
 *  com.kuma.boot.ratelimit.ratelimitaspect.LimitException
 *  com.kuma.boot.ratelimit.ratelimitprovider.RateLimitException
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  jakarta.validation.ConstraintViolation
 *  jakarta.validation.ConstraintViolationException
 *  jakarta.validation.ValidationException
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.context.ApplicationEventPublisher
 *  org.springframework.core.NestedCheckedException
 *  org.springframework.core.NestedRuntimeException
 *  org.springframework.dao.DataIntegrityViolationException
 *  org.springframework.dao.DuplicateKeyException
 *  org.springframework.http.converter.HttpMessageNotReadableException
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
 *  org.springframework.security.access.AccessDeniedException
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 *  org.springframework.validation.BindException
 *  org.springframework.validation.BindingResult
 *  org.springframework.validation.FieldError
 *  org.springframework.web.HttpMediaTypeNotSupportedException
 *  org.springframework.web.HttpRequestMethodNotSupportedException
 *  org.springframework.web.bind.MethodArgumentNotValidException
 *  org.springframework.web.bind.MissingServletRequestParameterException
 *  org.springframework.web.bind.annotation.ExceptionHandler
 *  org.springframework.web.bind.annotation.RestControllerAdvice
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.HandlerMethod
 *  org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
 */
package com.kuma.boot.web.exception.advice;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.exception.IdempotencyException;
import com.kuma.boot.common.exception.InnerException;
import com.kuma.boot.common.exception.LockException;
import com.kuma.boot.common.holder.TraceContextHolder;
import com.kuma.boot.common.model.Code;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import com.kuma.boot.idempotent.exception.IdempotentException;
import com.kuma.boot.ratelimit.ratelimitaspect.LimitException;
import com.kuma.boot.ratelimit.ratelimitprovider.RateLimitException;
import com.kuma.boot.web.annotation.BusinessApi;
import com.kuma.boot.web.exception.event.ErrorEvent;
import com.kuma.boot.web.exception.handler.ExceptionHandler;
import com.kuma.boot.web.utils.ErrorUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.NestedCheckedException;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@AutoConfiguration
@RestControllerAdvice(annotations={BusinessApi.class})
public class BusinessHandlerExceptionAdvice
implements InitializingBean {
    private final List<ExceptionHandler> exceptionHandler;
    @Autowired
    @Qualifier(value="asyncThreadPoolTaskExecutor")
    private ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public BusinessHandlerExceptionAdvice(List<ExceptionHandler> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public void afterPropertiesSet() throws Exception {
        LogUtils.started(BusinessHandlerExceptionAdvice.class, (String)"kuma-boot-starter-web", (String[])new String[0]);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={BaseException.class})
    public Result<String> baseException(NativeWebRequest req, BaseException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((Code)e.getCode(), (String)e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={InnerException.class})
    public Result<String> innerException(NativeWebRequest req, InnerException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((ResultEnum)ResultEnum.INNER_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={LockException.class})
    public Result<String> lockException(NativeWebRequest req, LockException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((ResultEnum)ResultEnum.FAILED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={IdempotencyException.class})
    public Result<String> idempotencyException(NativeWebRequest req, IdempotencyException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((ResultEnum)ResultEnum.FAILED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={BusinessException.class})
    public Result<String> businessException(NativeWebRequest req, BusinessException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((Code)e.getCode(), (String)e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={IllegalArgumentException.class})
    public Result<String> illegalArgumentException(NativeWebRequest req, IllegalArgumentException e) {
        this.handleExceptions(req, e);
        return Result.fail((ResultEnum)ResultEnum.ILLEGAL_ARGUMENT_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={AccessDeniedException.class})
    public Result<String> badMethodExpressException(NativeWebRequest req, AccessDeniedException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((ResultEnum)ResultEnum.FORBIDDEN);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={UsernameNotFoundException.class})
    public Result<String> badUsernameNotFoundException(NativeWebRequest req, UsernameNotFoundException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((ResultEnum)ResultEnum.USERNAME_OR_PASSWORD_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={DataIntegrityViolationException.class})
    public Result<String> handleDataIntegrityViolationException(NativeWebRequest req, DataIntegrityViolationException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((ResultEnum)ResultEnum.FAILED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={LimitException.class})
    public Result<String> limitException(NativeWebRequest req, LimitException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((Code)e.getCode(), (String)e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={IdempotentException.class})
    public Result<String> idempotentException(NativeWebRequest req, IdempotentException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((Code)e.getCode(), (String)e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={NestedRuntimeException.class})
    public Result<String> handleNestedRuntimeException(NativeWebRequest req, NestedRuntimeException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((String)e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={NestedCheckedException.class})
    public Result<String> handleNestedCheckedException(NativeWebRequest req, NestedCheckedException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((String)e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={BindException.class})
    public Result<Map<String, String>> handleBindException(NativeWebRequest req, BindException e) {
        this.handleExceptions(req, (Exception)e);
        List<HashMap<String, String>> errors = this.methodArgumentNotValidExceptionDescribe(e);
        String message = Optional.ofNullable(errors).orElse(Collections.emptyList()).stream().flatMap(s -> s.values().stream()).findFirst().orElse("\u8bf7\u6c42\u53c2\u6570\u9519\u8bef");
        BindingResult bindingResult = e.getBindingResult();
        return Result.fail((Code)ResultEnum.BAD_REQUEST.code(), (String)this.getErrors(bindingResult));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={MethodArgumentNotValidException.class})
    public Result<Map<String, String>> handleMethodArgumentNotValidException(NativeWebRequest req, MethodArgumentNotValidException e) {
        this.handleExceptions(req, (Exception)e);
        List<HashMap<String, String>> errors = this.methodArgumentNotValidExceptionDescribe((BindException)e);
        String message = Optional.ofNullable(errors).orElse(Collections.emptyList()).stream().flatMap(s -> s.values().stream()).findFirst().orElse("\u8bf7\u6c42\u53c2\u6570\u9519\u8bef");
        BindingResult bindingResult = e.getBindingResult();
        return Result.fail((Code)ResultEnum.BAD_REQUEST.code(), (String)this.getErrors(bindingResult));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={ConstraintViolationException.class})
    public Result<Map<String, String>> handleException(NativeWebRequest req, ConstraintViolationException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((Code)ResultEnum.BAD_REQUEST.code(), (String)this.getErrors(e));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={MethodArgumentTypeMismatchException.class})
    public Result<String> requestTypeMismatch(NativeWebRequest req, MethodArgumentTypeMismatchException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((ResultEnum)ResultEnum.METHOD_ARGUMENTS_TYPE_MISMATCH);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={MissingServletRequestParameterException.class})
    public Result<String> requestMissingServletRequest(NativeWebRequest req, MissingServletRequestParameterException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((ResultEnum)ResultEnum.MISSING_SERVLET_REQUEST_PARAMETER);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={HttpMessageNotReadableException.class})
    public Result<String> httpMessageNotReadableException(NativeWebRequest req, HttpMessageNotReadableException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((ResultEnum)ResultEnum.HTTP_MESSAGE_NOT_READABLE);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={ValidationException.class})
    public Result<String> handleException(NativeWebRequest req, ValidationException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((ResultEnum)ResultEnum.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={HttpRequestMethodNotSupportedException.class})
    public Result<String> handleHttpRequestMethodNotSupportedException(NativeWebRequest req, HttpRequestMethodNotSupportedException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((ResultEnum)ResultEnum.METHOD_NOT_SUPPORTED_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={HttpMediaTypeNotSupportedException.class})
    public Result<String> handleHttpMediaTypeNotSupportedException(NativeWebRequest req, HttpMediaTypeNotSupportedException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((ResultEnum)ResultEnum.MEDIA_TYPE_NOT_SUPPORTED_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={SQLException.class})
    public Result<String> handleSqlException(NativeWebRequest req, SQLException e) {
        this.handleExceptions(req, e);
        return Result.fail((ResultEnum)ResultEnum.FAILED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={SQLIntegrityConstraintViolationException.class})
    public Result<String> handleSqlException(NativeWebRequest req, SQLIntegrityConstraintViolationException e) {
        this.handleExceptions(req, e);
        return Result.fail((ResultEnum)ResultEnum.FAILED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={DuplicateKeyException.class})
    public Result<String> handleDuplicateKeyException(DuplicateKeyException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        LogUtils.error((String)"\u8bf7\u6c42\u5730\u5740'{}',\u6570\u636e\u5e93\u4e2d\u5df2\u5b58\u5728\u8bb0\u5f55'{}'", (Object[])new Object[]{requestURI, e.getMessage()});
        return Result.fail((String)"\u6570\u636e\u5e93\u4e2d\u5df2\u5b58\u5728\u8be5\u8bb0\u5f55\uff0c\u8bf7\u8054\u7cfb\u7ba1\u7406\u5458\u786e\u8ba4");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={Exception.class})
    public Result<String> handleException(NativeWebRequest req, Exception e) {
        this.handleExceptions(req, e);
        return Result.fail();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={Error.class})
    public Result<String> handleThrowable(NativeWebRequest req, Error e) {
        this.handleExceptions(req, new Exception(e.getMessage()));
        return Result.fail();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={UndeclaredThrowableException.class})
    public Result<String> handleUndeclaredThrowableException(NativeWebRequest req, UndeclaredThrowableException ex) {
        this.handleExceptions(req, ex);
        Throwable e = ex.getCause();
        LogUtils.error((String)"WebmvcHandler sentinel \u964d\u7ea7 \u8d44\u6e90\u540d\u79f0", (Object[])new Object[0]);
        String errMsg = e.getMessage();
        if (e instanceof FlowException) {
            errMsg = "\u88ab\u9650\u6d41\u4e86";
        }
        if (e instanceof DegradeException) {
            errMsg = "\u670d\u52a1\u964d\u7ea7\u4e86";
        }
        if (e instanceof ParamFlowException) {
            errMsg = "\u670d\u52a1\u70ed\u70b9\u964d\u7ea7\u4e86";
        }
        if (e instanceof SystemBlockException) {
            errMsg = "\u7cfb\u7edf\u8fc7\u8f7d\u4fdd\u62a4";
        }
        if (e instanceof AuthorityException) {
            errMsg = "\u9650\u6d41\u6743\u9650\u63a7\u5236\u5f02\u5e38";
        }
        return Result.fail((Code)ResultEnum.LIMIT_ERROR.code(), (String)errMsg);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={BlockException.class})
    public Result<String> handleBlockException(NativeWebRequest req, BlockException e) {
        this.handleExceptions(req, (Exception)e);
        LogUtils.error((String)"WebmvcHandler sentinel \u964d\u7ea7 \u8d44\u6e90\u540d\u79f0{}", (Object[])new Object[]{e, e.getRule().getResource()});
        String errMsg = e.getMessage();
        if (e instanceof FlowException) {
            errMsg = "\u88ab\u9650\u6d41\u4e86";
        }
        if (e instanceof DegradeException) {
            errMsg = "\u670d\u52a1\u964d\u7ea7\u4e86";
        }
        if (e instanceof ParamFlowException) {
            errMsg = "\u670d\u52a1\u70ed\u70b9\u964d\u7ea7\u4e86";
        }
        if (e instanceof SystemBlockException) {
            errMsg = "\u7cfb\u7edf\u8fc7\u8f7d\u4fdd\u62a4";
        }
        if (e instanceof AuthorityException) {
            errMsg = "\u9650\u6d41\u6743\u9650\u63a7\u5236\u5f02\u5e38";
        }
        return Result.fail((Code)ResultEnum.LIMIT_ERROR.code(), (String)errMsg);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={FlowException.class})
    public Result<String> handleFlowException(NativeWebRequest req, FlowException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((Code)ResultEnum.LIMIT_ERROR.code(), (String)"\u88ab\u9650\u6d41\u4e86");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={DegradeException.class})
    public Result<String> handleDegradeException(NativeWebRequest req, DegradeException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((Code)ResultEnum.LIMIT_ERROR.code(), (String)"\u670d\u52a1\u964d\u7ea7\u4e86");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={ParamFlowException.class})
    public Result<String> handleParamFlowException(NativeWebRequest req, ParamFlowException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((Code)ResultEnum.LIMIT_ERROR.code(), (String)"\u670d\u52a1\u70ed\u70b9\u964d\u7ea7\u4e86");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={SystemBlockException.class})
    public Result<String> handleSystemBlockException(NativeWebRequest req, SystemBlockException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((Code)ResultEnum.LIMIT_ERROR.code(), (String)"\u7cfb\u7edf\u8fc7\u8f7d\u4fdd\u62a4");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={AuthorityException.class})
    public Result<String> handleAuthorityException(NativeWebRequest req, AuthorityException e) {
        this.handleExceptions(req, (Exception)e);
        return Result.fail((Code)ResultEnum.LIMIT_ERROR.code(), (String)"\u9650\u6d41\u6743\u9650\u63a7\u5236\u5f02\u5e38");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={RateLimitException.class})
    public Result<String> rateLimitException(RateLimitException e) {
        return Result.fail((Code)ResultEnum.LIMIT_ERROR.code(), (String)"\u9650\u6d41\u6743\u9650\u63a7\u5236\u5f02\u5e38");
    }

    private String getErrors(BindingResult result) {
        String errorMsg = "";
        HashMap<String, String> map = new HashMap<String, String>();
        List list = result.getFieldErrors();
        for (FieldError error : list) {
            map.put(error.getField(), error.getDefaultMessage());
            errorMsg = error.getDefaultMessage();
        }
        return errorMsg;
    }

    private List<HashMap<String, String>> methodArgumentNotValidExceptionDescribe(BindException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        List fieldErrors = bindingResult.getFieldErrors();
        return fieldErrors.stream().map(fieldError -> {
            HashMap<String, String> map = new HashMap<String, String>(1);
            map.put(fieldError.getField(), fieldError.getDefaultMessage());
            return map;
        }).collect(Collectors.toList());
    }

    private String getErrors(ConstraintViolationException e) {
        String errorMsg = "";
        HashMap<String, String> map = new HashMap<String, String>();
        Set constraintViolations = e.getConstraintViolations();
        for (ConstraintViolation constraintViolation : constraintViolations) {
            String property = constraintViolation.getPropertyPath().toString();
            String message = constraintViolation.getMessage();
            map.put(property, message);
            errorMsg = message;
        }
        return errorMsg;
    }

    public void handleExceptions(NativeWebRequest req, Exception e) {
        String traceId = this.getTraceId();
        try {
            HttpServletRequest nativeRequest = (HttpServletRequest)req.getNativeRequest(HttpServletRequest.class);
            HttpServletResponse nativeResponse = (HttpServletResponse)req.getNativeResponse(HttpServletResponse.class);
            LogUtils.error((Throwable)e);
            if (nativeRequest != null) {
                LogUtils.error((String)"\u8bf7\u6c42\u5931\u8d25\u8fd4\u56de request:{} traceId:{} url:{} \u5f02\u5e38\u6d88\u606f:{}", (Object[])new Object[]{req.getDescription(true), traceId, nativeRequest.getRequestURL(), e.getMessage()});
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.publishEvent(e);
        this.asyncThreadPoolTaskExecutor.submit(() -> this.exceptionHandler.forEach(handler -> handler.handle(req, e, traceId)));
    }

    private void publishEvent(Throwable error) {
        ErrorEvent event = new ErrorEvent();
        event.setRequestId(this.getTraceId());
        event.setRequestType("api");
        HttpServletRequest request = RequestUtils.getRequest();
        event.setRequestMethod(request.getMethod());
        Object requestUrl = request.getRequestURI();
        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank((String)queryString)) {
            requestUrl = (String)requestUrl + "?" + queryString;
        }
        event.setRequestIp(RequestUtils.getHttpServletRequestIpAddress((HttpServletRequest)request));
        event.setRequestUrl((String)requestUrl);
        ErrorUtils.initErrorInfo(error, event);
        this.applicationEventPublisher.publishEvent((Object)event);
    }

    private String getTraceId() {
        String traceId = TraceContextHolder.getTraceId();
        if (traceId == null) {
            traceId = TraceUtils.getKmcTraceId();
        }
        if (traceId == null) {
            traceId = IdGeneratorUtils.getIdStr();
        }
        return traceId;
    }

    private ErrorMapping findMethodMapping(HandlerMethod handlerMethod, Exception ex) {
        Method method = handlerMethod.getMethod();
        for (ErrorMapping mapping : (ErrorMapping[])method.getAnnotationsByType(ErrorMapping.class)) {
            if (!mapping.exception().isInstance(ex)) continue;
            return mapping;
        }
        return null;
    }

    private ErrorMapping findClassMapping(HandlerMethod handlerMethod, Exception ex) {
        Class clazz = handlerMethod.getBeanType();
        for (ErrorMapping mapping : (ErrorMapping[])clazz.getAnnotationsByType(ErrorMapping.class)) {
            if (!mapping.exception().isInstance(ex)) continue;
            return mapping;
        }
        return null;
    }

    private ErrorResponseBody getResponseConfig(HandlerMethod handlerMethod) {
        return handlerMethod.getBeanType().getAnnotation(ErrorResponseBody.class);
    }
}

