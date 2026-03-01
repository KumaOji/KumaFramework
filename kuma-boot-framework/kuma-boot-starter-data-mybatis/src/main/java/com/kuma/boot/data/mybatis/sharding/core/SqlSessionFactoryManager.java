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

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.sharding.annos.Sharding;
import com.kuma.boot.data.mybatis.sharding.utils.Pair;
import com.kuma.boot.data.mybatis.sharding.utils.ResourceUtil;
import com.kuma.boot.data.mybatis.sharding.utils.XmlUtils;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import javax.sql.DataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * 管理sql session factory的东西， 同时管理 transaction 相关
 *
 */
public class SqlSessionFactoryManager {
    private static final Map<String, SqlSessionFactoryBean> SESSION_FACTORY_MAP =
            new ConcurrentHashMap<>();
    private static final Map<String, SqlSessionTemplate> TEMPLATE_MAP = new ConcurrentHashMap<>();
    private static final Map<String, String> RES_MAP = new ConcurrentHashMap<>();
    private static final ResourcePatternResolver RESOLVER =
            new PathMatchingResourcePatternResolver();
    private final List<Class<?>> classes;
    private final com.kuma.boot.data.mybatis.sharding.core.DatasourceManager datasourceManager;

    private Interceptor interceptor;

    public SqlSessionFactoryManager(List<Class<?>> cs, com.kuma.boot.data.mybatis.sharding.core.DatasourceManager dsManager) {
        this.classes = cs;
        this.datasourceManager = dsManager;
        init();
    }

    /**
     * 创建SQLSessionFactoryBean
     *
     * @param name       名称
     * @param dataSource 数据源
     * @param locations  对应xml文件
     * @return 对象
     */
    public SqlSessionFactoryBean createSqlSession(
            String name, DataSource dataSource, Resource... locations) {
        Configuration configuration = new Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        if (interceptor != null) {
            configuration.addInterceptor(interceptor);
        }
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setMapperLocations(locations);
        factoryBean.setDataSource(dataSource);
        factoryBean.setConfiguration(configuration);
        return factoryBean;
    }

    public Interceptor getInterceptor() {
        return interceptor;
    }

    public void init() {
        // 初始化所有的Mapper.xml
        datasourceManager.foreach(
                (name, ds) -> {
                    Map<String, Resource> resources = extractResources(name);
                    for (Map.Entry<String, Resource> entry : resources.entrySet()) {
                        RES_MAP.put(entry.getKey(), name);
                    }
                    SqlSessionFactoryBean sessionFactory =
                            createSqlSession(name, ds, toArray(resources.values()));

                    SESSION_FACTORY_MAP.putIfAbsent(name, sessionFactory);
                    SqlSessionTemplate template = null;
                    try {
                        template =
                                new SqlSessionTemplate(
                                        Objects.requireNonNull(sessionFactory.getObject()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    TEMPLATE_MAP.putIfAbsent(name, template);
                });
    }

    /**
     * 利用MyBatis的方法获取Mapper
     *
     * @param clz 类型
     * @param <T> 参数类型
     * @return mapper对象
     */
    public <T> T getMapper(Class<?> clz) {
        SqlSessionTemplate template = TEMPLATE_MAP.get(RES_MAP.get(clz.getCanonicalName()));
        if (template == null) {
            throw new RuntimeException("datasource init failed, template null!");
        }
        return (T) template.getMapper(clz);
    }

    /**
     * 解析resource
     *
     * @param dsName 数据源名称
     * @return 资源列表
     */
    private Map<String, Resource> extractResources(String dsName) {
        Map<String, Resource> result = new HashMap<>();
        for (Class<?> clz : classes) {
            Sharding sharding = ResourceUtil.getShardingAnno(clz);
            if (sharding == null || !contains(sharding.datasource(), dsName)) {
                continue;
            }
            boolean isSharding = !sharding.dbRule().isEmpty() || !sharding.tableRule().isEmpty();
            Resource[] resources;
            try {
                resources = RESOLVER.getResources(sharding.mapperLocation());
            } catch (IOException e) {
                LogUtils.warn("extractShardingResources - mapper location error", e);
                continue;
            }
            for (Resource r : resources) {
                if (XmlUtils.extractNamespace(r).equals(clz.getCanonicalName())) {
                    if (isSharding) {
                        Pair<String, Resource> resourcePair =
                                XmlUtils.changeMapperNameSpace(dsName, r);
                        if (resourcePair != null) {
                            result.putIfAbsent(resourcePair.left(), resourcePair.right());
                        }
                    } else {
                        result.putIfAbsent(XmlUtils.extractNamespace(r), r);
                    }
                }
            }
        }
        return result;
    }

    private static boolean contains(String[] arr, String element) {
        if (arr == null || element == null) {
            return false;
        }
        for (String e : arr) {
            if (element.equals(e)) {
                return true;
            }
        }
        return false;
    }

    private static Resource[] toArray(Collection<Resource> collection) {
        if (collection == null || collection.isEmpty()) {
            return new Resource[] {};
        }
        Resource[] result = new Resource[collection.size()];
        int i = 0;
        for (Resource e : collection) {
            result[i] = e;
            i++;
        }
        return result;
    }

    public void foreach(BiConsumer<String, SqlSessionTemplate> consumer) {
        TEMPLATE_MAP.forEach(consumer);
    }
}
