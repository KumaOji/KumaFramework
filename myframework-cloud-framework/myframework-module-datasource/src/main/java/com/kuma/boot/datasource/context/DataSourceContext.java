/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.datasource.context;

/**
 * 数据源切换上下文
 * 配合 dynamic-datasource 的 @DS 注解，也可通过 ThreadLocal 动态指定数据源
 *
 * @author kuma
 */
public final class DataSourceContext {

    private static final ThreadLocal<String> DS_HOLDER = new ThreadLocal<>();

    private DataSourceContext() {
    }

    /**
     * 设置当前线程的数据源 key
     */
    public static void setDataSource(String dataSourceKey) {
        if (dataSourceKey != null && !dataSourceKey.isBlank()) {
            DS_HOLDER.set(dataSourceKey.trim());
        } else {
            clearDataSource();
        }
    }

    /**
     * 获取当前线程的数据源 key
     */
    public static String getDataSource() {
        return DS_HOLDER.get();
    }

    /**
     * 清除当前线程的数据源
     */
    public static void clearDataSource() {
        DS_HOLDER.remove();
    }

    /**
     * 是否已设置数据源
     */
    public static boolean hasDataSource() {
        return DS_HOLDER.get() != null;
    }

    /**
     * 在指定数据源下执行代码
     */
    public static void withDataSource(String dataSourceKey, Runnable runnable) {
        String old = getDataSource();
        try {
            setDataSource(dataSourceKey);
            runnable.run();
        } finally {
            if (old != null) {
                setDataSource(old);
            } else {
                clearDataSource();
            }
        }
    }
}
