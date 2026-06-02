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

package com.kuma.boot.auditlog.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 操作审计日志配置.
 *
 * @author kuma
 */
@Data
@ConfigurationProperties(prefix = AuditLogProperties.PREFIX)
public class AuditLogProperties {

    public static final String PREFIX = "kuma.boot.audit-log";

    /** 是否启用. */
    private boolean enabled = true;

    /** 是否异步落库（避免阻塞业务线程）. */
    private boolean async = true;

    /** 入参 / 出参 JSON 最大长度，超出截断，避免大对象撑爆存储. */
    private int maxContentLength = 2000;

    /** 操作人 ID 请求头名称（默认实现使用）. */
    private String operatorIdHeader = "X-User-Id";

    /** 操作人名称请求头名称（默认实现使用）. */
    private String operatorNameHeader = "X-User-Name";

    /** 异步线程池配置. */
    private Executor executor = new Executor();

    @Data
    public static class Executor {

        /** 核心线程数. */
        private int corePoolSize = 2;

        /** 最大线程数. */
        private int maxPoolSize = 4;

        /** 队列容量. */
        private int queueCapacity = 1000;

        /** 线程名前缀. */
        private String threadNamePrefix = "audit-log-";
    }
}
