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
import com.kuma.boot.common.support.thread.MDCThreadPoolTaskExecutor;
import com.kuma.boot.common.support.thread.ThreadPoolFactory;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.autoconfigure.properties.AsyncProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务配置
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:01:42
 */
@EnableConfigurationProperties({AsyncProperties.class})
@AutoConfiguration
public class AsyncThreadPoolAutoConfiguration implements InitializingBean {

    private final AsyncProperties asyncProperties;

    public AsyncThreadPoolAutoConfiguration(AsyncProperties asyncProperties) {
        this.asyncProperties = asyncProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(AsyncThreadPoolAutoConfiguration.class, StarterNameConstants.CORE_STARTER);
    }

    @Bean("asyncThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor() {
        MDCThreadPoolTaskExecutor executor = new MDCThreadPoolTaskExecutor();

        executor.setVirtualThreads(asyncProperties.isVirtualThreads());
        // 线程池名的前缀
        executor.setThreadNamePrefix(asyncProperties.getThreadNamePrefix());
        // 核心线程数
        executor.setCorePoolSize(asyncProperties.getCorePoolSize());
        // 最大线程数
        executor.setMaxPoolSize(asyncProperties.getMaxPoolSiz());
        // 允许线程的空闲时间
        executor.setKeepAliveSeconds(asyncProperties.getKeepAliveSeconds());
        // 任务队列容量（阻塞队列）
        executor.setQueueCapacity(asyncProperties.getQueueCapacity());
        // 是否允许核心线程超时
        executor.setAllowCoreThreadTimeOut(asyncProperties.isAllowCoreThreadTimeOut());
        // 应用关闭时-是否等待未完成任务继续执行，再继续销毁其他的Bean
        executor.setWaitForTasksToCompleteOnShutdown(asyncProperties.isWaitForTasksToCompleteOnShutdown());
        // 应用关闭时-继续等待时间（单位：秒）
        executor.setAwaitTerminationSeconds(asyncProperties.getAwaitTerminationSeconds());
        // ThreadFactory
        executor.setThreadFactory(new ThreadPoolFactory(asyncProperties.getThreadNamePrefix(), executor));
        // 异步线程上下文装饰器
        executor.setTaskDecorator(new MDCRequestTaskDecorator(asyncProperties.isEnableServletAsyncContext(),
                asyncProperties.getServletAsyncContextTimeoutMillis()));
        /*
         线程池拒绝策略
         rejection-policy：当pool已经达到max size的时候，如何处理新任务
         CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
         // AbortPolicy: 直接抛出java.util.concurrent.RejectedExecutionException异常
        // CallerRunsPolicy: 主线程直接执行该任务，执行完之后尝试添加下一个任务到线程池中，可以有效降低向线程池内添加任务的速度
        // DiscardOldestPolicy: 抛弃旧的任务、暂不支持；会导致被丢弃的任务无法再次被执行
        // DiscardPolicy: 抛弃当前任务、暂不支持；会导致被丢弃的任务无法再次被执行
         */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();

        // ThreadFactory factory = Thread.ofVirtual().factory();
        // ExecutorService executorService = newThreadPerTaskExecutor(factory);
        // new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());

        return executor;
    }

}
