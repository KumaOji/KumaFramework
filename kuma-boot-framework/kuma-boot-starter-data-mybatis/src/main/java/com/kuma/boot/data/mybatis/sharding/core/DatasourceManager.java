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

package com.kuma.boot.data.mybatis.sharding.core;

import com.kuma.boot.data.mybatis.sharding.config.DataSourceProps;
import com.kuma.boot.data.mybatis.sharding.factories.HikariCPFactory;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import javax.sql.DataSource;

/**
 * 管理所有的datasource, 不管是否分库分表
 *
 * @author winjeg
 */
public class DatasourceManager {
    private static final Map<String, DataSource> DATASOURCE_MAP = new ConcurrentHashMap<>();
    private final DataSourceProps props;

    public DataSource get(String name) {
        return DATASOURCE_MAP.get(name);
    }

    /**
     * provide a method to traverse all the datasource managed by the manager
     *
     * @param consumer the consumer that need access datasource
     */
    public void foreach(BiConsumer<String, DataSource> consumer) {
        for (Entry<String, DataSource> entry : DATASOURCE_MAP.entrySet()) {
            consumer.accept(entry.getKey(), entry.getValue());
        }
    }

    public DatasourceManager(DataSourceProps props) {
        this.props = props;
        init();
    }

    public void init() {
        DATASOURCE_MAP.putAll(HikariCPFactory.createAllMap(props));
    }
}
