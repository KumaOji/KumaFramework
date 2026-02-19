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

package com.kuma.boot.data.mybatis.sharding.starter;

import static com.kuma.boot.data.mybatis.sharding.utils.ResourceUtil.getClassesWithAnno;

import com.kuma.boot.data.mybatis.sharding.annos.EnableSharding;
import com.kuma.boot.data.mybatis.sharding.config.DataSourceProps;
import com.kuma.boot.data.mybatis.sharding.core.DatasourceManager;
import com.kuma.boot.data.mybatis.sharding.core.ShardingTransactionManager;
import com.kuma.boot.data.mybatis.sharding.core.SqlSessionFactoryManager;
import com.kuma.boot.data.mybatis.sharding.factories.ClassManager;
import com.kuma.boot.data.mybatis.sharding.factories.ShardingMapperFactory;
import com.kuma.boot.data.mybatis.sharding.utils.Pair;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.jspecify.annotations.NonNull;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 1. 注入Mapper， 包括动态代理的， 和非动态代理的
 * 2. 注入SqlSessionFactory
 * 3. 注入Datasource 以及DatasourceManager
 * 4. 注入事务管理器
 *
 * @author winjeg
 */
public class ShardingStarter implements ImportBeanDefinitionRegistrar, BeanFactoryAware {
    private ConfigurableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata metaData, @NonNull BeanDefinitionRegistry registry) {
        MergedAnnotation<EnableSharding> anno = metaData.getAnnotations().get(EnableSharding.class);
        if (!anno.isPresent()) {
            return;
        }
        String[] packages = anno.getStringArray("packages");
        if (packages.length == 0) {
            return;
        }
        Pair<List<Class<?>>, List<Class<?>>> classesPair = getClassesWithAnno(packages);
        // 生成sharding所依赖的interface，并且load到jvm
        ClassManager classManager = null;
        if (!classesPair.right().isEmpty()) {
            classManager = new ClassManager(classesPair.right());
        }
        DataSourceProps datasourceCfg = getDatasourceCfg();
        DatasourceManager datasourceManager = new DatasourceManager(datasourceCfg);
        registerDatasource(datasourceManager);

        List<Class<?>> classes = new ArrayList<>();
        classes.addAll(classesPair.left());
        classes.addAll(classesPair.right());
        SqlSessionFactoryManager sessionManager =
                new SqlSessionFactoryManager(classes, datasourceManager);
        if (classManager != null) {
            registerSharding(classesPair.right(), sessionManager, classManager);
        }
        if (!classesPair.left().isEmpty()) {
            registerNonShardingClasses(classesPair.left(), sessionManager);
        }
        beanFactory.registerSingleton("session_factory_manager", sessionManager);
        beanFactory.registerSingleton(
                "sharding_trans_mgr", new ShardingTransactionManager(datasourceManager));
        beanFactory.registerSingleton("sharding_trans_def", new DefaultTransactionDefinition(0));
    }

    private void registerDatasource(DatasourceManager datasourceManager) {
        datasourceManager.foreach(
                (name, ds) -> {
                    beanFactory.registerSingleton(name, ds);
                });
    }

    private void registerSharding(
            List<Class<?>> classes,
            SqlSessionFactoryManager sessionManager,
            ClassManager classManager) {
        ShardingMapperFactory factory = new ShardingMapperFactory(sessionManager, classManager);
        for (Class<?> clz : classes) {
            Object mapper = factory.createProxy(clz);
            beanFactory.registerSingleton(clz.getCanonicalName(), mapper);
        }
    }

    private void registerNonShardingClasses(
            List<Class<?>> classes, SqlSessionFactoryManager sessionManager) {
        for (Class<?> clz : classes) {
            Object mapper = sessionManager.getMapper(clz);
            beanFactory.registerSingleton(clz.getCanonicalName(), mapper);
        }
    }

    private DataSourceProps getDatasourceCfg() {
        Environment env = beanFactory.getBean(Environment.class);
        try {
            Iterable<ConfigurationPropertySource> sources = ConfigurationPropertySources.get(env);
            Binder binder = new Binder(sources);
            BindResult<DataSourceProps> config = binder.bind("datasource", DataSourceProps.class);
            if (config == null
                    || config.get() == null
                    || config.get().getList() == null
                    || config.get().getList().length == 0) {
                return null;
            }
            return config.get();
        } catch (Exception ignored) {
        }
        return null;
    }
}
