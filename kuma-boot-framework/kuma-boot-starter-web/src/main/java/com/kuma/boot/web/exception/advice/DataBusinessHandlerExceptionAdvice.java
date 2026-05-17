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

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.constant.StrPoolConstants;
import com.kuma.boot.common.holder.TraceContextHolder;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.utils.servlet.RequestUtils;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import com.kuma.boot.web.exception.event.ErrorEvent;
import com.kuma.boot.web.exception.handler.ExceptionHandler;
import com.kuma.boot.web.utils.ErrorUtils;
import com.kuma.boot.web.annotation.BusinessApi;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;

/**
 * 全局异常处理
 */
@AutoConfiguration
// @ConditionalOnExpression("!'${security.oauth2.client.clientId}'.isEmpty()")
// @RestControllerAdvice
// @RestControllerAdvice(basePackages = {"com.kuma.cloud.**.biz.controller.business"})
@RestControllerAdvice(annotations = BusinessApi.class)
public class DataBusinessHandlerExceptionAdvice implements InitializingBean {

    private final List<ExceptionHandler> exceptionHandler;

    @Autowired
    @Qualifier("asyncThreadPoolTaskExecutor")
    private ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor;

    @Autowired private ApplicationEventPublisher applicationEventPublisher;

    public DataBusinessHandlerExceptionAdvice(List<ExceptionHandler> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(
                DataBusinessHandlerExceptionAdvice.class, StarterNameConstants.WEB_STARTER);
    }

    // @org.springframework.web.bind.annotation.ExceptionHandler({PersistenceException.class})
    // public Result<String> handleSqlException(NativeWebRequest req, PersistenceException e) {
    //    handleExceptions(req, e);
    //    return Result.fail(ResultEnum.FAILED);
    // }

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

    public void handleExceptions(NativeWebRequest req, Exception e) {
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

    private void publishEvent(Throwable error) {
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
            traceId = TraceUtils.getKmcTraceId();
        }
        if (traceId == null) {
            traceId = IdGeneratorUtils.getIdStr();
        }
        return traceId;
    }
}
