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

package com.kuma.boot.security.satoken.interceptor;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.kuma.boot.security.satoken.annotation.NotAuth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Sa-Token 路由拦截器.
 *
 * <p>对所有请求执行登录校验；以下情况跳过：
 * <ul>
 *   <li>处理器不是 {@link HandlerMethod}（如静态资源）</li>
 *   <li>方法或类上标注了框架 {@link NotAuth} 注解</li>
 *   <li>方法或类上标注了 Sa-Token 原生 {@link SaIgnore} 注解</li>
 * </ul>
 *
 * <p>路由级白名单通过 {@code kuma.boot.security.sa-token.exclude-urls} 配置，
 * 在 {@link com.kuma.boot.security.satoken.configuration.SaTokenAutoConfiguration}
 * 注册拦截器时排除。
 *
 * @author kuma
 */
public class SaTokenRouteInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        if (isIgnored(handlerMethod)) {
            return true;
        }
        StpUtil.checkLogin();
        return true;
    }

    private boolean isIgnored(HandlerMethod handlerMethod) {
        return handlerMethod.hasMethodAnnotation(NotAuth.class)
                || AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), NotAuth.class) != null
                || handlerMethod.hasMethodAnnotation(SaIgnore.class)
                || AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), SaIgnore.class) != null;
    }
}
