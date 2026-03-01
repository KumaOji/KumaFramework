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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.perm.code;

import com.kuma.boot.common.exception.BusinessException;
import java.util.Arrays;

/** 数据范围权限类型 */
public enum DataScopeEnum {
    /** 自己的数据 */
    SELF(1),
    /** 指定用户级别 */
    USER_SCOPE(2),
    /** 指定部门级别 */
    DEPT_SCOPE(3),
    /** 指定部门与用户级别 */
    DEPT_AND_USER_SCOPE(4),
    /** 所在部门 (部门管理者默认拥有) */
    BELONG_DEPT_SCOPE(5),
    /** 所在及下级部门 */
    BELONG_DEPT_AND_SUB_SCOPE(6),
    /** 组织数据 (组织管理员默认拥有) */
    ORG_SCOPE(7),
    /** 所有组织数据 (组织超级管理员默认拥有) */
    ALL_ORG_SCOPE(8),
    /** 全部数据 直接可以看数据库的(程序员级别) */
    ALL_SCOPE(9);

    private final int code;

    DataScopeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /** 根据数字编号获取 */
    public static DataScopeEnum findByCode(int code) {
        return Arrays.stream(DataScopeEnum.values())
                .filter(e -> e.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new BusinessException("不支持的数据权限类型"));
    }
}
