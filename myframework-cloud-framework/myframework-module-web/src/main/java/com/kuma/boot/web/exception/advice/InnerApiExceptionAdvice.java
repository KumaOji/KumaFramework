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
 *  com.kuma.boot.common.exception.BusinessException
 *  com.kuma.boot.common.holder.TraceContextHolder
 *  com.kuma.boot.common.model.result.ExceptionResult
 *  com.kuma.boot.common.utils.id.IdGeneratorUtils
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.common.utils.servlet.RequestUtils
 *  com.kuma.boot.common.utils.servlet.TraceUtils
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  jakarta.validation.ConstraintViolationException
 *  jakarta.validation.ValidationException
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.context.ApplicationEventPublisher
 *  org.springframework.core.NestedCheckedException
 *  org.springframework.core.NestedRuntimeException
 *  org.springframework.core.annotation.Order
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.converter.HttpMessageNotReadableException
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
 *  org.springframework.validation.BindException
 *  org.springframework.validation.BindingResult
 *  org.springframework.validation.FieldError
 *  org.springframework.web.bind.MethodArgumentNotValidException
 *  org.springframework.web.bind.MissingServletRequestParameterException
 *  org.springframework.web.bind.annotation.ExceptionHandler
 *  org.springframework.web.bind.annotation.ResponseStatus
 *  org.springframework.web.bind.annotation.RestControllerAdvice
 *  org.springframework.web.context.request.NativeWebRequest
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
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.holder.TraceContextHolder;
import com.kuma.boot.common.model.result.ExceptionResult;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import com.kuma.boot.web.annotation.InnerApi;
import com.kuma.boot.web.exception.event.ErrorEvent;
import com.kuma.boot.web.exception.handler.ExceptionHandler;
import com.kuma.boot.web.utils.ErrorUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.NestedCheckedException;
import org.springframework.core.NestedRuntimeException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@AutoConfiguration
@Order(value=-2147483648)
@RestControllerAdvice(annotations={InnerApi.class})
public class InnerApiExceptionAdvice
implements InitializingBean {
    private final List<ExceptionHandler> exceptionHandler;
    private final ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor;
    private final ApplicationEventPublisher applicationEventPublisher;

    public InnerApiExceptionAdvice(List<ExceptionHandler> exceptionHandler, ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor, ApplicationEventPublisher applicationEventPublisher) {
        this.exceptionHandler = exceptionHandler;
        this.asyncThreadPoolTaskExecutor = asyncThreadPoolTaskExecutor;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void afterPropertiesSet() throws Exception {
        LogUtils.started(InnerApiExceptionAdvice.class, (String)"kuma-boot-starter-web", (String[])new String[0]);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={BusinessException.class})
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    public String handleBusinessException(NativeWebRequest req, BusinessException e) {
        this.handleExceptions(req, (Exception)e);
        return JacksonUtils.toJSONString((Object)new ExceptionResult(e.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={NestedRuntimeException.class})
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    public String handleNestedRuntimeException(NativeWebRequest req, NestedRuntimeException e) {
        this.handleExceptions(req, (Exception)e);
        return JacksonUtils.toJSONString((Object)new ExceptionResult(e.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={NestedCheckedException.class})
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    public String handleNestedCheckedException(NativeWebRequest req, NestedCheckedException e) {
        this.handleExceptions(req, (Exception)e);
        return JacksonUtils.toJSONString((Object)new ExceptionResult(e.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={Exception.class})
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    public String handleException(NativeWebRequest req, Exception e) {
        this.handleExceptions(req, e);
        return JacksonUtils.toJSONString((Object)new ExceptionResult(e.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={UndeclaredThrowableException.class})
    @ResponseStatus(value=HttpStatus.TOO_MANY_REQUESTS)
    public String handleUndeclaredThrowableException(NativeWebRequest req, UndeclaredThrowableException ex) {
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
        return JacksonUtils.toJSONString((Object)new ExceptionResult(errMsg));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={BlockException.class})
    @ResponseStatus(value=HttpStatus.TOO_MANY_REQUESTS)
    public String handleBlockException(NativeWebRequest req, BlockException e) {
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
        return JacksonUtils.toJSONString((Object)new ExceptionResult(errMsg));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={FlowException.class})
    @ResponseStatus(value=HttpStatus.TOO_MANY_REQUESTS)
    public String handleFlowException(NativeWebRequest req, FlowException e) {
        this.handleExceptions(req, (Exception)e);
        return JacksonUtils.toJSONString((Object)new ExceptionResult("\u88ab\u9650\u6d41\u4e86"));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={DegradeException.class})
    @ResponseStatus(value=HttpStatus.TOO_MANY_REQUESTS)
    public String handleDegradeException(NativeWebRequest req, DegradeException e) {
        this.handleExceptions(req, (Exception)e);
        return JacksonUtils.toJSONString((Object)new ExceptionResult("\u670d\u52a1\u964d\u7ea7\u4e86"));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={ParamFlowException.class})
    @ResponseStatus(value=HttpStatus.TOO_MANY_REQUESTS)
    public String handleParamFlowException(NativeWebRequest req, ParamFlowException e) {
        this.handleExceptions(req, (Exception)e);
        return JacksonUtils.toJSONString((Object)new ExceptionResult("\u670d\u52a1\u70ed\u70b9\u964d\u7ea7\u4e86"));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={SystemBlockException.class})
    @ResponseStatus(value=HttpStatus.TOO_MANY_REQUESTS)
    public String handleSystemBlockException(NativeWebRequest req, SystemBlockException e) {
        this.handleExceptions(req, (Exception)e);
        return JacksonUtils.toJSONString((Object)new ExceptionResult("\u7cfb\u7edf\u8fc7\u8f7d\u4fdd\u62a4"));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={AuthorityException.class})
    @ResponseStatus(value=HttpStatus.TOO_MANY_REQUESTS)
    public String handleAuthorityException(NativeWebRequest req, AuthorityException e) {
        this.handleExceptions(req, (Exception)e);
        return JacksonUtils.toJSONString((Object)new ExceptionResult("\u9650\u6d41\u6743\u9650\u63a7\u5236\u5f02\u5e38"));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={BindException.class})
    public String handleBindException(NativeWebRequest req, BindException e) {
        this.handleExceptions(req, (Exception)e);
        BindingResult bindingResult = e.getBindingResult();
        return this.getErrors(bindingResult);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={MethodArgumentNotValidException.class})
    public String handleMethodArgumentNotValidException(NativeWebRequest req, MethodArgumentNotValidException e) {
        this.handleExceptions(req, (Exception)e);
        BindingResult bindingResult = e.getBindingResult();
        return this.getErrors(bindingResult);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={ConstraintViolationException.class})
    public String handleException(NativeWebRequest req, ConstraintViolationException e) {
        this.handleExceptions(req, (Exception)e);
        return ResultEnum.BAD_REQUEST.getDesc();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={MethodArgumentTypeMismatchException.class})
    public String requestTypeMismatch(NativeWebRequest req, MethodArgumentTypeMismatchException e) {
        this.handleExceptions(req, (Exception)e);
        return ResultEnum.METHOD_ARGUMENTS_TYPE_MISMATCH.getDesc();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={MissingServletRequestParameterException.class})
    public String requestMissingServletRequest(NativeWebRequest req, MissingServletRequestParameterException e) {
        this.handleExceptions(req, (Exception)e);
        return ResultEnum.MISSING_SERVLET_REQUEST_PARAMETER.getDesc();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={HttpMessageNotReadableException.class})
    public String httpMessageNotReadableException(NativeWebRequest req, HttpMessageNotReadableException e) {
        this.handleExceptions(req, (Exception)e);
        return ResultEnum.HTTP_MESSAGE_NOT_READABLE.getDesc();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value={ValidationException.class})
    public String handleException(NativeWebRequest req, ValidationException e) {
        this.handleExceptions(req, (Exception)e);
        return ResultEnum.BAD_REQUEST.getDesc();
    }

    public void handleExceptions(NativeWebRequest req, Exception e) {
        String traceId = this.getTraceId();
        try {
            HttpServletRequest nativeRequest = (HttpServletRequest)req.getNativeRequest(HttpServletRequest.class);
            HttpServletResponse nativeResponse = (HttpServletResponse)req.getNativeResponse(HttpServletResponse.class);
            LogUtils.error((Throwable)e);
            if (nativeRequest != null) {
                LogUtils.error((String)"inner \u8bf7\u6c42\u5931\u8d25\u8fd4\u56de request:{} traceId:{} url:{} \u5f02\u5e38\u6d88\u606f:{}", (Object[])new Object[]{req.getDescription(true), traceId, nativeRequest.getRequestURL(), e.getMessage()});
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
        event.setRequestType("inner");
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
}

