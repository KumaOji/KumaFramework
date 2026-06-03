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

package com.kuma.boot.dingtalk.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.autoconfigure.properties.DingtalkProperties;
import com.kuma.boot.dingtalk.constant.DingerConstant;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * DINGTALK线程池配置类
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:16:41
 */
@AutoConfiguration
@ConditionalOnMissingBean(name = DingerConstant.DINGER_EXECUTOR)
public class DingtalkThreadPoolAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(DingtalkThreadPoolAutoConfiguration.class, StarterNameConstants.DINGTALK_STARTER);
    }

    @Bean(name = DingerConstant.DINGER_EXECUTOR)
    public Executor dingTalkExecutor( DingtalkProperties dingtalkProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        DingtalkProperties.DingtalkThreadPool dingtalkThreadPoolProperties = dingtalkProperties.getThreadPool();
        // 核心线程数
        executor.setCorePoolSize(dingtalkThreadPoolProperties.getCoreSize());
        // 最大线程数
        executor.setMaxPoolSize(dingtalkThreadPoolProperties.getMaxSize());
        // 线程最大空闲时间
        executor.setKeepAliveSeconds(dingtalkThreadPoolProperties.getKeepAliveSeconds());
        // 队列大小
        executor.setQueueCapacity(dingtalkThreadPoolProperties.getQueueCapacity());
        // 指定用于新创建的线程名称的前缀
        executor.setThreadNamePrefix(dingtalkThreadPoolProperties.getThreadNamePrefix());

        // 使用自定义拒绝策略, 直接抛出异常
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 等待任务完成时再关闭线程池--表明等待所有线程执行完
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 初始化
        executor.initialize();
        return executor;
    }
}
