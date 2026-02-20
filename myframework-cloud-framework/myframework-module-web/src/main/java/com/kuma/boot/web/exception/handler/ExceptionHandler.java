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

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Objects;

/**
 * 异常日志处理类
 *
 * @author kuma
 * @version 2023.05
 * @since 2023-05-05 10:49:15
 */
public interface ExceptionHandler {

    /**
     * 在此处理错误信息 进行落库，入ES， 发送报警通知等信息
     *
     * @param req 异常
     * @param throwable 异常
     * @param traceId traceId
     */
    void handle(NativeWebRequest req, Throwable throwable, String traceId);

    /**
     * 获取请求路径
     *
     * @param request 请求对象
     * @return 请求路径
     * @since 2021-09-02 21:27:08
     */
    default String uri(NativeWebRequest request) {
        HttpServletRequest nativeRequest = request.getNativeRequest(HttpServletRequest.class);
        if (Objects.nonNull(nativeRequest)) {
            return nativeRequest.getRequestURI();
        } else {
            return "--";
        }
    }

    /**
     * 获取请求参数
     *
     * @param request 请求对象
     * @return 请求参数
     * @since 2021-09-02 21:27:14
     */
    default String query(NativeWebRequest request) {
        HttpServletRequest nativeRequest = request.getNativeRequest(HttpServletRequest.class);
        if (Objects.nonNull(nativeRequest)) {
            String queryString = nativeRequest.getQueryString();
            if (StrUtil.isNotBlank(queryString)) {
                return queryString;
            }
        }
        return "--";
    }
}
