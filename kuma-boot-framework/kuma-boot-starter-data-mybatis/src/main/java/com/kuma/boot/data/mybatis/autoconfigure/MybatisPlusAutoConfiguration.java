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

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.mybatisplus.handler.objecthandler.AutoFieldMetaObjectHandler;
import com.kuma.boot.data.mybatis.mybatisplus.handler.typehandler.like.FullLikeTypeHandler;
import com.kuma.boot.data.mybatis.mybatisplus.handler.typehandler.like.LeftLikeTypeHandler;
import com.kuma.boot.data.mybatis.mybatisplus.handler.typehandler.like.RightLikeTypeHandler;
import com.kuma.boot.data.mybatis.mybatisplus.incrementer.SnowFlakeIdGenerator;
import com.kuma.boot.data.mybatis.mybatisplus.injector.MateSqlInjector;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.MpInterceptor;
import com.kuma.boot.data.mybatis.aot.MybatisPlusRuntimeHintsRegistrar;
import com.kuma.boot.data.mybatis.autoconfigure.properties.MybatisPlusAutoFillProperties;
import com.kuma.boot.data.mybatis.autoconfigure.properties.MybatisPlusProperties;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.type.EnumTypeHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MybatisPlusAutoConfiguration
 *
 * <p>本 starter 聚合框架内置 Mapper 包（含延时任务、幂等 DB 等），保证 Spring AOT / Graal Native 在编译期可解析，
 * 且避免仅在组件扫描中出现的 {@code Configuration} + {@code @MapperScan} 导致 {@code MapperFactoryBean} 失效。
 *
 * <p>应用业务 Mapper（如 {@code com.kuma.cloud.*.mapper}）仍须在应用的 {@link org.springframework.boot.autoconfigure.SpringBootApplication}
 * （或等价入口）上使用 {@code @MapperScan} 显式列出包名。
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-04 07:40:02
 */
@MapperScan(
        basePackages = {
            "com.kuma.boot.mybatis.mapper",
            "com.kuma.boot.data.mybatis.delay",
            // Graal Native / Spring AOT：勿把 @MapperScan 放在仅组件扫描载入的 Configuration 上，否则 MapperFactoryBean
            // 的 Class<?> 参数在镜像中装配失败（与 kuma-boot-starter-idempotent 同仓声明）
            "com.kuma.boot.idempotent.idempotentenhance.db.mapper",
        })
@EnableTransactionManagement
@ImportRuntimeHints(MybatisPlusRuntimeHintsRegistrar.class)
@AutoConfiguration(after = {com.kuma.boot.data.mybatis.autoconfigure.MybatisPlusInterceptorAutoConfiguration.class})
@EnableConfigurationProperties({MybatisPlusAutoFillProperties.class, MybatisPlusProperties.class})
@ConditionalOnProperty(
        prefix = MybatisPlusProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class MybatisPlusAutoConfiguration implements InitializingBean {

    private final MybatisPlusAutoFillProperties autoFillProperties;
    private final MybatisPlusProperties mybatisPlusProperties;

    public MybatisPlusAutoConfiguration(
            MybatisPlusAutoFillProperties autoFillProperties,
            MybatisPlusProperties mybatisPlusProperties) {
        this.autoFillProperties = autoFillProperties;
        this.mybatisPlusProperties = mybatisPlusProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(MybatisPlusAutoConfiguration.class, StarterNameConstants.DATA_MYBATIS_STARTER);
    }

    /**
     * 自定义ID生成器
     */
    @Bean
    @ConditionalOnProperty(
            prefix = MybatisPlusProperties.PREFIX,
            name = "snowFlakeIdGenerator",
            havingValue = "true")
    public IdentifierGenerator identifierGenerator() {
        return new SnowFlakeIdGenerator();
    }

    // @Bean
    // public MybatisPlusPropertiesCustomizer plusPropertiesCustomizer() {
    //	return plusProperties -> plusProperties.getGlobalConfig();
    // }

    /**
     * 自定义方法增强注入
     */
    @Bean
    public ISqlInjector sqlInjector() {
        return new MateSqlInjector();
    }

    /**
     * <p>
     * 注意:
     * 如果内部插件都是使用,需要注意顺序关系,建议使用如下顺序
     * 多租户插件->数据权限插件->动态表名插件->分页插件->乐观锁插件->sql性能规范插件->防止全表更新与删除插件
     * 总结: 对sql进行单次改造的优先放入,不对sql进行改造的最后放入
     * <p>
     * 参考：
     * https://mybatis.plus/guide/interceptor.html#%E4%BD%BF%E7%94%A8%E6%96%B9%E5%BC%8F-%E4%BB%A5%E5%88%86%E9%A1%B5%E6%8F%92%E4%BB%B6%E4%B8%BE%E4%BE%8B
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(List<MpInterceptor> interceptors) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        interceptors.stream()
                .sorted(Comparator.comparing(MpInterceptor::getSortNo))
                .map(MpInterceptor::getInnerInterceptor)
                .forEach(interceptor::addInnerInterceptor);

        return interceptor;
    }

    /**
     * 自动填充数据配置
     */
    @Bean
    @ConditionalOnProperty(
            prefix = MybatisPlusAutoFillProperties.PREFIX,
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true)
    public AutoFieldMetaObjectHandler metaObjectHandler() {
        return new AutoFieldMetaObjectHandler(autoFillProperties);
    }

    /**
     * 数据库配置
     *
     * @return 配置
     */
    @Bean
    public DatabaseIdProvider getDatabaseIdProvider() {
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("Oracle", DbType.ORACLE.getDb());
        properties.setProperty("MySQL", DbType.MYSQL.getDb());
        properties.setProperty("Microsoft SQL Server", DbType.SQL_SERVER.getDb());
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }

    /**
     * IEnum 枚举配置
     */
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            configuration.setDefaultEnumTypeHandler(EnumTypeHandler.class);
            // 关闭 mybatis 默认的日志
            configuration.setLogPrefix("log.mybatis");
        };
    }

    /**
     * IEnum 枚举配置
     */
    // @Bean
    // public ConfigurationCustomizer configurationCustomizer(@Autowired(required = false)
    // BigResultQueryInterceptor
    // bigResultQueryInterceptor,
    //													   @Autowired(required = false) SlowQueryInterceptor slowQueryInterceptor,
    //													   @Autowired(required = false) SqlCollectorInterceptor sqlCollectorInterceptor,
    //													   @Autowired(required = false) SqlLogInterceptor sqlLogInterceptor,
    //													   @Autowired(required = false) SqlValidateInterceptor sqlValidateInterceptor) {
    //	return configuration -> {
    //		Optional.ofNullable(bigResultQueryInterceptor).ifPresent(configuration::addInterceptor);
    //		Optional.ofNullable(slowQueryInterceptor).ifPresent(configuration::addInterceptor);
    //		Optional.ofNullable(sqlCollectorInterceptor).ifPresent(configuration::addInterceptor);
    //		Optional.ofNullable(sqlLogInterceptor).ifPresent(configuration::addInterceptor);
    //		Optional.ofNullable(sqlValidateInterceptor).ifPresent(configuration::addInterceptor);
    //
    //		configuration.setDefaultEnumTypeHandler(EnumTypeHandler.class);
    //		// 关闭 mybatis 默认的日志
    //		configuration.setLogPrefix("log.mybatis");
    //	};
    // }

    /**
     * Mybatis 自定义的类型处理器： 处理XML中 #{name,typeHandler=leftLike} 类型的参数 用于左模糊查询时使用
     *
     * <p>eg： and name like #{name,typeHandler=leftLike}
     *
     * @return 左模糊处理器
     */
    @Bean
    public LeftLikeTypeHandler getLeftLikeTypeHandler() {
        return new LeftLikeTypeHandler();
    }

    /**
     * Mybatis 自定义的类型处理器： 处理XML中 #{name,typeHandler=rightLike} 类型的参数 用于右模糊查询时使用
     *
     * <p>eg： and name like #{name,typeHandler=rightLike}
     *
     * @return 右模糊处理器
     */
    @Bean
    public RightLikeTypeHandler getRightLikeTypeHandler() {
        return new RightLikeTypeHandler();
    }

    /**
     * Mybatis 自定义的类型处理器： 处理XML中 #{name,typeHandler=fullLike} 类型的参数 用于全模糊查询时使用
     *
     * <p>eg： and name like #{name,typeHandler=fullLike}
     *
     * @return 全模糊处理器
     */
    @Bean
    public FullLikeTypeHandler getFullLikeTypeHandler() {
        return new FullLikeTypeHandler();
    }
}
