/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
 */
package com.kuma.boot.dingtalk.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.autoconfigure.properties.DingtalkProperties;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@AutoConfiguration
@ConditionalOnMissingBean(name={"dingtalkExecutor"})
public class DingtalkThreadPoolAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(DingtalkThreadPoolAutoConfiguration.class, (String)"kuma-boot-starter-dingtalk", (String[])new String[0]);
    }

    @Bean(name={"dingtalkExecutor"})
    public Executor dingTalkExecutor(DingtalkProperties dingtalkProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        DingtalkProperties.DingtalkThreadPool dingtalkThreadPoolProperties = dingtalkProperties.getThreadPool();
        executor.setCorePoolSize(dingtalkThreadPoolProperties.getCoreSize());
        executor.setMaxPoolSize(dingtalkThreadPoolProperties.getMaxSize());
        executor.setKeepAliveSeconds(dingtalkThreadPoolProperties.getKeepAliveSeconds());
        executor.setQueueCapacity(dingtalkThreadPoolProperties.getQueueCapacity());
        executor.setThreadNamePrefix(dingtalkThreadPoolProperties.getThreadNamePrefix());
        executor.setRejectedExecutionHandler((RejectedExecutionHandler)new ThreadPoolExecutor.AbortPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}

