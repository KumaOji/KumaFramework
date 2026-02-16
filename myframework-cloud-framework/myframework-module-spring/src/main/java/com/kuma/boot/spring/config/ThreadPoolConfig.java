package com.kuma.boot.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author Kuma
 * @version 1.0
 */
@Configuration
@EnableAsync
@Lazy
public class ThreadPoolConfig implements AsyncConfigurer {

    /**
     * 命令执行线程池
     * 用于执行系统命令和读取输出
     */
    @Bean("commandExecutor")
    @Lazy
    public Executor commandExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("command-executor-");
        executor.setKeepAliveSeconds(60);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
    /**
     * 异步任务执行器（懒加载）
     * 用于异步初始化非关键组件，延迟初始化以提高启动速度
     * 作为默认的异步执行器
     */
    @Bean("normalExecutor")
    @Lazy
    public Executor normalExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("normal-executor-");
        executor.setKeepAliveSeconds(60);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }

    /**
     * 默认异步执行器
     * 实现 AsyncConfigurer 接口，设置默认的异步执行器
     */
    @Override
    public Executor getAsyncExecutor() {
        return normalExecutor();
    }
}

