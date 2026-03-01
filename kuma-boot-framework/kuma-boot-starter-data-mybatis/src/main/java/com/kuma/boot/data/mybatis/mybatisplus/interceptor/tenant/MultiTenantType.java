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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.tenant;

/**
 * 多租户类型
 *
 * <p>NONE、COLUMN、SCHEMA 模式开源
 *
 * <p>DATASOURCE 模式收费，购买咨询作者
 */
public enum MultiTenantType {
    /** 非租户模式 */
    NONE("非租户模式"),
    /** 字段模式 在sql中拼接 tenant_code 字段 */
    COLUMN("字段模式"),
    /**
     * 独立schema模式 在sql中拼接 数据库 schema
     *
     * <p>该模式暂不支持复杂sql、存储过程、函数等，欢迎大家提供解决方案。
     */
    SCHEMA("独立schema模式"),
    /**
     * 独立数据源模式
     *
     * <p>该模式不开源，购买咨询作者。
     */
    DATASOURCE("独立数据源模式"),
    /**
     * 数据源 + 字段 混合模式
     *
     * <p>该模式不开源，购买咨询作者。
     */
    DATASOURCE_COLUMN("数据源&字段混合模式"),
    /** SCHEMA + 字段 混合模式 */
    SCHEMA_COLUMN("SCHEMA&字段混合模式"),
    ;
    private String describe;

    public String getDescribe() {
        return describe;
    }

    MultiTenantType(String describe) {
        this.describe = describe;
    }

    public boolean eq(String val) {
        return this.name().equalsIgnoreCase(val);
    }

    public boolean eq(MultiTenantType val) {
        if (val == null) {
            return false;
        }
        return eq(val.name());
    }
}
