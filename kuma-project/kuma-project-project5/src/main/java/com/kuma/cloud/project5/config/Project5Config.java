package com.kuma.cloud.project5.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * project5 基础配置
 */
@Configuration
public class Project5Config {

    /**
     * 幂等异常事件处理线程池
     * 供 kuma-boot-starter-idempotent 的 DefaultIdempotentExceptionEventHandler 使用
     */
    @Bean("idempotentExceptionEventExecutor")
    public ExecutorService idempotentExceptionEventExecutor() {
        return new ThreadPoolExecutor(
                2, 5,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1024),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
