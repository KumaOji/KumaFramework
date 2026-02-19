/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.druid.DbType
 */
package com.kuma.boot.data.mybatis.interceptor.tenant.handler;

import com.alibaba.druid.DbType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface TenantInfoHandler {
    public static final Set<String> IGNORE_TENANT_ID_METHODS = new HashSet<String>();

    public DbType getDbType();

    public <T> List<T> getTenantIds();

    public List<String> ignoreTableName();

    public List<String> ignoreMatchTableAlias();

    public List<String> ignoreTableNamePrefix();

    public String getTenantIdColumn();

    public List<String> ignoreDynamicDatasource();
}

