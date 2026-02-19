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

package com.kuma.boot.data.mybatis.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.support.Collector;
import com.kuma.boot.data.mybatis.interceptor.cipher.interceptor.FieldEncryptInterceptor;
import com.kuma.boot.data.mybatis.interceptor.easylog.dataaudit.interceptor.DataOperateInterceptor;
import com.kuma.boot.data.mybatis.interceptor.encrypt.config.EncryptorAutoConfiguration;
import com.kuma.boot.data.mybatis.interceptor.encrypt.core.EncryptorManager;
import com.kuma.boot.data.mybatis.interceptor.encrypt.interceptor.MybatisDecryptInterceptor;
import com.kuma.boot.data.mybatis.interceptor.encrypt.interceptor.MybatisEncryptInterceptor;
import com.kuma.boot.data.mybatis.interceptor.sql.BigResultQueryInterceptor;
import com.kuma.boot.data.mybatis.interceptor.sql.SlowSqlInterceptor;
import com.kuma.boot.data.mybatis.interceptor.sql.SqlCollectorInterceptor;
import com.kuma.boot.data.mybatis.interceptor.sql.SqlValidateInterceptor;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.core.SqlAnalysisAspect;
import com.kuma.boot.data.mybatis.autoconfigure.properties.MybatisInterceptorProperties;
import java.util.Properties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

/**
 * 插件容器
 */
@AutoConfiguration(after = EncryptorAutoConfiguration.class)
@ConditionalOnProperty(
        prefix = MybatisInterceptorProperties.PREFIX,
        name = "enabled",
        havingValue = "true")
public class MybatisInterceptorAutoConfiguration implements InitializingBean {

    private final MybatisInterceptorProperties interceptorProperties;

    public MybatisInterceptorAutoConfiguration(MybatisInterceptorProperties interceptorProperties) {
        this.interceptorProperties = interceptorProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(MybatisInterceptorAutoConfiguration.class, StarterNameConstants.DATA_MYBATIS_STARTER);
    }

    /**
     * 如果使用了多个mybatis组件，建议把该组件放在最前面，防止其它组件对mybatis相关对象进行二次包装，无法获取对应的数据
     */
    @Bean
    @Order(1)
    public SqlAnalysisAspect getSqlAnalysisAspect() {
        SqlAnalysisAspect plugin = new SqlAnalysisAspect();
        Properties properties = new Properties();
        // 是否开启分析功能
        properties.setProperty("analysisSwitch", "true");
        // 是否对一个sqlid只分析一次
        properties.setProperty("onlyCheckOnce", "true");
        plugin.setProperties(properties);
        return plugin;
    }

    /**
     * 日志打印插件
     */
    @Bean
    @Order(2)
    @ConditionalOnProperty(
            prefix = MybatisInterceptorProperties.SQL_LOG_PREFIX,
            name = "enabled",
            havingValue = "true")
    public SlowSqlInterceptor sqlLogInterceptor() {
        return new SlowSqlInterceptor();
    }

    @Bean
    @Order(3)
    public SqlValidateInterceptor sqlValidateInterceptor() {
        return new SqlValidateInterceptor();
    }

    /**
     * sql收集插件
     */
    @Bean
    @Order(4)
    @ConditionalOnProperty(
            prefix = MybatisInterceptorProperties.SQL_COLLECTOR_PREFIX,
            name = "enabled",
            havingValue = "true")
    public SqlCollectorInterceptor sqlCollectorInterceptor(Collector collector) {
        return new SqlCollectorInterceptor(collector);
    }

    /**
     * 字段加解密插件
     */
    @Bean
    @Order(5)
    @ConditionalOnProperty(
            prefix = MybatisInterceptorProperties.CIPHER_ENCRYPT_PREFIX,
            name = "enabled",
            havingValue = "true")
    public FieldEncryptInterceptor fieldEncryptInterceptor() {
        return new FieldEncryptInterceptor();
    }

    @Bean
    @Order(6)
    @ConditionalOnProperty(
            prefix = MybatisInterceptorProperties.FIELD_ENCRYPT_PREFIX,
            name = "enabled",
            havingValue = "true")
    public MybatisEncryptInterceptor mybatisEncryptInterceptor(EncryptorManager encryptorManager) {
        MybatisInterceptorProperties.FieldEncrypt fieldEncrypt =
                interceptorProperties.getFieldEncrypt();
        return new MybatisEncryptInterceptor(encryptorManager, fieldEncrypt);
    }

    @Bean
    @Order(7)
    @ConditionalOnProperty(
            prefix = MybatisInterceptorProperties.FIELD_ENCRYPT_PREFIX,
            name = "enabled",
            havingValue = "true")
    public MybatisDecryptInterceptor mybatisDecryptInterceptor(EncryptorManager encryptorManager) {
        MybatisInterceptorProperties.FieldEncrypt fieldEncrypt =
                interceptorProperties.getFieldEncrypt();
        return new MybatisDecryptInterceptor(encryptorManager, fieldEncrypt);
    }

    /**
     * 数据更新、增加、删除记录插件
     */
    @Bean
    @Order(8)
    @ConditionalOnProperty(
            prefix = MybatisInterceptorProperties.DATA_OPERATE_PREFIX,
            name = "enabled",
            havingValue = "true")
    public DataOperateInterceptor dataOperateInterceptor() {
        return new DataOperateInterceptor();
    }

    /**
     * SQL执行时间监控插件
     */
    //    @Bean
    //    @ConditionalOnProperty(
    //            prefix = MybatisInterceptorProperties.SLOW_QUERY_PREFIX,
    //            name = "enabled",
    //            havingValue = "true")
    //    public SlowQueryInterceptor slowQueryInterceptor() {
    //        MybatisInterceptorProperties.SlowQuery slowQuery =
    // interceptorProperties.getSlowQuery();
    //        return new SlowQueryInterceptor(slowQuery.getEnabled(),
    // slowQuery.getSlowSqlThresholdMs());
    //    }

    /**
     * 查询大结果集监控插件
     */
    @Bean
    @Order(9)
    @ConditionalOnProperty(
            prefix = MybatisInterceptorProperties.BIG_RESULT_QUERY_PREFIX,
            name = "enabled",
            havingValue = "true")
    public BigResultQueryInterceptor bigResultQueryInterceptor() {
        MybatisInterceptorProperties.BigResultQuery bigResultQuery =
                interceptorProperties.getBigResultQuery();
        return new BigResultQueryInterceptor(bigResultQuery.getEnabled(), bigResultQuery.getSize());
    }
}
