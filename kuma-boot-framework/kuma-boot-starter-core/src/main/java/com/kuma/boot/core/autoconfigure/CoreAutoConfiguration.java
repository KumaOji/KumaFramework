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

package com.kuma.boot.core.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.model.PropertyCache;
import com.kuma.boot.common.model.Pubsub;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.autoconfigure.properties.CoreProperties;
import com.kuma.boot.core.runtime.listener.StartedEventListener;
import com.kuma.boot.core.runtime.runner.KmcApplicationRunner;
import com.kuma.boot.core.runtime.runner.KmcCommandLineRunner;
import com.kuma.boot.core.support.Collector;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 核心配置
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:05:41
 */
@AutoConfiguration
@EnableConfigurationProperties({CoreProperties.class})
@ConditionalOnProperty(prefix = CoreProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class CoreAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(CoreAutoConfiguration.class, StarterNameConstants.CORE_STARTER);
    }

    //在6.2之前的所以版本中Spring 容器在实例化单例Bean时，都是在一个for循环中，一个一个的实例化。自6.2起，单例bean的初始化支持多线程。
    // 底层通过Executor +CompletableFuture#runAsync实现。
    // 同时你还可以自定义线程池对象，如下方式：
    //bean的名称必须是bootstrapExecutor。
    //@Bean
    //public Executor bootstrapExecutor() {
    //	ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    //	taskExecutor.setCorePoolSize(50);
    //	taskExecutor.setMaxPoolSize(50);
    //	taskExecutor.setQueueCapacity(200);
    //	taskExecutor.setVirtualThreads(true);
    //	taskExecutor.initialize();
    //	return taskExecutor;
    //}


    @Bean
    public Collector collector(CoreProperties coreProperties) {
        return new Collector(coreProperties);
    }

    @Bean
    public PropertyCache propertyCache() {
        return new PropertyCache(new Pubsub<>());
    }

    @Bean
    public KmcApplicationRunner coreApplicationRunner() {
        return new KmcApplicationRunner();
    }

    @Bean
    @ConditionalOnBean(PropertyCache.class)
    public KmcCommandLineRunner coreCommandLineRunner(PropertyCache propertyCache,
                                                      CoreProperties coreProperties) {
        return new KmcCommandLineRunner(propertyCache, coreProperties);
    }

    @Bean
    public StartedEventListener startedEventListener() {
        return new StartedEventListener();
    }

    //@Configuration
    //public static class CoreFunction implements Function<String, String> {
    //
    //	@Override
    //	public String apply(String s) {
    //		return s;
    //	}
    //}
}
