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

/**
 * 操作人解析 SPI —— 回答审计中的"谁".
 *
 * <p>默认实现 {@link DefaultAuditOperatorProvider} 从请求头读取操作人；接入 Spring Security
 * 的应用应注册自定义 Bean，从 {@code SecurityContext} 中取当前登录用户。
 *
 * @author kuma
 */
public interface AuditOperatorProvider {

    /**
     * @return 当前操作人 ID（无则返回 null）
     */
    String currentOperatorId();

    /**
     * @return 当前操作人名称（无则返回 null）
     */
    String currentOperatorName();
}
