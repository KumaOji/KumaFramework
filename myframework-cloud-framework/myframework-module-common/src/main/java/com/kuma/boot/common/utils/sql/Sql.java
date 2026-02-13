/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.ObjUtil
 */
package com.kuma.boot.common.utils.sql;

import cn.hutool.core.util.ObjUtil;

public class Sql {
    private StringBuffer sql = new StringBuffer();

    private Sql(CharSequence initialSql) {
        this.sql.append(initialSql);
    }

    public static Sql sql() {
        return new Sql("");
    }

    public static Sql sql(CharSequence appendSql) {
        return new Sql(appendSql);
    }

    public static StringBuffer append(StringBuffer originalSql, CharSequence appendSql) {
        return originalSql.append(appendSql);
    }

    public static StringBuffer append(StringBuffer originalSql, CharSequence appendSql, boolean expression) {
        if (expression) {
            return originalSql.append(appendSql);
        }
        return originalSql;
    }

    public static StringBuffer append(StringBuffer originalSql, CharSequence appendSql, Object isNotEmpty) {
        return Sql.append(originalSql, appendSql, ObjUtil.isNotEmpty((Object)isNotEmpty));
    }

    public Sql append(CharSequence appendSql) {
        this.sql.append(appendSql);
        return this;
    }

    public Sql append(CharSequence appendSql, boolean expression) {
        if (expression) {
            return this.append(appendSql);
        }
        return this;
    }

    public Sql append(CharSequence appendSql, Object isNotEmpty) {
        return this.append(appendSql, ObjUtil.isNotEmpty((Object)isNotEmpty));
    }

    public StringBuffer getSql() {
        return this.sql;
    }

    public String getSqlString() {
        return this.sql.toString();
    }

    public String toString() {
        return this.getSqlString();
    }
}

