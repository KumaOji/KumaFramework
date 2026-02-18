/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

import java.util.function.Supplier;

/**
 * Schema 上下文
 * 用于在同一数据源下动态切换 schema（MySQL 数据库 / PostgreSQL schema）
 * 使用 ThreadLocal 存储，线程安全
 *
 * @author kuma
 */
public final class SchemaContext {

    private static final ThreadLocal<String> SCHEMA_HOLDER = new ThreadLocal<>();

    private SchemaContext() {
    }

    /**
     * 设置当前线程的 schema
     */
    public static void setSchema(String schema) {
        if (schema != null && !schema.isBlank()) {
            SCHEMA_HOLDER.set(schema.trim());
        } else {
            clearSchema();
        }
    }

    /**
     * 获取当前线程的 schema
     */
    public static String getSchema() {
        return SCHEMA_HOLDER.get();
    }

    /**
     * 清除当前线程的 schema
     */
    public static void clearSchema() {
        SCHEMA_HOLDER.remove();
    }

    /**
     * 是否已设置 schema
     */
    public static boolean hasSchema() {
        return SCHEMA_HOLDER.get() != null;
    }

    /**
     * 在指定 schema 下执行代码，执行完后自动恢复
     */
    public static void withSchema(String schema, Runnable runnable) {
        String old = getSchema();
        try {
            setSchema(schema);
            runnable.run();
        } finally {
            if (old != null) {
                setSchema(old);
            } else {
                clearSchema();
            }
        }
    }

    /**
     * 在指定 schema 下执行代码并返回结果，执行完后自动恢复
     */
    public static <T> T withSchema(String schema, Supplier<T> supplier) {
        String old = getSchema();
        try {
            setSchema(schema);
            return supplier.get();
        } finally {
            if (old != null) {
                setSchema(old);
            } else {
                clearSchema();
            }
        }
    }
}
