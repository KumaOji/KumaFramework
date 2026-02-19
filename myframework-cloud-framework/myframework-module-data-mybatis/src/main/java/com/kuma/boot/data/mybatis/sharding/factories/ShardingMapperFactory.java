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

import static com.kuma.boot.data.mybatis.sharding.utils.ResourceUtil.getShardingAnno;

import com.kuma.boot.data.mybatis.sharding.annos.Sharding;
import com.kuma.boot.data.mybatis.sharding.core.ShardingCoreHandler;
import com.kuma.boot.data.mybatis.sharding.core.SqlSessionFactoryManager;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 核心逻辑之二， 分库分表动态代理的实现处
 *
 * @author winjeg
 */
public class ShardingMapperFactory {

    private static final Map<Class<?>, ShardingCoreHandler> HANDLER_MAP = new ConcurrentHashMap<>();

    private final SqlSessionFactoryManager sessionFactoryManager;

    private final com.kuma.boot.data.mybatis.sharding.factories.ClassManager classManager;

    public ShardingMapperFactory(
            SqlSessionFactoryManager sessionFactoryManager, com.kuma.boot.data.mybatis.sharding.factories.ClassManager classManager) {
        this.sessionFactoryManager = sessionFactoryManager;
        this.classManager = classManager;
    }

    /**
     * 创建动态代理， 基于jdk
     *
     * @param clz 代理接口类型
     */
    public <T> T createProxy(Class<T> clz) {
        Sharding sharding = getShardingAnno(clz);
        boolean isSharding = !sharding.dbRule().isEmpty() || !sharding.tableRule().isEmpty();
        if (isSharding) {
            ShardingCoreHandler handler = HANDLER_MAP.get(clz);
            if (handler == null) {
                handler =
                        new ShardingCoreHandler(sharding, sessionFactoryManager, clz, classManager);
                HANDLER_MAP.putIfAbsent(clz, handler);
            }
            return (T)
                    Proxy.newProxyInstance(
                            this.getClass().getClassLoader(), new Class[] {clz}, handler);
        } else {
            return null;
        }
    }
}
