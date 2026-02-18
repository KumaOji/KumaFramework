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

package com.kuma.boot.core.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.support.thread.MDCRequestTaskDecorator;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.autoconfigure.properties.AsyncProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.task.SimpleAsyncTaskExecutorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步任务配置
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:01:42
 */
@AutoConfiguration(after = AsyncThreadPoolAutoConfiguration.class)
@EnableAsync(proxyTargetClass = true)
@EnableConfigurationProperties({AsyncProperties.class})
@ConditionalOnProperty(prefix = AsyncProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
//public class AsyncAutoConfiguration implements AsyncConfigurer, InitializingBean {
public class AsyncAutoConfiguration implements InitializingBean {

    private final AsyncProperties asyncProperties;
    private final ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor;

    public AsyncAutoConfiguration(AsyncProperties asyncProperties,
                                  @Autowired @Qualifier("asyncThreadPoolTaskExecutor") ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor) {
        this.asyncProperties = asyncProperties;
        this.asyncThreadPoolTaskExecutor = asyncThreadPoolTaskExecutor;
    }

    @Bean
    public SimpleAsyncTaskExecutorCustomizer simpleAsyncTaskExecutorCustomizer(){
        return new SimpleAsyncTaskExecutorCustomizer() {
            @Override
            public void customize(SimpleAsyncTaskExecutor taskExecutor) {
                taskExecutor.setVirtualThreads(true);
            }
        };
    }

    @Bean
    public MDCRequestTaskDecorator mdcRequestTaskDecorator(){
        return new MDCRequestTaskDecorator(asyncProperties.isEnableServletAsyncContext(), asyncProperties.getServletAsyncContextTimeoutMillis());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(AsyncAutoConfiguration.class, StarterNameConstants.CORE_STARTER);
    }

    //@Override
    //public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    //	return (ex, method, params) -> LogUtils.error(
    //		ex,
    //		"AsyncUncaughtExceptionHandler {} class: {} method: {} params: {}",
    //		asyncProperties.getThreadNamePrefix(),
    //		method.getDeclaringClass().getName(),
    //		method.getName(),
    //		params);
    //}
    //
    //@Override
    //public Executor getAsyncExecutor() {
    //	return asyncThreadPoolTaskExecutor;
    //}

}
