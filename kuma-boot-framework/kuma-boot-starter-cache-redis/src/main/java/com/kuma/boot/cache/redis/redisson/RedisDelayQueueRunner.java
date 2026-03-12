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

package com.kuma.boot.cache.redis.redisson;

import com.kuma.boot.cache.redis.redisson.handle.RedisDelayQueueHandle;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.boot.CommandLineRunner;

/**
 * 启动延迟队列
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:45:59
 */
public class RedisDelayQueueRunner implements CommandLineRunner {

    private static ExecutorService executor;

    static {
        init();
    }

    public static synchronized void init() {
        if (null != executor) {
            executor.shutdownNow();
        }

        // 最佳的线程数 = CPU可用核心数 / (1 - 阻塞系数)
        AtomicInteger counter = new AtomicInteger(0);
        ThreadFactory threadFactory = r -> {
            Thread t = new Thread(r, "kmc-redis-delay-queue-thread-" + counter.getAndIncrement());
            t.setDaemon(false);
            return t;
        };
        executor = new ThreadPoolExecutor(5, 10, 0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(), threadFactory);
    }

    private final RedisDelayQueue redisDelayQueue;

    public RedisDelayQueueRunner(RedisDelayQueue redisDelayQueue) {
        this.redisDelayQueue = redisDelayQueue;
    }

    @Override
    public void run(String... args) {
        RedisDelayQueueEnum[] queueEnums = RedisDelayQueueEnum.values();
        for (RedisDelayQueueEnum queueEnum : queueEnums) {
            executor.submit(() -> {
                try {
                    while (true) {
                        Object value = redisDelayQueue.getDelayQueue(queueEnum.getCode());
                        if (Objects.isNull(value)) {
                            try {
                                Thread.sleep(1000);
                                continue;
                            } catch (InterruptedException e) {
                                LogUtils.error(e);
                            }
                        }

                        RedisDelayQueueHandle redisDelayQueueHandle =
                                ContextUtils.getBean(RedisDelayQueueHandle.class, queueEnum.getBeanId(), true);

                        if (Objects.nonNull(redisDelayQueueHandle)) {
                            executor.submit(() -> redisDelayQueueHandle.execute(value));
                            LogUtils.info("RedisDelayQueueRunner run success");
                        }
                    }
                } catch (InterruptedException e) {
                    LogUtils.error("(Redis延迟队列异常中断) {}", e.getMessage());
                }
            });
        }
        LogUtils.info("(Redis延迟队列启动成功)");
    }
}
