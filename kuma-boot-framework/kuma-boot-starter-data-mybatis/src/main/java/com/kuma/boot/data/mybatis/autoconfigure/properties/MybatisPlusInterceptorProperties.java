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

/**
 * MybatisPlusAutoFillProperties
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-04 07:44:25
 */
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
     * 鍒嗛〉鎻掍欢
     */
    private Pagination pagination;

    /**
     * 闃插叏琛ㄦ洿鏂颁笌鍒犻櫎鎻掍欢
     */
    private BlockAttack blockAttack;

    /**
     * 闈炴硶sql鎻掍欢
     */
    private IllegalSql illegalSql;

    /**
     * 涔愯閿佹彃浠?
     */
    private OptimisticLocker optimisticLocker;

    /**
     * 鏁版嵁鏉冮檺鎻掍欢
     */
    private DataScope dataScope;

    /**
     * 澶氱鎴锋彃浠?
     */
    private MultiTenant multiTenant;

    /**
     * 鏁版嵁鎿嶄綔鎻掍欢
     */
    private DataOperate dataOperate;

    /**
     * 鏁版嵁鍙樻洿鎻掍欢
     */
    private DataChange dataChange;

    /**
     * 鎱㈡煡璇㈡彃浠?
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
         * 婧㈠嚭鎬婚〉鏁板悗鏄惁杩涜澶勭悊
         */
        private boolean overflow = true;

        /**
         * 鍗曢〉鍒嗛〉鏉℃暟闄愬埗
         */
        protected Long maxLimit = 1000L;

        /**
         * 鏁版嵁搴撶被鍨?
         */
        private DbType dbType = DbType.MYSQL;

        /**
         * 鏂硅█瀹炵幇绫?
         */
        private IDialect dialect;

        /**
         * 鐢熸垚 countSql 浼樺寲鎺?join
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
         * 鏄惁寮€鍚绉熸埛
         */
        private Boolean enabled = false;

        /**
         * 闇€瑕佹帓闄ょ殑澶氱鎴风殑琛?
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
         * 澶氱鎴峰瓧娈靛悕绉?
         */
        private String column = "tenant_id";

        /**
         * 鎺掗櫎涓嶈繘琛岀鎴烽殧绂荤殑sql 鏍蜂緥鍏ㄨ矾寰勶細vip.mate.system.mapper.UserMapper.findList
         */
        private List<String> ignoreSqls = new ArrayList<>();

        private MultiTenantType multiTenantType = MultiTenantType.COLUMN;

        /**
         * SCHEMA 妯″紡涓撶敤
         */
        private String owner = "";

        /**
         * 褰撳墠鏈嶅姟鐨勭鎴峰簱鍓嶇紑 浠匰CHEMA妯″紡浣跨敤
         */
        private String tenantDatabasePrefix = "lamp_base";

        /**
         * SCHEMA 妯″紡涓撶敤
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
         * 瀛樺偍鏂瑰紡, 榛樿涓烘墦鍗?
         */
        private StoreType[] types = new StoreType[]{StoreType.LOGGER};

        /**
         * 瀛樺偍绫诲瀷
         */
        public enum StoreType {
            /**
             * 鏁版嵁搴?
             */
            REDIS,
            /**
             * 鎵撳嵃
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
         * 鏄惁寮€鍚參鏌ヨ鐩戞帶
         */
        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }

        /**
         * 鏈€澶ч槇鍊?
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
