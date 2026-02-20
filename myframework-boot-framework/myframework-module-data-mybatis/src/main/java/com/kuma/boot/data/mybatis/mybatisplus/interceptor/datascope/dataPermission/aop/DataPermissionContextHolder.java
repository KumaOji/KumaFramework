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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.aop;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.annotation.DataPermission;
import java.util.LinkedList;
import java.util.List;

/** 数据权限注解上下文 */
public class DataPermissionContextHolder {

    /** ttl解决父子线程传值问题 由于存在方法的嵌套调用 所以使用List */
    private static final ThreadLocal<LinkedList<DataPermission>> DATA_PERMISSIONS =
            TransmittableThreadLocal.withInitial(LinkedList::new);

    /**
     * 获得当前的数据权限注解
     *
     * @return 数据权限 注解
     */
    public static DataPermission get() {
        return DATA_PERMISSIONS.get().peekLast();
    }

    /**
     * 入栈 数据权限 注解
     *
     * @param dataPermission 数据权限注解
     */
    public static void add(DataPermission dataPermission) {
        DATA_PERMISSIONS.get().addLast(dataPermission);
    }

    /**
     * 出栈 数据权限 注解
     */
    public static void remove() {
        DATA_PERMISSIONS.get().removeLast();
        // 无元素时，清空 ThreadLocal
        if (DATA_PERMISSIONS.get().isEmpty()) {
            DATA_PERMISSIONS.remove();
        }
    }

    /**
     * 获得所有 数据权限
     *
     * @return 数据权限 队列
     */
    public static List<DataPermission> getAll() {
        return DATA_PERMISSIONS.get();
    }
}
