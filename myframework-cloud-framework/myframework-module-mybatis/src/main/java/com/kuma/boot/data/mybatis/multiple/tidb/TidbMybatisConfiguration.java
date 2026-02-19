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

package com.kuma.boot.data.mybatis.multiple.tidb;

import com.kuma.boot.data.datasource.multiple.tidb.TidbDataSourceConfiguration;
import com.kuma.boot.data.mybatis.multiple.trino.TrinoMybatisConfiguration;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * TidbMybatisConfiguration
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@AutoConfiguration(after = TidbDataSourceConfiguration.class)
@ConditionalOnBean(value = DataSource.class, name = "tidbDruidDataSource")
@ConditionalOnProperty(name = "kuma.boot.data.datasource.multiple.tidb.enabled", havingValue = "true")
@MapperScan(
        basePackages = "com.kuma.cloud.*.tidb.mapper",
        sqlSessionFactoryRef = "tidbSqlSessionFactory")
public class TidbMybatisConfiguration {

    private static Logger logger = LoggerFactory.getLogger(TrinoMybatisConfiguration.class);

    // 这里是mapper.xml路径， 根据自己的项目调整
    private static final String MAPPER_LOCATION = "classpath*:mapper/tidb/*.xml";
    // 这里是数据库表对应的entity实体类所在包路径， 根据自己的项目调整
    private static final String TYPE_ALIASES_PACKAGE = "com.kuma.boot.data.analysis.tidb.*";

    @Bean("tidbSqlSessionFactory")
    public SqlSessionFactory tidbSqlSessionFactory(
            @Qualifier("tidbDruidDataSource") DataSource dataSource ) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        // mapper的xml形式文件位置必须要配置，不然将报错：no statement （这种错误也可能是mapper的xml中，namespace与项目的路径不一致导致）
        bean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATION));
        bean.setTypeAliasesPackage(TYPE_ALIASES_PACKAGE);
        return bean.getObject();
    }

    @Bean("tidbSqlSessionTemplate")
    public SqlSessionTemplate tidbSqlSessionTemplate(
            @Qualifier("tidbSqlSessionFactory") SqlSessionFactory sqlSessionFactory ) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
