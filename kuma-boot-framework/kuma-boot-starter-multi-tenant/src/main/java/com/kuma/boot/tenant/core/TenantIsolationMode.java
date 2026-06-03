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

package com.kuma.boot.tenant.core;

/**
 * 多租户隔离模式
 *
 * <ul>
 *   <li>{@link #COLUMN}：字段隔离 —— 所有表共享，通过 {@code tenant_id} 列区分数据，成本最低。
 *   <li>{@link #SCHEMA}：库/Schema 隔离 —— 每个租户独立 Schema，数据物理隔离，安全性高。
 *   <li>{@link #DATASOURCE}：数据源隔离 —— 每个租户独立数据源，最高隔离性，需配合动态数据源使用。
 *   <li>{@link #SCHEMA_COLUMN}：Schema + 字段混合隔离。
 *   <li>{@link #DATASOURCE_COLUMN}：数据源 + 字段混合隔离（预留）。
 * </ul>
 *
 * @author kuma
 * @version 2026.01
 * @since 2026-01-01
 */
public enum TenantIsolationMode {

    /** 字段隔离：在 SQL 中自动拼接 tenant_id 条件 */
    COLUMN("字段隔离"),

    /** Schema 隔离：在 SQL 中自动替换 schema 前缀 */
    SCHEMA("Schema 隔离"),

    /**
     * 数据源隔离：根据租户 ID 路由到独立数据源
     *
     * <p>需配合 {@code kuma-boot-starter-data-datasource} 动态数据源使用。
     */
    DATASOURCE("数据源隔离"),

    /** Schema + 字段 混合隔离 */
    SCHEMA_COLUMN("Schema&字段混合隔离"),

    /** 数据源 + 字段 混合隔离（预留） */
    DATASOURCE_COLUMN("数据源&字段混合隔离");

    private final String describe;

    TenantIsolationMode(String describe) {
        this.describe = describe;
    }

    public String getDescribe() {
        return describe;
    }
}
