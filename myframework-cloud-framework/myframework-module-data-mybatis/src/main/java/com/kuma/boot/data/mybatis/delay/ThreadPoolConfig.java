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

package com.kuma.boot.data.mybatis.delay;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 线程池配置类
 */
@EnableAsync
@Configuration("delayThreadPoolConfig")
public class ThreadPoolConfig {
    /**
     * 核心线程数量
     */
    @Value("${delay.thread.core}")
    private Integer core;

    /**
     * 最大线程数
     */
    @Value("${delay.thread.max}")
    private Integer max;

    /**
     * 排队线程数
     */
    @Value("${delay.thread.queue}")
    private Integer queue;

    /**
     * 线程回收时间
     */
    @Value("${delay.thread.keepAlive}")
    private Integer keepAlive;

    /**
     * delayThreadPool
     *
     * @return {@link ThreadPoolExecutor} 线程池
     */
    @Bean("delayThreadPool")
    public ThreadPoolExecutor arxmlThreadPoolExecutor() {
        ThreadFactory namedThreadFactory =
                new ThreadFactoryBuilder().setNameFormat("build-tool-%d").build();
        return new ThreadPoolExecutor(
                core,
                max,
                keepAlive,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queue),
                namedThreadFactory);
    }
}
