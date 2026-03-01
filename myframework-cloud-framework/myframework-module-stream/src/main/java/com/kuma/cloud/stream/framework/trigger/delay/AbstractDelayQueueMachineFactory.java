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

package com.kuma.cloud.stream.framework.trigger.delay;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.log.LogUtils;

/** 延时队列工厂 */
public abstract class AbstractDelayQueueMachineFactory implements DelayQueueMachine {

    private final RedisRepository cache;

    protected AbstractDelayQueueMachineFactory(RedisRepository cache) {
        this.cache = cache;
    }

    /**
     * 插入任务id
     *
     * @param jobId 任务id(队列内唯一)
     * @param triggerTime 执行时间 时间戳（毫秒）
     * @return 是否插入成功
     */
    @Override
    public boolean addJob(String jobId, Long triggerTime) {
        // redis 中排序时间
        long delaySeconds = triggerTime / 1000;
        // 增加延时任务 参数依次为：队列名称、执行时间、任务id
        boolean result = cache.zAdd(getDelayQueueName(), jobId, delaySeconds);
        LogUtils.info("增加延时任务, 缓存key {}, 执行时间 {},任务id {}", getDelayQueueName(), DateUtils.toString(triggerTime), jobId);
        return result;
    }
}
