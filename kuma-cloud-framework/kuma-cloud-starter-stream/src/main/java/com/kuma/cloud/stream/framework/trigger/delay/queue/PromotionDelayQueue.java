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

package com.kuma.cloud.stream.framework.trigger.delay.queue;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.cloud.stream.framework.trigger.delay.AbstractDelayQueueMachineFactory;
import com.kuma.cloud.stream.framework.trigger.enums.DelayQueueEnums;
import org.springframework.stereotype.Component;

/** 促销延迟队列 */
@Component
public class PromotionDelayQueue extends AbstractDelayQueueMachineFactory {

    protected PromotionDelayQueue(RedisRepository cache) {
        super(cache);
    }

    @Override
    public String getDelayQueueName() {
        return DelayQueueEnums.PROMOTION.name();
    }
}
