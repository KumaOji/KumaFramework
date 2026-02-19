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

import com.kuma.boot.common.utils.log.LogUtils;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 封装 Redis 延迟队列工具
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:45:41
 */
public class RedisDelayQueue {

    private final RedissonClient redissonClient;

    public RedisDelayQueue(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 添加延迟队列
     *
     * @param value 队列值
     * @param delay 延迟时间
     * @param timeUnit 时间单位
     * @param queueCode 队列键
     * @since 2022-04-27 17:45:48
     */
    public <T> void addDelayQueue(T value, long delay, TimeUnit timeUnit, String queueCode) {
        try {
            RBlockingDeque<Object> blockingDeque = redissonClient.getBlockingDeque(queueCode);
            RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
            delayedQueue.offer(value, delay, timeUnit);
            LogUtils.info("添加延时队列成功，队列键：{}，队列值：{}，延迟时间：{}", queueCode, value, timeUnit.toSeconds(delay) + "秒");
        } catch (Exception e) {
            LogUtils.error("添加延时队列失败：{}", e.getMessage());
            throw new RuntimeException("添加延时队列失败");
        }
    }

    /**
     * 获取延迟队列
     *
     * @param queueCode queueCode
     * @return {@link T }
     * @since 2022-04-27 17:45:48
     */
    @SuppressWarnings("unchecked")
    public <T> T getDelayQueue(String queueCode) throws InterruptedException {
        RBlockingDeque<Map> blockingDeque = redissonClient.getBlockingDeque(queueCode);
        T value = (T) blockingDeque.take();
        return value;
    }
}
