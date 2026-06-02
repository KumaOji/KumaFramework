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

package com.kuma.cloud.openfeign.interceptor;

import com.kuma.cloud.openfeign.properties.OpenFeignCloudProperties;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 请求头透传拦截器.
 *
 * <p>当前服务在处理入站 HTTP 请求时，通过 Feign 发起对下游服务的出站调用，
 * 自动把入站请求中配置的请求头（鉴权 Token、TraceId、灰度标签等）复制到出站请求，
 * 从而贯穿整条微服务调用链。
 *
 * <p>仅在存在 Servlet 请求上下文（RequestContextHolder）时生效；异步线程、定时任务、
 * MQ 消费等无 HTTP 上下文的场景不会透传。
 *
 * @author kuma
 */
public class FeignHeaderPropagationInterceptor implements RequestInterceptor {

    private final List<String> headerNames;

    public FeignHeaderPropagationInterceptor(OpenFeignCloudProperties properties) {
        this.headerNames = properties.getPropagation().getHeaders();
    }

    @Override
    public void apply(RequestTemplate template) {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return;
        }
        for (String name : headerNames) {
            // 出站请求已显式设置同名头时不覆盖
            if (!template.headers().containsKey(name)) {
                String value = request.getHeader(name);
                if (value != null && !value.isBlank()) {
                    template.header(name, value);
                }
            }
        }
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            return servletAttributes.getRequest();
        }
        return null;
    }
}
