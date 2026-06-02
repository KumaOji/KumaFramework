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

import com.kuma.boot.auditlog.model.AuditLogEntry;

/**
 * 审计日志存储 SPI.
 *
 * <p>默认实现 {@link Slf4jAuditLogStore} 将审计记录输出到独立 logger；
 * 业务方可注册自定义 Bean（落库 / 推 MQ / 写 ES）覆盖默认实现。
 *
 * @author kuma
 */
public interface AuditLogStore {

    /**
     * 保存一条审计记录.
     *
     * @param entry 审计记录
     */
    void save(AuditLogEntry entry);
}
