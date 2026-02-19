/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.perm.local;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.kuma.boot.common.model.BaseSecurityUser;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.perm.NestedPermission;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.perm.Permission;
import java.util.Optional;

/** 忽略数据上下文 */
public class DataPermContextHolder {
    private static final ThreadLocal<Permission> PERMISSION_LOCAL =
            new TransmittableThreadLocal<>();
    private static final ThreadLocal<NestedPermission> NESTED_PERMISSION_LOCAL =
            new TransmittableThreadLocal<>();
    private static final ThreadLocal<BaseSecurityUser> USER_DETAIL_LOCAL =
            new TransmittableThreadLocal<>();

    /** 设置 数据权限控制注解 */
    public static void putPermission(Permission permission) {
        PERMISSION_LOCAL.set(permission);
    }

    /** 获取 数据权限控制注解 */
    public static Permission getPermission() {
        return PERMISSION_LOCAL.get();
    }

    /** 设置 数据权限控制注解 */
    public static void putNestedPermission(NestedPermission nestedPermission) {
        NESTED_PERMISSION_LOCAL.set(nestedPermission);
    }

    /** 获取 数据权限控制注解 */
    public static NestedPermission getNestedPermission() {
        return NESTED_PERMISSION_LOCAL.get();
    }

    /** 设置 用户缓存 */
    public static void putUserDetail(BaseSecurityUser dataPerm) {
        USER_DETAIL_LOCAL.set(dataPerm);
    }

    /** 获取 用户缓存 */
    public static Optional<BaseSecurityUser> getUserDetail() {
        return Optional.ofNullable(USER_DETAIL_LOCAL.get());
    }

    /** 清除线程变量(数据权限控制和用户信息) */
    public static void clearUserAndPermission() {
        USER_DETAIL_LOCAL.remove();
        PERMISSION_LOCAL.remove();
    }

    /** 清除线程变量(嵌套权限注解) */
    public static void clearNestedPermission() {
        NESTED_PERMISSION_LOCAL.remove();
    }
}
