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

package com.kuma.boot.web.exception.advice;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.constant.StrPoolConstants;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.holder.TraceContextHolder;
import com.kuma.boot.common.model.result.ExceptionResult;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import com.kuma.boot.web.exception.event.ErrorEvent;
import com.kuma.boot.web.exception.handler.ExceptionHandler;
import com.kuma.boot.web.utils.ErrorUtils;
import com.kuma.boot.web.annotation.InnerApi;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.NestedCheckedException;
import org.springframework.core.NestedRuntimeException;
import org.springframework.core.Ordered;
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

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kuma.boot.common.enums.ResultEnum.*;

/**
 * 全局异常处理
 */
@AutoConfiguration
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(annotations = {InnerApi.class})
// @RestControllerAdvice(basePackages = {"com.kuma.cloud.*.biz.controller.feign"})
public class InnerApiExceptionAdvice implements InitializingBean {

    private final List<ExceptionHandler> exceptionHandler;
    private final ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor;
    private final ApplicationEventPublisher applicationEventPublisher;

    public InnerApiExceptionAdvice(
            List<ExceptionHandler> exceptionHandler,
            ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor,
            ApplicationEventPublisher applicationEventPublisher) {
        this.exceptionHandler = exceptionHandler;
        this.asyncThreadPoolTaskExecutor = asyncThreadPoolTaskExecutor;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(InnerApiExceptionAdvice.class, StarterNameConstants.WEB_STARTER);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBusinessException(NativeWebRequest req, BusinessException e) {
        handleExceptions(req, e);
        return JacksonUtils.toJSONString(new ExceptionResult(e.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NestedRuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNestedRuntimeException(NativeWebRequest req, NestedRuntimeException e) {
        handleExceptions(req, e);
        return JacksonUtils.toJSONString(new ExceptionResult(e.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NestedCheckedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNestedCheckedException(NativeWebRequest req, NestedCheckedException e) {
        handleExceptions(req, e);
        return JacksonUtils.toJSONString(new ExceptionResult(e.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(NativeWebRequest req, Exception e) {
        handleExceptions(req, e);
        return JacksonUtils.toJSONString(new ExceptionResult(e.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UndeclaredThrowableException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public String handleUndeclaredThrowableException(
            NativeWebRequest req, UndeclaredThrowableException ex) {
        handleExceptions(req, ex);
        Throwable e = ex.getCause();

        LogUtils.error("WebmvcHandler sentinel 降级 资源名称");
        String errMsg = e.getMessage();
        if (e instanceof FlowException) {
            errMsg = "被限流了";
        }
        if (e instanceof DegradeException) {
            errMsg = "服务降级了";
        }
        if (e instanceof ParamFlowException) {
            errMsg = "服务热点降级了";
        }
        if (e instanceof SystemBlockException) {
            errMsg = "系统过载保护";
        }
        if (e instanceof AuthorityException) {
            errMsg = "限流权限控制异常";
        }

        return JacksonUtils.toJSONString(new ExceptionResult(errMsg));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(BlockException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public String handleBlockException(NativeWebRequest req, BlockException e) {
        handleExceptions(req, e);
        LogUtils.error("WebmvcHandler sentinel 降级 资源名称{}", e, e.getRule().getResource());
        String errMsg = e.getMessage();
        if (e instanceof FlowException) {
            errMsg = "被限流了";
        }
        if (e instanceof DegradeException) {
            errMsg = "服务降级了";
        }
        if (e instanceof ParamFlowException) {
            errMsg = "服务热点降级了";
        }
        if (e instanceof SystemBlockException) {
            errMsg = "系统过载保护";
        }
        if (e instanceof AuthorityException) {
            errMsg = "限流权限控制异常";
        }
        return JacksonUtils.toJSONString(new ExceptionResult(errMsg));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(FlowException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public String handleFlowException(NativeWebRequest req, FlowException e) {
        handleExceptions(req, e);
        return JacksonUtils.toJSONString(new ExceptionResult("被限流了"));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DegradeException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public String handleDegradeException(NativeWebRequest req, DegradeException e) {
        handleExceptions(req, e);
        return JacksonUtils.toJSONString(new ExceptionResult("服务降级了"));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ParamFlowException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public String handleParamFlowException(NativeWebRequest req, ParamFlowException e) {
        handleExceptions(req, e);
        return JacksonUtils.toJSONString(new ExceptionResult("服务热点降级了"));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(SystemBlockException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public String handleSystemBlockException(NativeWebRequest req, SystemBlockException e) {
        handleExceptions(req, e);
        return JacksonUtils.toJSONString(new ExceptionResult("系统过载保护"));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AuthorityException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public String handleAuthorityException(NativeWebRequest req, AuthorityException e) {
        handleExceptions(req, e);
        return JacksonUtils.toJSONString(new ExceptionResult("限流权限控制异常"));
    }

    // *****************************参数校验错误***************************************

    /**
     * 处理 form data方式调用接口对象参数校验失败抛出的异常
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(value = BindException.class)
    public String handleBindException(NativeWebRequest req, BindException e) {
        handleExceptions(req, e);
        BindingResult bindingResult = e.getBindingResult();
        return getErrors(bindingResult);
    }

    /**
     * 处理 json 请求体调用接口对象参数校验失败抛出的异常
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleMethodArgumentNotValidException(
            NativeWebRequest req, MethodArgumentNotValidException e) {
        handleExceptions(req, e);
        BindingResult bindingResult = e.getBindingResult();
        return getErrors(bindingResult);
    }

    /**
     * 处理Get请求中 验证路径中 单个参数请求失败抛出异常
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
    public String handleException(NativeWebRequest req, ConstraintViolationException e) {
        handleExceptions(req, e);
        return BAD_REQUEST.getDesc();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
            MethodArgumentTypeMismatchException.class
    })
    public String requestTypeMismatch(NativeWebRequest req, MethodArgumentTypeMismatchException e) {
        handleExceptions(req, e);
        return METHOD_ARGUMENTS_TYPE_MISMATCH.getDesc();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
            MissingServletRequestParameterException.class
    })
    public String requestMissingServletRequest(
            NativeWebRequest req, MissingServletRequestParameterException e) {
        handleExceptions(req, e);
        return MISSING_SERVLET_REQUEST_PARAMETER.getDesc();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
            HttpMessageNotReadableException.class
    })
    public String httpMessageNotReadableException(
            NativeWebRequest req, HttpMessageNotReadableException e) {
        handleExceptions(req, e);
        return HTTP_MESSAGE_NOT_READABLE.getDesc();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ValidationException.class)
    public String handleException(NativeWebRequest req, ValidationException e) {
        handleExceptions(req, e);
        return BAD_REQUEST.getDesc();
    }

    public void handleExceptions(NativeWebRequest req, Exception e) {
        String traceId = getTraceId();

        try {
            HttpServletRequest nativeRequest = req.getNativeRequest(HttpServletRequest.class);
            HttpServletResponse nativeResponse = req.getNativeResponse(HttpServletResponse.class);

            LogUtils.error(e);
            if (nativeRequest != null) {
                LogUtils.error(
                        "inner 请求失败返回 request:{} traceId:{} url:{} 异常消息:{}",
                        req.getDescription(true),
                        traceId,
                        nativeRequest.getRequestURL(),
                        e.getMessage());
            }
        } catch (Exception ignored) {
        }

        publishEvent(e);

        asyncThreadPoolTaskExecutor.submit(
                () -> {
                    exceptionHandler.forEach(
                            handler -> {
                                handler.handle(req, e, traceId);
                            });
                });
    }

    private void publishEvent(Throwable error) {
        ErrorEvent event = new ErrorEvent();
        // 服务异常类型
        // event.setErrorType(ErrorType.REQUEST);
        // 异步获取不到的一些信息
        event.setRequestId(getTraceId());
        event.setRequestType("inner");

        HttpServletRequest request = RequestUtils.getRequest();
        // 请求方法名
        event.setRequestMethod(request.getMethod());
        // 拼接地址
        String requestUrl = request.getRequestURI();
        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            requestUrl = requestUrl + StrPoolConstants.QUESTION_MARK + queryString;
        }
        // 请求ip
        event.setRequestIp(RequestUtils.getHttpServletRequestIpAddress(request));
        event.setRequestUrl(requestUrl);
        // 堆栈信息
        ErrorUtils.initErrorInfo(error, event);
        // 发布事件，其他参数可监听时异步获取
        applicationEventPublisher.publishEvent(event);
    }

    private String getTraceId() {
        String traceId = TraceContextHolder.getTraceId();
        if (traceId == null) {
            traceId = TraceUtils.getTtcTraceId();
        }
        if (traceId == null) {
            traceId = IdGeneratorUtils.getIdStr();
        }
        return traceId;
    }

    /**
     * 获取Binding错误数据
     *
     * @param result 请求对象
     * @return 错误数据
     * @since 2021-09-02 21:27:21
     */
    private String getErrors(BindingResult result) {
        String errorMsg = "";

        Map<String, String> map = new HashMap<>();
        List<FieldError> list = result.getFieldErrors();
        for (FieldError error : list) {
            map.put(error.getField(), error.getDefaultMessage());

            errorMsg = error.getDefaultMessage();
        }
        // return JsonUtil.toJSONString(map);
        return errorMsg;
    }
}
