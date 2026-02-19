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

package com.kuma.boot.data.mybatis.interceptor.tenant.starter;

import com.alibaba.druid.DbType;
import com.alibaba.druid.util.JdbcConstants;
import com.kuma.boot.data.mybatis.interceptor.tenant.service.ITenantService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 多租户参数配置
 *
 * @author wency_cai
 */
@ConfigurationProperties(prefix = TenantProperties.PREFIX)
public class TenantProperties {

    public static final String PREFIX = "mybatis.tenant.config";

    /**
     * 数据库中租户ID的列名
     */
    private String tenantIdColumn = "tenant_id";

    /**
     * 是否忽略表按租户ID过滤,默认所有表都按租户ID过滤，指定表名称(区分大小写全等判断)
     */
    private List<String> ignoreTableName = new ArrayList<>();

    /**
     * 匹配判断指定表别名是否忽略表按租户ID过滤(区分大小写匹配判断)
     */
    private List<String> ignoreMatchTableAlias = new ArrayList<>();

    /**
     * 忽略表名前缀
     */
    private List<String> ignoreTableNamePrefix = new ArrayList<>();

    /**
     * 忽略数据-多数据源情况下指定需要忽略的数据源
     * 需要重写 {@link ITenantService#ignoreDynamicDatasource} 方法，提供获取当前执行的数据源名称
     */
    private List<String> ignoreDynamicDatasource = new ArrayList<>();

    /**
     * 是否使用MyBatis拦截器方式修改sql
     */
    private boolean enable;

    /**
     * 数据库方言
     */
    private DbType dbType = JdbcConstants.MYSQL;

    public String getTenantIdColumn() {
        return tenantIdColumn;
    }

    public void setTenantIdColumn(String tenantIdColumn) {
        this.tenantIdColumn = tenantIdColumn;
    }

    public List<String> getIgnoreTableName() {
        return ignoreTableName;
    }

    public void setIgnoreTableName(List<String> ignoreTableName) {
        this.ignoreTableName = ignoreTableName;
    }

    public List<String> getIgnoreMatchTableAlias() {
        return ignoreMatchTableAlias;
    }

    public void setIgnoreMatchTableAlias(List<String> ignoreMatchTableAlias) {
        this.ignoreMatchTableAlias = ignoreMatchTableAlias;
    }

    public List<String> getIgnoreTableNamePrefix() {
        return ignoreTableNamePrefix;
    }

    public void setIgnoreTableNamePrefix(List<String> ignoreTableNamePrefix) {
        this.ignoreTableNamePrefix = ignoreTableNamePrefix;
    }

    public List<String> getIgnoreDynamicDatasource() {
        return ignoreDynamicDatasource;
    }

    public void setIgnoreDynamicDatasource(List<String> ignoreDynamicDatasource) {
        this.ignoreDynamicDatasource = ignoreDynamicDatasource;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }
}
