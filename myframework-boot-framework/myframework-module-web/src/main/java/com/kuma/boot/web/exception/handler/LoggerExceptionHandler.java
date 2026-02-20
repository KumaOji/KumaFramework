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

package com.kuma.boot.web.exception.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/** 默认的异常日志处理类 */
public class LoggerExceptionHandler implements com.kuma.boot.web.exception.handler.ExceptionHandler {

    private RequestMappingHandlerMapping mapping;

    public LoggerExceptionHandler(RequestMappingHandlerMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public void handle(NativeWebRequest req, Throwable throwable, String traceId) {
        printLog(req, throwable);
    }

    /**
     * 打印日志
     *
     * @param req 请求对象
     * @param e 异常信息
     * @since 2021-09-02 21:27:34
     */
    private void printLog(NativeWebRequest req, Throwable e) {
        try {
            // RequestMappingHandlerMapping mapping =
            // ContextUtils.getBean("requestMappingHandlerMapping",RequestMappingHandlerMapping.class);
            HandlerExecutionChain chain =
                    mapping.getHandler((HttpServletRequest) req.getNativeRequest());
            Object handler = chain.getHandler();
            if (handler instanceof HandlerMethod handlerMethod) {
                MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
                Object bean = handlerMethod.getBean();
            }
        } catch (Exception ex) {
            LogUtils.error(e);
        }

        LogUtils.error(
                e,
                "【全局异常拦截】{}: 请求路径: {}, 请求参数: {}, 异常信息 {} ",
                e.getClass().getName(),
                uri(req),
                query(req),
                e.getMessage());
    }
}
