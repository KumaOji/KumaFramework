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

package com.kuma.boot.auditlog.model;

import com.kuma.boot.auditlog.enums.OperateType;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 一条操作审计记录：谁（operator）在何时（operateTime）对什么（module/bizId）做了什么（type/description）.
 *
 * @author kuma
 */
@Data
public class AuditLogEntry {

    /** 链路追踪 ID. */
    private String traceId;

    /** 操作人 ID. */
    private String operatorId;

    /** 操作人名称. */
    private String operatorName;

    /** 操作类型. */
    private OperateType type;

    /** 业务模块. */
    private String module;

    /** 操作描述（SpEL 求值后的结果）. */
    private String description;

    /** 业务主键（SpEL 求值后的结果）. */
    private String bizId;

    /** 目标方法：全限定类名#方法名. */
    private String method;

    /** 方法入参（JSON）. */
    private String params;

    /** 方法返回值（JSON）. */
    private String result;

    /** 是否成功. */
    private boolean success;

    /** 失败时的异常信息. */
    private String errorMsg;

    /** 客户端 IP. */
    private String clientIp;

    /** User-Agent. */
    private String userAgent;

    /** 请求 URI. */
    private String requestUri;

    /** HTTP 方法. */
    private String httpMethod;

    /** 操作时间. */
    private LocalDateTime operateTime;

    /** 耗时（毫秒）. */
    private long costMillis;
}
