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

package com.kuma.boot.tenant.properties;

import com.baomidou.mybatisplus.annotation.DbType;
import com.kuma.boot.tenant.core.TenantIsolationMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 多租户配置属性
 *
 * <p>配置前缀：{@value PREFIX}
 *
 * <p>示例（字段模式）：
 *
 * <pre>{@code
 * kuma:
 *   boot:
 *     tenant:
 *       enabled: true
 *       mode: COLUMN
 *       column-name: tenant_id
 *       ignore-tables:
 *         - sys_config
 *         - sys_dict
 * }</pre>
 *
 * <p>示例（Schema 模式）：
 *
 * <pre>{@code
 * kuma:
 *   boot:
 *     tenant:
 *       enabled: true
 *       mode: SCHEMA
 *       schema-database-prefix: saas_db
 *       db-type: MYSQL
 * }</pre>
 *
 * @author kuma
 * @version 2026.01
 * @since 2026-01-01
 */
@ConfigurationProperties(prefix = TenantProperties.PREFIX)
public class TenantProperties {

    public static final String PREFIX = "kuma.boot.tenant";

    /** 是否启用多租户 */
    private boolean enabled = false;

    /** 隔离模式，默认字段隔离 */
    private TenantIsolationMode mode = TenantIsolationMode.COLUMN;

    // ── COLUMN 模式 ──────────────────────────────────────────────────────────

    /** 租户 ID 字段名（COLUMN / SCHEMA_COLUMN / DATASOURCE_COLUMN 模式生效） */
    private String columnName = "tenant_id";

    /**
     * 不参与租户过滤的表名列表（精确匹配）
     *
     * <p>公共基础表（如字典、菜单、客户端）通常无需隔离，在此配置后 SQL 不会追加租户条件。
     */
    private List<String> ignoreTables = new ArrayList<>();

    /**
     * 不参与租户过滤的 MappedStatement ID 列表
     *
     * <p>格式：全限定 Mapper 方法名，例如 {@code com.example.mapper.UserMapper.findAll}。
     */
    private List<String> ignoreSqls = new ArrayList<>();

    // ── SCHEMA 模式 ──────────────────────────────────────────────────────────

    /**
     * Schema 数据库前缀（SCHEMA / SCHEMA_COLUMN 模式生效）
     *
     * <p>最终 Schema 名为 {@code {schemaDatabasePrefix}_{tenantId}}，例如 {@code saas_db_1001}。
     */
    private String schemaDatabasePrefix = "saas_db";

    /** Schema 模式下额外的 owner（仅 Oracle/PostgreSQL 等需要指定 schema owner 时使用） */
    private String schemaOwner = "";

    /** Schema 模式对应的数据库类型，用于 SQL 改写（默认 MySQL） */
    private DbType dbType = DbType.MYSQL;

    // ── Web 层 ───────────────────────────────────────────────────────────────

    /** 是否启用 HTTP 请求过滤器，从 Header 中自动提取租户 ID */
    private boolean enableWebFilter = true;

    /**
     * HTTP Header 名称，用于传递租户 ID
     *
     * <p>客户端请求时在请求头中携带：{@code X-Tenant-Id: 1001}。
     */
    private String headerName = "X-Tenant-Id";

    // ── Getters / Setters ────────────────────────────────────────────────────

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public TenantIsolationMode getMode() {
        return mode;
    }

    public void setMode(TenantIsolationMode mode) {
        this.mode = mode;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public List<String> getIgnoreTables() {
        return ignoreTables;
    }

    public void setIgnoreTables(List<String> ignoreTables) {
        this.ignoreTables = ignoreTables;
    }

    public List<String> getIgnoreSqls() {
        return ignoreSqls;
    }

    public void setIgnoreSqls(List<String> ignoreSqls) {
        this.ignoreSqls = ignoreSqls;
    }

    public String getSchemaDatabasePrefix() {
        return schemaDatabasePrefix;
    }

    public void setSchemaDatabasePrefix(String schemaDatabasePrefix) {
        this.schemaDatabasePrefix = schemaDatabasePrefix;
    }

    public String getSchemaOwner() {
        return schemaOwner;
    }

    public void setSchemaOwner(String schemaOwner) {
        this.schemaOwner = schemaOwner;
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public boolean isEnableWebFilter() {
        return enableWebFilter;
    }

    public void setEnableWebFilter(boolean enableWebFilter) {
        this.enableWebFilter = enableWebFilter;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }
}
