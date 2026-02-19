/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.thread.ExecutorBuilder
 *  cn.hutool.core.thread.ThreadFactoryBuilder
 *  cn.hutool.core.thread.ThreadUtil
 *  com.kuma.boot.common.utils.context.ContextUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.boot.CommandLineRunner
 */
package com.kuma.boot.cache.redis.redisson;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.thread.ThreadUtil;
import com.kuma.boot.cache.redis.redisson.handle.RedisDelayQueueHandle;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import org.springframework.boot.CommandLineRunner;

public class RedisDelayQueueRunner
implements CommandLineRunner {
    private static ExecutorService executor;
    private final RedisDelayQueue redisDelayQueue;

    public static synchronized void init() {
        if (null != executor) {
            executor.shutdownNow();
        }
        executor = ExecutorBuilder.create().setCorePoolSize(5).setMaxPoolSize(10).setKeepAliveTime(0L).setThreadFactory(r -> ThreadFactoryBuilder.create().setNamePrefix("kmc-redis-delay-queue-thread").setDaemon(false).build().newThread(r)).useSynchronousQueue().build();
    }

    public RedisDelayQueueRunner(RedisDelayQueue redisDelayQueue) {
        this.redisDelayQueue = redisDelayQueue;
    }

    public void run(String ... args) {
        RedisDelayQueueEnum[] queueEnums;
        for (RedisDelayQueueEnum queueEnum : queueEnums = RedisDelayQueueEnum.values()) {
            executor.submit(() -> {
                try {
                    while (true) {
                        RedisDelayQueueHandle redisDelayQueueHandle;
                        Object value;
                        if (Objects.isNull(value = this.redisDelayQueue.getDelayQueue(queueEnum.getCode()))) {
                            try {
                                Thread.sleep(1000L);
                                continue;
                            }
                            catch (InterruptedException e) {
                                LogUtils.error((Throwable)e);
                            }
                        }
                        if (!Objects.nonNull(redisDelayQueueHandle = (RedisDelayQueueHandle)ContextUtils.getBean(RedisDelayQueueHandle.class, (String)queueEnum.getBeanId(), (boolean)true))) continue;
                        ThreadUtil.execute(() -> redisDelayQueueHandle.execute(value));
                        LogUtils.info((String)"RedisDelayQueueRunner run success", (Object[])new Object[0]);
                    }
                }
                catch (InterruptedException e) {
                    LogUtils.error((String)"(Redis\u5ef6\u8fdf\u961f\u5217\u5f02\u5e38\u4e2d\u65ad) {}", (Object[])new Object[]{e.getMessage()});
                    return;
                }
            });
        }
        LogUtils.info((String)"(Redis\u5ef6\u8fdf\u961f\u5217\u542f\u52a8\u6210\u529f)", (Object[])new Object[0]);
    }

    static {
        RedisDelayQueueRunner.init();
    }
}

