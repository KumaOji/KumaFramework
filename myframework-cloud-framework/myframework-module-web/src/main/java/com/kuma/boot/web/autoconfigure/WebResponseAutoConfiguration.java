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

package com.kuma.boot.web.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.ResponseUtils;
import com.kuma.boot.web.annotation.BusinessApi;
import com.kuma.boot.web.annotation.IgnoreResponseBodyAdvice;
import jakarta.servlet.Servlet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局统一返回值包装器 自动配置
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 21:28:49
 */
@AutoConfiguration
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
// @RestControllerAdvice(basePackages = {"com.kuma.cloud.*.biz.api.controller"})
@RestControllerAdvice(annotations = BusinessApi.class)
public class WebResponseAutoConfiguration implements ResponseBodyAdvice<Object>, InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(WebResponseAutoConfiguration.class, StarterNameConstants.WEB_STARTER);
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        // 类上如果被 IgnoreResponseBodyAdvice 标识就不拦截
        if (methodParameter
                .getDeclaringClass()
                .isAnnotationPresent(IgnoreResponseBodyAdvice.class)) {
            return false;
        }

        // 方法上被标注也不拦截
        return !methodParameter.getMethod().isAnnotationPresent(IgnoreResponseBodyAdvice.class);
    }

    @Override
    public Object beforeBodyWrite(
            Object o,
            MethodParameter methodParameter,
            MediaType mediaType,
            Class aClass,
            ServerHttpRequest serverHttpRequest,
            ServerHttpResponse serverHttpResponse) {

        ResponseUtils.addResponseHeader(serverHttpResponse);

        if (o == null) {
            return null;
        }

        if (o instanceof Result) {
            return o;
        }

        return Result.success(o);
    }
}
