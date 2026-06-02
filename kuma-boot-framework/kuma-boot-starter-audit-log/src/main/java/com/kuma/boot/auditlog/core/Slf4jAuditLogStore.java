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

import com.alibaba.fastjson2.JSON;
import com.kuma.boot.auditlog.model.AuditLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认审计存储：输出到独立的 {@code AUDIT} logger.
 *
 * <p>使用专用 logger 名称 {@code com.kuma.boot.auditlog.AUDIT}，便于在 logback 中
 * 将审计日志单独路由到独立文件 / Kafka，与业务日志区分。
 *
 * @author kuma
 */
public class Slf4jAuditLogStore implements AuditLogStore {

    private static final Logger AUDIT = LoggerFactory.getLogger("com.kuma.boot.auditlog.AUDIT");

    @Override
    public void save(AuditLogEntry entry) {
        AUDIT.info("{}", JSON.toJSONString(entry));
    }
}
