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

package com.kuma.boot.ip2region.ip2region.support;

import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.ip2region.ip2region.annotation.RequestIp;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * <p>
 * RequestIPMethodArgumentResolver
 * </p>
 *
 *
 */
public class RequestIPMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestIp.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        if (parameter.getParameterType().isAssignableFrom(String.class)) {
            return com.kuma.boot.ip2region.ip2region.support.RequestIpContextHolder.computeIfAbsent(() -> parseIp(webRequest));
        }
        throw new RuntimeException("param not support " + parameter.getParameterType());
    }

    private String parseIp(NativeWebRequest webRequest) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        Assert.notNull(request, "request not be null");
        return RequestUtils.getHttpServletRequestIpAddress(request);
    }
}
