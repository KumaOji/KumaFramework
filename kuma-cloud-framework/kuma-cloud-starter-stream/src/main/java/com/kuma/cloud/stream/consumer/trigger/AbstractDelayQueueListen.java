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

package com.kuma.cloud.stream.consumer.trigger;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.DefaultTypedTuple;

/** 延时队列工厂 */
public abstract class AbstractDelayQueueListen implements ApplicationRunner {

    private final RedisRepository redisRepository;

    protected AbstractDelayQueueListen(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    /** 延时队列机器开始运作 */
    private void startDelayQueueMachine() {
        LogUtils.info("延时队列机器{}开始运作", setDelayQueueName());

        // 监听redis队列
        while (true) {
            try {
                // 获取当前时间的时间戳
                long now = System.currentTimeMillis() / 1000;
                // 获取当前时间前需要执行的任务列表
                Set<Object> tuples = redisRepository.zRangeByScore(setDelayQueueName(), 0, now);

                // 如果任务不为空
                if (!CollectionUtils.isEmpty(tuples)) {
                    LogUtils.info("执行任务:{}", JSONUtil.toJsonStr(tuples));

                    for (Object t : tuples) {
                        DefaultTypedTuple tuple = (DefaultTypedTuple) t;
                        String jobId = (String) tuple.getValue();
                        // 移除缓存，如果移除成功则表示当前线程处理了延时任务，则执行延时任务
                        Long num = redisRepository.zRem(setDelayQueueName(), jobId);
                        // 如果移除成功, 则执行
                        if (num > 0) {
                            ThreadUtil.execute(() -> invoke(jobId));
                        }
                    }
                }
            } catch (Exception e) {
                LogUtils.error("处理延时任务发生异常,异常原因为{}", e.getMessage(), e);
            } finally {
                // 间隔一秒钟搞一次
                try {
                    TimeUnit.SECONDS.sleep(5L);
                } catch (InterruptedException e) {
                    LogUtils.error(e);
                }
            }
        }
    }

    /**
     * 最终执行的任务方法
     *
     * @param jobId 任务id
     */
    public abstract void invoke(String jobId);

    /**
     * 要实现延时队列的名字
     *
     * @return 促销延时队列名称
     */
    public abstract String setDelayQueueName();

    /** 监听队列 */
    public void init() {
        ThreadUtil.execute(this::startDelayQueueMachine);
    }
}
