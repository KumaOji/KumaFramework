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

package com.kuma.boot.data.mybatis.autoconfigure;

import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.IllegalSQLInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.MpInterceptor;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.interceptor.DataChangeRecorderInnerInterceptor;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.DataScopeInterceptor;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.pagination.SqlPaginationInnerInterceptor;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.tenant.MultiTenantLineHandler;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.tenant.MultiTenantLineInnerInterceptor;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.tenant.MultiTenantType;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.tenant.SchemaInterceptor;
import com.kuma.boot.data.mybatis.autoconfigure.properties.MybatisPlusInterceptorProperties;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 插件容器
 */
@AutoConfiguration
@ConditionalOnProperty(
        prefix = MybatisPlusInterceptorProperties.PREFIX,
        name = "enabled",
        havingValue = "true")
@EnableConfigurationProperties({MybatisPlusInterceptorProperties.class})
public class MybatisPlusInterceptorAutoConfiguration implements InitializingBean {

    private final MybatisPlusInterceptorProperties interceptorProperties;

    public MybatisPlusInterceptorAutoConfiguration(
            MybatisPlusInterceptorProperties interceptorProperties) {
        this.interceptorProperties = interceptorProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(MybatisPlusInterceptorAutoConfiguration.class, StarterNameConstants.DATA_MYBATIS_STARTER);
    }

    /**
     * 分页插件
     */
    @Bean
    @ConditionalOnProperty(
            prefix = MybatisPlusInterceptorProperties.PAGINATION_PREFIX,
            name = "enabled",
            havingValue = "true")
    public MpInterceptor paginationInnerInterceptor() {
        MybatisPlusInterceptorProperties.Pagination paginationProperties =
                interceptorProperties.getPagination();
        SqlPaginationInnerInterceptor paginationInterceptor = new SqlPaginationInnerInterceptor();
        // 单页分页条数限制
        paginationInterceptor.setMaxLimit(paginationProperties.getMaxLimit());
        // 数据库类型
        // if (databaseProperties.getDbType() != null) {
        // 	paginationInterceptor.setDbType(DbType.MYSQL);
        // }
        paginationInterceptor.setDbType(paginationProperties.getDbType());
        // 溢出总页数后是否进行处理
        paginationInterceptor.setOverflow(paginationProperties.isOverflow());
        // 生成 countSql 优化掉 join 现在只支持 left join
        paginationInterceptor.setOptimizeJoin(paginationProperties.isOptimizeJoin());
        return new MpInterceptor(paginationInterceptor, 199);
    }

    /**
     * 防止全表更新与删除插件
     */
    @Bean
    @ConditionalOnProperty(
            prefix = MybatisPlusInterceptorProperties.BLOCK_ATTACK_PREFIX,
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true)
    public MpInterceptor blockAttackInnerInterceptor() {
        return new MpInterceptor(new BlockAttackInnerInterceptor(), 196);
    }

    /**
     * sql规范插件
     */
    @Bean
    @ConditionalOnProperty(
            prefix = MybatisPlusInterceptorProperties.ILLEGAL_SQL_PREFIX,
            name = "enabled",
            havingValue = "true")
    public MpInterceptor illegalSqlInnerInterceptor() {
        return new MpInterceptor(new IllegalSQLInnerInterceptor(), 197);
    }

    /**
     * 乐观锁插件
     */
    @Bean
    @ConditionalOnProperty(
            prefix = MybatisPlusInterceptorProperties.OPTIMISTIC_LOCKER_PREFIX,
            name = "enabled",
            havingValue = "true")
    public MpInterceptor optimisticLockerInnerInterceptor() {
        return new MpInterceptor(new OptimisticLockerInnerInterceptor(), 198);
    }

    /**
     * 数据权限插件
     */
    @Bean
    @ConditionalOnProperty(
            prefix = MybatisPlusInterceptorProperties.DATA_SCOPE_PREFIX,
            name = "enabled",
            havingValue = "true")
    public MpInterceptor dataScopeInterceptor() {
        return new MpInterceptor(new DataScopeInterceptor(), 200);
    }

    /**
     * 多租户插件
     */
    @Bean
    @ConditionalOnProperty(
            prefix = MybatisPlusInterceptorProperties.MULTI_TENANT_LINE_PREFIX,
            name = "enabled",
            havingValue = "true")
    public MpInterceptor multiTenantLineInnerInterceptor() {
        MybatisPlusInterceptorProperties.MultiTenant tenantProperties =
                interceptorProperties.getMultiTenant();
        LogUtils.info(
                "检测到 kuma.cloud.data.database.multiTenantType={}，已启用 {} 模式",
                tenantProperties.getMultiTenantType().name(),
                tenantProperties.getMultiTenantType().getDescribe());
        InnerInterceptor innerInterceptor = null;
        if (StrUtil.equalsAny(
                tenantProperties.getMultiTenantType().name(),
                MultiTenantType.SCHEMA.name(),
                MultiTenantType.SCHEMA_COLUMN.name())) {
            // ArgumentAssert.notNull(databaseProperties.getDbType(), "SCHEMA
            // 模式请在mysql.yml、oracle.yml、sqlserver.yml中配置:
            // {}.dbType", DatabaseProperties.PREFIX);

            // SCHEMA 动态表名插件
            innerInterceptor = new SchemaInterceptor(tenantProperties);
        }
        if (StrUtil.equalsAny(
                tenantProperties.getMultiTenantType().name(),
                MultiTenantType.COLUMN.name(),
                MultiTenantType.SCHEMA_COLUMN.name(),
                MultiTenantType.DATASOURCE_COLUMN.name())) {

            // COLUMN 模式 多租户插件
            MultiTenantLineHandler multiTenantLineHandler =
                    new MultiTenantLineHandler(tenantProperties);
            innerInterceptor =
                    new MultiTenantLineInnerInterceptor(multiTenantLineHandler, tenantProperties);
        }

        return new MpInterceptor(innerInterceptor, 201);
    }

    /**
     * 数据变更插件
     */
    @Bean
    @ConditionalOnProperty(
            prefix = MybatisPlusInterceptorProperties.DATA_CHANGE_PREFIX,
            name = "enabled",
            havingValue = "true")
    public MpInterceptor dataChangeRecorderInnerInterceptor() {
        return new MpInterceptor(new DataChangeRecorderInnerInterceptor(), 195);
    }
}
