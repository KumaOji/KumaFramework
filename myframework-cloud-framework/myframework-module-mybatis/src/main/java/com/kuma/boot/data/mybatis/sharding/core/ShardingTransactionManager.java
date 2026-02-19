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

package com.kuma.boot.data.mybatis.sharding.core;

import com.kuma.boot.data.mybatis.sharding.annos.Sharding;
import com.kuma.boot.data.mybatis.sharding.utils.ExpressionUtil;
import com.kuma.boot.data.mybatis.sharding.utils.ResourceUtil;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 分库分表情况下事务管理器
 * 有三种方法获取事务管理器
 *
 * @author winjeg
 */
public class ShardingTransactionManager {

    private static final Map<String, DataSourceTransactionManager> TRANS_MAP = new HashMap<>();

    private final com.kuma.boot.data.mybatis.sharding.core.DatasourceManager datasourceManager;

    public ShardingTransactionManager(com.kuma.boot.data.mybatis.sharding.core.DatasourceManager ds) {
        datasourceManager = ds;
        init();
    }

    private void init() {
        datasourceManager.foreach(
                (name, ds) -> {
                    TRANS_MAP.put(name, new DataSourceTransactionManager(ds));
                });
    }

    /**
     * 对于分库分表的情况， 不能明确计算是哪个数据源的，
     */
    public PlatformTransactionManager getTransactionManager(Class<?> mapperClz, long shardingVal) {
        Sharding sharding = ResourceUtil.getShardingAnno(mapperClz);
        if (sharding == null) {
            throw new IllegalArgumentException("this mapper is illegal");
        }
        if (sharding.dbRule().isEmpty()) {
            return TRANS_MAP.get(sharding.datasource()[0]);
        } else {
            String dsName =
                    ExpressionUtil.eval(sharding.dbRule(), sharding.shardingKey(), shardingVal);
            return TRANS_MAP.get(dsName);
        }
    }

    /**
     * 根据分库规则，以及key 计算出实际数据源，拿到对应的管理器
     */
    public PlatformTransactionManager getTransactionManager(
            String dbRule, String key, long shardingVal) {
        String dsName = ExpressionUtil.eval(dbRule, key, shardingVal);
        return TRANS_MAP.get(dsName);
    }

    /**
     * 对于可以明确是哪个datasource的来说， 用这个就足以， 这个效率会高一些
     *
     * @param ds 数据源名称
     * @return 事务管理器
     */
    public PlatformTransactionManager getTransactionManager(String ds) {
        return TRANS_MAP.get(ds);
    }
}
