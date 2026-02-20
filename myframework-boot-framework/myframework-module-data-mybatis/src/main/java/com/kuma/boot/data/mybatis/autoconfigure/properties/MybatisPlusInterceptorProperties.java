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

package com.kuma.boot.data.mybatis.autoconfigure.properties;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.tenant.MultiTenantType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * MybatisPlusAutoFillProperties
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-04 07:44:25
 */
@RefreshScope
@ConfigurationProperties(prefix = MybatisPlusInterceptorProperties.PREFIX)
public class MybatisPlusInterceptorProperties {

    public static final String PREFIX = "kuma.boot.data.mybatis.mybatis-plus.interceptor";
    public static final String PAGINATION_PREFIX = PREFIX + ".pagination";
    public static final String BLOCK_ATTACK_PREFIX = PREFIX + ".block-attack";
    public static final String ILLEGAL_SQL_PREFIX = PREFIX + ".illegal-sql";
    public static final String OPTIMISTIC_LOCKER_PREFIX = PREFIX + ".optimistic-locker";
    public static final String DATA_SCOPE_PREFIX = PREFIX + ".data-scope";
    public static final String MULTI_TENANT_LINE_PREFIX = PREFIX + ".multi-tenant";
    public static final String DATA_CHANGE_PREFIX = PREFIX + ".data-change";

    private Boolean enabled = false;

    /**
     * 分页插件
     */
    private Pagination pagination;

    /**
     * 防全表更新与删除插件
     */
    private BlockAttack blockAttack;

    /**
     * 非法sql插件
     */
    private IllegalSql illegalSql;

    /**
     * 乐观锁插件
     */
    private OptimisticLocker optimisticLocker;

    /**
     * 数据权限插件
     */
    private DataScope dataScope;

    /**
     * 多租户插件
     */
    private MultiTenant multiTenant;

    /**
     * 数据操作插件
     */
    private DataOperate dataOperate;

    /**
     * 数据变更插件
     */
    private DataChange dataChange;

    /**
     * 慢查询插件
     */
    private SlowQuery slowQuery;

    /**
     * Pagination
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class Pagination {

        private Boolean enabled = false;

        /**
         * 溢出总页数后是否进行处理
         */
        private boolean overflow = true;

        /**
         * 单页分页条数限制
         */
        protected Long maxLimit = 1000L;

        /**
         * 数据库类型
         */
        private DbType dbType = DbType.MYSQL;

        /**
         * 方言实现类
         */
        private IDialect dialect;

        /**
         * 生成 countSql 优化掉 join
         */
        protected boolean optimizeJoin = true;

        public boolean isOverflow() {
            return overflow;
        }

        public void setOverflow( boolean overflow ) {
            this.overflow = overflow;
        }

        public Long getMaxLimit() {
            return maxLimit;
        }

        public void setMaxLimit( Long maxLimit ) {
            this.maxLimit = maxLimit;
        }

        public DbType getDbType() {
            return dbType;
        }

        public void setDbType( DbType dbType ) {
            this.dbType = dbType;
        }

        public IDialect getDialect() {
            return dialect;
        }

        public void setDialect( IDialect dialect ) {
            this.dialect = dialect;
        }

        public boolean isOptimizeJoin() {
            return optimizeJoin;
        }

        public void setOptimizeJoin( boolean optimizeJoin ) {
            this.optimizeJoin = optimizeJoin;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }
    }

    /**
     * BlockAttack
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class BlockAttack {

        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }
    }

    /**
     * IllegalSql
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class IllegalSql {

        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }
    }

    /**
     * OptimisticLocker
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class OptimisticLocker {

        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }
    }

    /**
     * DataScope
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class DataScope {

        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }
    }

    /**
     * MultiTenant
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class MultiTenant {

        /**
         * 是否开启多租户
         */
        private Boolean enabled = false;

        /**
         * 需要排除的多租户的表
         */
        private List<String> ignoreTables =
                Arrays.asList(
                        "tt_sys_menu",
                        "tt_sys_dict",
                        "tt_sys_client",
                        "tt_sys_tenant",
                        "tt_sys_role_permission",
                        "tt_sys_config",
                        "tt_sys_data_source",
                        "tt_sys_attachment");

        /**
         * 多租户字段名称
         */
        private String column = "tenant_id";

        /**
         * 排除不进行租户隔离的sql 样例全路径：vip.mate.system.mapper.UserMapper.findList
         */
        private List<String> ignoreSqls = new ArrayList<>();

        private MultiTenantType multiTenantType = MultiTenantType.COLUMN;

        /**
         * SCHEMA 模式专用
         */
        private String owner = "";

        /**
         * 当前服务的租户库前缀 仅SCHEMA模式使用
         */
        private String tenantDatabasePrefix = "lamp_base";

        /**
         * SCHEMA 模式专用
         */
        private DbType dbType;

        public String getOwner() {
            return owner;
        }

        public void setOwner( String owner ) {
            this.owner = owner;
        }

        public String getTenantDatabasePrefix() {
            return tenantDatabasePrefix;
        }

        public void setTenantDatabasePrefix( String tenantDatabasePrefix ) {
            this.tenantDatabasePrefix = tenantDatabasePrefix;
        }

        public DbType getDbType() {
            return dbType;
        }

        public void setDbType( DbType dbType ) {
            this.dbType = dbType;
        }

        public MultiTenantType getMultiTenantType() {
            return multiTenantType;
        }

        public void setMultiTenantType( MultiTenantType multiTenantType ) {
            this.multiTenantType = multiTenantType;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public List<String> getIgnoreSqls() {
            return ignoreSqls;
        }

        public void setIgnoreSqls( List<String> ignoreSqls ) {
            this.ignoreSqls = ignoreSqls;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }

        public List<String> getIgnoreTables() {
            return ignoreTables;
        }

        public void setIgnoreTables( List<String> ignoreTables ) {
            this.ignoreTables = ignoreTables;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn( String column ) {
            this.column = column;
        }
    }

    /**
     * DataOperate
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class DataOperate {

        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }
    }

    /**
     * DataChange
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class DataChange {

        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }

        /**
         * 存储方式, 默认为打印
         */
        private StoreType[] types = new StoreType[]{StoreType.LOGGER};

        /**
         * 存储类型
         */
        public enum StoreType {
            /**
             * 数据库
             */
            REDIS,
            /**
             * 打印
             */
            LOGGER,
            /**
             * KAFKA
             */
            KAFKA
        }

        public StoreType[] getTypes() {
            return types;
        }

        public void setTypes( StoreType[] types ) {
            this.types = types;
        }
    }

    /**
     * SlowQuery
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class SlowQuery {

        /**
         * 是否开启慢查询监控
         */
        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }

        /**
         * 最大阈值
         */
        private Integer slowSqlThresholdMs = 6000;

        public Integer getSlowSqlThresholdMs() {
            return slowSqlThresholdMs;
        }

        public void setSlowSqlThresholdMs( Integer slowSqlThresholdMs ) {
            this.slowSqlThresholdMs = slowSqlThresholdMs;
        }
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled( Boolean enabled ) {
        this.enabled = enabled;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination( Pagination pagination ) {
        this.pagination = pagination;
    }

    public BlockAttack getBlockAttack() {
        return blockAttack;
    }

    public void setBlockAttack( BlockAttack blockAttack ) {
        this.blockAttack = blockAttack;
    }

    public IllegalSql getIllegalSql() {
        return illegalSql;
    }

    public void setIllegalSql( IllegalSql illegalSql ) {
        this.illegalSql = illegalSql;
    }

    public OptimisticLocker getOptimisticLocker() {
        return optimisticLocker;
    }

    public void setOptimisticLocker( OptimisticLocker optimisticLocker ) {
        this.optimisticLocker = optimisticLocker;
    }

    public DataScope getDataScope() {
        return dataScope;
    }

    public void setDataScope( DataScope dataScope ) {
        this.dataScope = dataScope;
    }

    public MultiTenant getMultiTenant() {
        return multiTenant;
    }

    public void setMultiTenant( MultiTenant multiTenant ) {
        this.multiTenant = multiTenant;
    }

    public DataOperate getDataOperate() {
        return dataOperate;
    }

    public void setDataOperate( DataOperate dataOperate ) {
        this.dataOperate = dataOperate;
    }

    public SlowQuery getSlowQuery() {
        return slowQuery;
    }

    public void setSlowQuery( SlowQuery slowQuery ) {
        this.slowQuery = slowQuery;
    }

    public DataChange getDataChange() {
        return dataChange;
    }

    public void setDataChange( DataChange dataChange ) {
        this.dataChange = dataChange;
    }
}
