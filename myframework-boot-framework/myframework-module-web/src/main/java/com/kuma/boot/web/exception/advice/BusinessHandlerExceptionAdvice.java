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
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.exception.*;
import com.kuma.boot.common.holder.TraceContextHolder;
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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.NestedCheckedException;
import org.springframework.core.NestedRuntimeException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
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

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

import static com.kuma.boot.common.enums.ResultEnum.BAD_REQUEST;
import static com.kuma.boot.common.enums.ResultEnum.LIMIT_ERROR;

/**
 * 全局异常处理
 */
@AutoConfiguration
// @ConditionalOnExpression("!'${security.oauth2.client.clientId}'.isEmpty()")
// @RestControllerAdvice
// @RestControllerAdvice(basePackages = {"com.kuma.cloud.**.biz.controller.business"})
@RestControllerAdvice(annotations = BusinessApi.class)
public class BusinessHandlerExceptionAdvice implements InitializingBean {

    private final List<ExceptionHandler> exceptionHandler;

    @Autowired
    @Qualifier("asyncThreadPoolTaskExecutor")
    private ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public BusinessHandlerExceptionAdvice( List<ExceptionHandler> exceptionHandler ) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(BusinessHandlerExceptionAdvice.class, StarterNameConstants.WEB_STARTER);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({BaseException.class})
    public Result<String> baseException( NativeWebRequest req, BaseException e ) {
        handleExceptions(req, e);
        return Result.fail(e.getCode(), e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({InnerException.class})
    public Result<String> innerException( NativeWebRequest req, InnerException e ) {
        handleExceptions(req, e);
        return Result.fail(ResultEnum.INNER_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({LockException.class})
    public Result<String> lockException( NativeWebRequest req, LockException e ) {
        handleExceptions(req, e);
        return Result.fail(ResultEnum.FAILED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({IdempotencyException.class})
    public Result<String> idempotencyException( NativeWebRequest req, IdempotencyException e ) {
        handleExceptions(req, e);
        return Result.fail(ResultEnum.FAILED);
    }

    /**
     * 自定义异常处理方法
     */
    @org.springframework.web.bind.annotation.ExceptionHandler({BusinessException.class})
    public Result<String> businessException( NativeWebRequest req, BusinessException e ) {
        handleExceptions(req, e);
        return Result.fail(e.getCode(), e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({IllegalArgumentException.class})
    public Result<String> illegalArgumentException(
            NativeWebRequest req, IllegalArgumentException e ) {
        handleExceptions(req, e);
        return Result.fail(ResultEnum.ILLEGAL_ARGUMENT_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({AccessDeniedException.class})
    public Result<String> badMethodExpressException( NativeWebRequest req, AccessDeniedException e ) {
        handleExceptions(req, e);
        return Result.fail(ResultEnum.FORBIDDEN);
    }


    @org.springframework.web.bind.annotation.ExceptionHandler({UsernameNotFoundException.class})
    public Result<String> badUsernameNotFoundException(
            NativeWebRequest req, UsernameNotFoundException e ) {
        handleExceptions(req, e);
        return Result.fail(ResultEnum.USERNAME_OR_PASSWORD_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
            DataIntegrityViolationException.class
    })
    public Result<String> handleDataIntegrityViolationException(
            NativeWebRequest req, DataIntegrityViolationException e ) {
        handleExceptions(req, e);
        return Result.fail(ResultEnum.FAILED);
    }

//	@org.springframework.web.bind.annotation.ExceptionHandler({DecodeException.class})
//	public Result<String> handleDecodeException(NativeWebRequest req, DecodeException e) {
//		handleExceptions(req, e);
//		return Result.fail(ResultEnum.FAILED);
//	}

    @org.springframework.web.bind.annotation.ExceptionHandler({LimitException.class})
    public Result<String> limitException( NativeWebRequest req, LimitException e ) {
        handleExceptions(req, e);
        return Result.fail(e.getCode(), e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({IdempotentException.class})
    public Result<String> idempotentException( NativeWebRequest req, IdempotentException e ) {
        handleExceptions(req, e);
        return Result.fail(e.getCode(), e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NestedRuntimeException.class)
    public Result<String> handleNestedRuntimeException(
            NativeWebRequest req, NestedRuntimeException e ) {
        handleExceptions(req, e);
        return Result.fail(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NestedCheckedException.class)
    public Result<String> handleNestedCheckedException(
            NativeWebRequest req, NestedCheckedException e ) {
        handleExceptions(req, e);
        return Result.fail(e.getMessage());
    }

    // *****************************参数校验错误***************************************

    /**
     * 校验表单提交参数
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(value = BindException.class)
    public Result<Map<String, String>> handleBindException( NativeWebRequest req, BindException e ) {
        handleExceptions(req, e);

        List<HashMap<String, String>> errors = methodArgumentNotValidExceptionDescribe(e);
        String message =
                Optional.ofNullable(errors).orElse(Collections.emptyList()).stream()
                        .flatMap(s -> s.values().stream())
                        .findFirst()
                        .orElse("请求参数错误");

        BindingResult bindingResult = e.getBindingResult();
        return Result.fail(BAD_REQUEST.code(), getErrors(bindingResult));
    }

    /**
     * 处理 json 请求体调用接口对象参数校验失败抛出的异常  对应的是校验RequestBody入参
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Map<String, String>> handleMethodArgumentNotValidException(
            NativeWebRequest req, MethodArgumentNotValidException e ) {
        handleExceptions(req, e);

        List<HashMap<String, String>> errors = methodArgumentNotValidExceptionDescribe(e);
        String message =
                Optional.ofNullable(errors).orElse(Collections.emptyList()).stream()
                        .flatMap(s -> s.values().stream())
                        .findFirst()
                        .orElse("请求参数错误");

        BindingResult bindingResult = e.getBindingResult();
        return Result.fail(BAD_REQUEST.code(), getErrors(bindingResult));
    }

    /**
     * get请求 异常对应的就是校验RequestParam参数和校验PathVariable参数
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
    public Result<Map<String, String>> handleException(
            NativeWebRequest req, ConstraintViolationException e ) {
        handleExceptions(req, e);
        return Result.fail(BAD_REQUEST.code(), getErrors(e));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
            MethodArgumentTypeMismatchException.class
    })
    public Result<String> requestTypeMismatch(
            NativeWebRequest req, MethodArgumentTypeMismatchException e ) {
        handleExceptions(req, e);
        return Result.fail(ResultEnum.METHOD_ARGUMENTS_TYPE_MISMATCH);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
            MissingServletRequestParameterException.class
    })
    public Result<String> requestMissingServletRequest(
            NativeWebRequest req, MissingServletRequestParameterException e ) {
        handleExceptions(req, e);
        return Result.fail(ResultEnum.MISSING_SERVLET_REQUEST_PARAMETER);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
            HttpMessageNotReadableException.class
    })
    public Result<String> httpMessageNotReadableException(
            NativeWebRequest req, HttpMessageNotReadableException e ) {
        handleExceptions(req, e);
        return Result.fail(ResultEnum.HTTP_MESSAGE_NOT_READABLE);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ValidationException.class)
    public Result<String> handleException( NativeWebRequest req, ValidationException e ) {
        handleExceptions(req, e);
        return Result.fail(BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
            HttpRequestMethodNotSupportedException.class
    })
    public Result<String> handleHttpRequestMethodNotSupportedException(
            NativeWebRequest req, HttpRequestMethodNotSupportedException e ) {
        handleExceptions(req, e);
        return Result.fail(ResultEnum.METHOD_NOT_SUPPORTED_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
            HttpMediaTypeNotSupportedException.class
    })
    public Result<String> handleHttpMediaTypeNotSupportedException(
            NativeWebRequest req, HttpMediaTypeNotSupportedException e ) {
        handleExceptions(req, e);
        return Result.fail(ResultEnum.MEDIA_TYPE_NOT_SUPPORTED_ERROR);
    }

    // *******************************sql错误***************************************

    @org.springframework.web.bind.annotation.ExceptionHandler({SQLException.class})
    public Result<String> handleSqlException( NativeWebRequest req, SQLException e ) {
        handleExceptions(req, e);
        return Result.fail(ResultEnum.FAILED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
            SQLIntegrityConstraintViolationException.class
    })
    public Result<String> handleSqlException(
            NativeWebRequest req, SQLIntegrityConstraintViolationException e ) {
        handleExceptions(req, e);
        return Result.fail(ResultEnum.FAILED);
    }

    // @org.springframework.web.bind.annotation.ExceptionHandler({PersistenceException.class})
    // public Result<String> handleSqlException(NativeWebRequest req, PersistenceException e) {
    //    handleExceptions(req, e);
    //    return Result.fail(ResultEnum.FAILED);
    // }

    /**
     * 主键或UNIQUE索引，数据重复异常
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(DuplicateKeyException.class)
    public Result<String> handleDuplicateKeyException(
            DuplicateKeyException e, HttpServletRequest request ) {
        String requestURI = request.getRequestURI();
        LogUtils.error("请求地址'{}',数据库中已存在记录'{}'", requestURI, e.getMessage());
        return Result.fail("数据库中已存在该记录，请联系管理员确认");
    }

    /**
     * Mybatis系统异常 通用处理
     */
    // @org.springframework.web.bind.annotation.ExceptionHandler(MyBatisSystemException.class)
    // public Result<String> handleCannotFindDataSourceException(MyBatisSystemException e,
    // HttpServletRequest request) {
    //    String requestURI = request.getRequestURI();
    //    String message = e.getMessage();
    //    if (message.contains("CannotFindDataSourceException")) {
    //        LogUtils.error("请求地址'{}', 未找到数据源", requestURI);
    //        return Result.fail("未找到数据源，请联系管理员确认");
    //    }
    //    LogUtils.error("请求地址'{}', Mybatis系统异常", requestURI, e);
    //    return Result.fail(message);
    // }

    // *******************************通用错误***************************************
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public Result<String> handleException( NativeWebRequest req, Exception e ) {
        handleExceptions(req, e);
        return Result.fail();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Error.class)
    public Result<String> handleThrowable( NativeWebRequest req, Error e ) {
        handleExceptions(req, new Exception(e.getMessage()));
        return Result.fail();
    }

    // *******************************限流错误***************************************

    @org.springframework.web.bind.annotation.ExceptionHandler(UndeclaredThrowableException.class)
    public Result<String> handleUndeclaredThrowableException(
            NativeWebRequest req, UndeclaredThrowableException ex ) {
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

        return Result.fail(LIMIT_ERROR.code(), errMsg);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(BlockException.class)
    public Result<String> handleBlockException( NativeWebRequest req, BlockException e ) {
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
        return Result.fail(LIMIT_ERROR.code(), errMsg);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(FlowException.class)
    public Result<String> handleFlowException( NativeWebRequest req, FlowException e ) {
        handleExceptions(req, e);
        return Result.fail(LIMIT_ERROR.code(), "被限流了");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DegradeException.class)
    public Result<String> handleDegradeException( NativeWebRequest req, DegradeException e ) {
        handleExceptions(req, e);
        return Result.fail(LIMIT_ERROR.code(), "服务降级了");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ParamFlowException.class)
    public Result<String> handleParamFlowException( NativeWebRequest req, ParamFlowException e ) {
        handleExceptions(req, e);
        return Result.fail(LIMIT_ERROR.code(), "服务热点降级了");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(SystemBlockException.class)
    public Result<String> handleSystemBlockException( NativeWebRequest req, SystemBlockException e ) {
        handleExceptions(req, e);
        return Result.fail(LIMIT_ERROR.code(), "系统过载保护");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AuthorityException.class)
    public Result<String> handleAuthorityException( NativeWebRequest req, AuthorityException e ) {
        handleExceptions(req, e);
        return Result.fail(LIMIT_ERROR.code(), "限流权限控制异常");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = RateLimitException.class)
    public Result<String> rateLimitException( RateLimitException e ) {
        return Result.fail(LIMIT_ERROR.code(), "限流权限控制异常");
    }

    /**
     * 获取Binding错误数据
     *
     * @param result 请求对象
     * @return 错误数据
     * @since 2021-09-02 21:27:21
     */
    private String getErrors( BindingResult result ) {
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

    /**
     * 格式化请求参数异常
     **/
    private List<HashMap<String, String>> methodArgumentNotValidExceptionDescribe(
            BindException exception ) {
        BindingResult bindingResult = exception.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        return fieldErrors.stream()
                .map(
                        fieldError -> {
                            HashMap<String, String> map = new HashMap<>(1);
                            map.put(fieldError.getField(), fieldError.getDefaultMessage());
                            return map;
                        })
                .collect(Collectors.toList());
    }

    /**
     * 获取校验错误数据
     *
     * @param e 异常信息
     * @return 校验错误数据
     * @since 2021-09-02 21:27:27
     */
    private String getErrors( ConstraintViolationException e ) {
        String errorMsg = "";
        Map<String, String> map = new HashMap<>();
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            String property = constraintViolation.getPropertyPath().toString();
            String message = constraintViolation.getMessage();
            map.put(property, message);

            errorMsg = message;
        }
        // return JsonUtil.toJSONString(map);
        return errorMsg;
    }

    public void handleExceptions( NativeWebRequest req, Exception e ) {
        String traceId = getTraceId();

        try {
            HttpServletRequest nativeRequest = req.getNativeRequest(HttpServletRequest.class);
            HttpServletResponse nativeResponse = req.getNativeResponse(HttpServletResponse.class);

            LogUtils.error(e);
            if (nativeRequest != null) {
                LogUtils.error(
                        "请求失败返回 request:{} traceId:{} url:{} 异常消息:{}",
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

    private void publishEvent( Throwable error ) {
        ErrorEvent event = new ErrorEvent();
        // 服务异常类型
        // event.setErrorType(ErrorType.REQUEST);
        // 异步获取不到的一些信息
        event.setRequestId(getTraceId());
        event.setRequestType("api");

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


//	@ExceptionHandler(Exception.class)
//	public ResponseEntity<ErrorResponse> handleException( Exception ex, HandlerMethod handlerMethod,
//		HttpServletRequest request ) {    // 1.获取方法级异常配置
//		ErrorMapping errorMapping = findMethodMapping(handlerMethod, ex);
//		if (errorMapping == null) {      // 2.获取类级别异常配置
//			errorMapping = findClassMapping(handlerMethod, ex);
//		}    // 3.获取类上的响应配置
//		ErrorResponseBody responseConfig = getResponseConfig(handlerMethod);    // 4.构建错误响应
//		ErrorResponse errorResponse = buildErrorResponse(ex, request, errorMapping, responseConfig);    // 5.记录日志
//		logError(ex, responseConfig);    // 6.返回响应
//		return ResponseEntity.status(errorMapping != null ? errorMapping.httpStatus() : 500).body(errorResponse);
//	}

    // 查找方法上的注解映射
    private ErrorMapping findMethodMapping( HandlerMethod handlerMethod, Exception ex ) {
        Method method = handlerMethod.getMethod();
        for (ErrorMapping mapping : method.getAnnotationsByType(ErrorMapping.class)) {
            if (mapping.exception().isInstance(ex)) {
                return mapping;
            }
        }
        return null;
    }

    private ErrorMapping findClassMapping( HandlerMethod handlerMethod, Exception ex ) {
        Class<?> clazz = handlerMethod.getBeanType();
        for (ErrorMapping mapping : clazz.getAnnotationsByType(ErrorMapping.class)) {
            if (mapping.exception().isInstance(ex)) {
                return mapping;
            }
        }
        return null;
    }

    private ErrorResponseBody getResponseConfig( HandlerMethod handlerMethod ) {
        return handlerMethod.getBeanType().getAnnotation(ErrorResponseBody.class);
    }
}
