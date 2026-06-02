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

package com.kuma.boot.auditlog.core;

import com.kuma.boot.auditlog.autoconfigure.properties.AuditLogProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 默认操作人解析：从请求头读取（由网关 / 鉴权过滤器写入）.
 *
 * <p>读取的头名称可通过 {@code kuma.boot.audit-log.operator-id-header} /
 * {@code operator-name-header} 配置。无 HTTP 上下文（异步线程、定时任务）时返回 null。
 *
 * @author kuma
 */
public class DefaultAuditOperatorProvider implements AuditOperatorProvider {

    private final AuditLogProperties properties;

    public DefaultAuditOperatorProvider(AuditLogProperties properties) {
        this.properties = properties;
    }

    @Override
    public String currentOperatorId() {
        return header(properties.getOperatorIdHeader());
    }

    @Override
    public String currentOperatorName() {
        return header(properties.getOperatorNameHeader());
    }

    private String header(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        HttpServletRequest request = currentRequest();
        return request == null ? null : request.getHeader(name);
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            return servletAttributes.getRequest();
        }
        return null;
    }
}
