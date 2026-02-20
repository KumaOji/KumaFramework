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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.enums;

/**
 * 数据范围枚举类
 *
 * <p>用于实现数据级别的权限
 */
public enum DataScopeEnum {

    /** 全部数据权限 */
    ALL(1),

    /** 指定部门数据权限 */
    DEPT_CUSTOM(2),

    /** 部门数据权限 */
    DEPT_ONLY(3),

    /** 部门及以下数据权限 */
    DEPT_AND_CHILD(4),

    /** 仅本人数据权限 */
    SELF(5);

    /** 范围 */
    private final Integer scope;

    DataScopeEnum(Integer scope) {
        this.scope = scope;
    }

    public Integer getScope() {
        return scope;
    }
}
