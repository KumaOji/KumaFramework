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

package com.kuma.boot.data.mybatis.sharding.factories;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.sharding.config.DataSourceProps;
import com.kuma.boot.data.mybatis.sharding.config.DataSourceProps.HikariProps;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 * 创建hikaricp的统一工厂
 *
 * @author winjeg
 */
public class HikariCPFactory {

    public static DataSource createOne(HikariProps props) {
        HikariConfig config = new HikariConfig();
        config.setConnectionTestQuery(props.getConnectionTestQuery());
        config.setConnectionTimeout(props.getConnectionTimeout());
        // config.setIdleTimeout(props.getIdleTimeout());
        config.setDriverClassName(props.getDriverClassName());
        config.setPoolName(props.getName());
        config.setJdbcUrl(props.getJdbcUrl());
        config.setUsername(props.getUsername());
        config.setPassword(props.getPassword());
        config.setMaximumPoolSize(props.getMaximumPoolSize());
        config.addDataSourceProperty("dataSource.cachePrepStmts", "true");
        config.addDataSourceProperty("dataSource.prepStmtCacheSize", "250");
        config.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("dataSource.useServerPrepStmts", "true");
        LogUtils.info(
                "createOne ----  datasource:{} of type:{} created...",
                props.getName(),
                props.getDriverClassName());
        return new HikariDataSource(config);
    }

    /**
     * 创建结果为list
     */
    public static List<DataSource> createAll(DataSourceProps props) {
        if (props.getList() == null || props.getList().length == 0) {
            return Collections.emptyList();
        }
        List<DataSource> result = new ArrayList<>(props.getList().length);
        for (HikariProps prop : props.getList()) {
            result.add(createOne(prop));
        }
        return result;
    }

    /**
     * 创建结果为map
     */
    public static Map<String, DataSource> createAllMap(DataSourceProps props) {
        if (props.getList() == null || props.getList().length == 0) {
            return Collections.emptyMap();
        }
        Map<String, DataSource> result = new HashMap<>(props.getList().length, 1);
        for (HikariProps prop : props.getList()) {
            result.put(prop.getName(), createOne(prop));
        }
        return result;
    }
}
