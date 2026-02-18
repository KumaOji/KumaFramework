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

package com.kuma.boot.common.utils.sql;

import cn.hutool.core.util.ObjUtil;

/**
 * Sql拼接
 *
 * @author kuma
 * @version 2022.06
 * @since 2022-07-30 10:02:23
 */
public class Sql {

    private StringBuffer sql = new StringBuffer();

    private Sql(CharSequence initialSql) {
        sql.append(initialSql);
    }

    /**
     * Sql拼接
     * @return Sql拼接
     */
    public static Sql sql() {
        return new Sql("");
    }

    /**
     * Sql拼接
     * @param appendSql 需要拼接的Sql字符串
     * @return Sql拼接
     */
    public static Sql sql(CharSequence appendSql) {
        return new Sql(appendSql);
    }

    /**
     * 拼接Sql
     * @param originalSql 原始Sql
     * @param appendSql 需要追加的Sql字符串
     * @return 拼接的Sql
     */
    public static StringBuffer append(StringBuffer originalSql, CharSequence appendSql) {
        return originalSql.append(appendSql);
    }

    /**
     * 拼接Sql
     * @param originalSql 原始Sql
     * @param appendSql 需要追加的Sql字符串
     * @param expression 条件表达式（true拼接，false不拼接）
     * @return 拼接的Sql
     */
    public static StringBuffer append(
            StringBuffer originalSql, CharSequence appendSql, boolean expression) {
        if (expression) {
            return originalSql.append(appendSql);
        }

        return originalSql;
    }

    /**
     * 拼接Sql
     * @param originalSql 原始Sql
     * @param appendSql 需要追加的Sql字符串
     * @param isNotEmpty 判空对象，支持：CharSequence、Map、Iterable、Iterator、Array
     * @return 拼接的Sql
     */
    public static StringBuffer append(
            StringBuffer originalSql, CharSequence appendSql, Object isNotEmpty) {
        return append(originalSql, appendSql, ObjUtil.isNotEmpty(isNotEmpty));
    }

    /**
     * 拼接Sql
     * @param appendSql 需要追加的Sql字符串
     * @return Sql拼接
     */
    public Sql append(CharSequence appendSql) {
        sql.append(appendSql);
        return this;
    }

    /**
     * 拼接Sql
     * @param appendSql 需要追加的Sql字符串
     * @param expression 条件表达式（true拼接，false不拼接）
     * @return Sql拼接
     */
    public Sql append(CharSequence appendSql, boolean expression) {
        if (expression) {
            return append(appendSql);
        }

        return this;
    }

    /**
     * 拼接Sql
     * @param appendSql 需要追加的Sql字符串
     * @param isNotEmpty 判空对象，支持：CharSequence、Map、Iterable、Iterator、Array
     * @return Sql拼接
     */
    public Sql append(CharSequence appendSql, Object isNotEmpty) {
        return append(appendSql, ObjUtil.isNotEmpty(isNotEmpty));
    }

    /** 获得Sql */
    public StringBuffer getSql() {
        return sql;
    }

    /** 获得Sql字符串 */
    public String getSqlString() {
        return sql.toString();
    }

    /** 获得Sql字符串 */
    @Override
    public String toString() {
        return getSqlString();
    }
}
