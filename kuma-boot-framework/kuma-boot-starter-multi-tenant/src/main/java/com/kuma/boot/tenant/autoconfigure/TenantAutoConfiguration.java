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

package com.kuma.boot.tenant.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.MpInterceptor;
import com.kuma.boot.tenant.core.DefaultTenantIdProvider;
import com.kuma.boot.tenant.core.TenantIdProvider;
import com.kuma.boot.tenant.core.TenantIsolationMode;
import com.kuma.boot.tenant.core.column.ColumnTenantInnerInterceptor;
import com.kuma.boot.tenant.core.column.ColumnTenantLineHandler;
import com.kuma.boot.tenant.core.schema.SchemaTenantInnerInterceptor;
import com.kuma.boot.tenant.properties.TenantProperties;
import com.kuma.boot.tenant.web.TenantContextFilter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.Ordered;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 多租户自动配置
 *
 * <p>生效条件：{@code kuma.boot.tenant.enabled=true}。
 *
 * <p>根据 {@code kuma.boot.tenant.mode} 自动注册对应的 MybatisPlus 内部拦截器：
 *
 * <ul>
 *   <li>{@code COLUMN}：注册 {@link ColumnTenantInnerInterceptor}，向 SQL 注入 tenant_id 条件。
 *   <li>{@code SCHEMA}：注册 {@link SchemaTenantInnerInterceptor}，改写 SQL 的 Schema 前缀。
 *   <li>{@code SCHEMA_COLUMN}：同时注册以上两个拦截器。
 *   <li>{@code DATASOURCE}：仅打印提示日志，需配合动态数据源模块实现路由。
 * </ul>
 *
 * <p>所有 {@link MpInterceptor} Bean 将被
 * {@link com.kuma.boot.data.mybatis.autoconfigure.MybatisPlusAutoConfiguration}
 * 收集并装配到 {@code MybatisPlusInterceptor} 中。
 *
 * <p>{@link TenantIdProvider} 和 {@link TenantContextFilter} 均以
 * {@link ConditionalOnMissingBean} 暴露，业务可注册自定义实现覆盖。
 *
 * @author kuma
 * @version 2026.01
 * @since 2026-01-01
 */
@AutoConfiguration
@EnableConfigurationProperties(TenantProperties.class)
@ConditionalOnProperty(prefix = TenantProperties.PREFIX, name = "enabled", havingValue = "true")
public class TenantAutoConfiguration implements InitializingBean {

    private final TenantProperties properties;

    public TenantAutoConfiguration(TenantProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(TenantAutoConfiguration.class, StarterNameConstants.MULTI_TENANT_STARTER);
        LogUtils.info(
                "[MultiTenant] 启动模式: {} ({})",
                properties.getMode().name(),
                properties.getMode().getDescribe());
    }

    // ── SPI 默认实现 ──────────────────────────────────────────────────────────

    @Bean
    @ConditionalOnMissingBean(TenantIdProvider.class)
    public TenantIdProvider tenantIdProvider() {
        return new DefaultTenantIdProvider();
    }

    // ── Web 过滤器 ────────────────────────────────────────────────────────────

    @Bean
    @ConditionalOnMissingBean(TenantContextFilter.class)
    @ConditionalOnProperty(
            prefix = TenantProperties.PREFIX,
            name = "enable-web-filter",
            havingValue = "true",
            matchIfMissing = true)
    public TenantContextFilter tenantContextFilter() {
        return new TenantContextFilter(properties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "tenantContextFilterRegistration")
    @ConditionalOnProperty(
            prefix = TenantProperties.PREFIX,
            name = "enable-web-filter",
            havingValue = "true",
            matchIfMissing = true)
    public FilterRegistrationBean<TenantContextFilter> tenantContextFilterRegistration(
            TenantContextFilter filter) {
        FilterRegistrationBean<TenantContextFilter> registration =
                new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/*");
        // 最高优先级：保证在 Security、业务 Filter 之前设置租户上下文
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    // ── COLUMN 模式拦截器 ─────────────────────────────────────────────────────

    /** 注册字段隔离拦截器（COLUMN / SCHEMA_COLUMN / DATASOURCE_COLUMN 模式均需要） */
    @Bean("tenantColumnMpInterceptor")
    @Conditional(ColumnModeCondition.class)
    public MpInterceptor tenantColumnMpInterceptor(TenantIdProvider tenantIdProvider) {
        ColumnTenantLineHandler handler =
                new ColumnTenantLineHandler(tenantIdProvider, properties);
        ColumnTenantInnerInterceptor interceptor =
                new ColumnTenantInnerInterceptor(handler, properties);
        // 多租户拦截器排在分页（199）之前，优先改写 SQL
        return new MpInterceptor(interceptor, 201);
    }

    // ── SCHEMA 模式拦截器 ─────────────────────────────────────────────────────

    /** 注册 Schema 隔离拦截器（SCHEMA / SCHEMA_COLUMN 模式均需要） */
    @Bean("tenantSchemaMpInterceptor")
    @Conditional(SchemaModeCondition.class)
    public MpInterceptor tenantSchemaMpInterceptor(TenantIdProvider tenantIdProvider) {
        SchemaTenantInnerInterceptor interceptor =
                new SchemaTenantInnerInterceptor(tenantIdProvider, properties);
        // Schema 改写在字段注入（201）之前执行
        return new MpInterceptor(interceptor, 202);
    }

    // ── 自定义 Condition ──────────────────────────────────────────────────────

    /** COLUMN / SCHEMA_COLUMN / DATASOURCE_COLUMN 模式下激活字段拦截器 */
    static class ColumnModeCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String mode =
                    context.getEnvironment()
                            .getProperty(TenantProperties.PREFIX + ".mode", "COLUMN")
                            .toUpperCase();
            return mode.equals(TenantIsolationMode.COLUMN.name())
                    || mode.equals(TenantIsolationMode.SCHEMA_COLUMN.name())
                    || mode.equals(TenantIsolationMode.DATASOURCE_COLUMN.name());
        }
    }

    /** SCHEMA / SCHEMA_COLUMN 模式下激活 Schema 拦截器 */
    static class SchemaModeCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String mode =
                    context.getEnvironment()
                            .getProperty(TenantProperties.PREFIX + ".mode", "COLUMN")
                            .toUpperCase();
            return mode.equals(TenantIsolationMode.SCHEMA.name())
                    || mode.equals(TenantIsolationMode.SCHEMA_COLUMN.name());
        }
    }
}
