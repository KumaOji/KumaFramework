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

package com.kuma.cloud.stream.consumer.trigger.listen;

import cn.hutool.json.JSONUtil;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.cloud.stream.consumer.trigger.AbstractDelayQueueListen;
import com.kuma.cloud.stream.framework.trigger.enums.DelayQueueEnums;
import com.kuma.cloud.stream.framework.trigger.interfaces.TimeTrigger;
import com.kuma.cloud.stream.framework.trigger.model.TimeTriggerMsg;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

/** PromotionTimeTriggerListen */
@Component
public class PromotionDelayQueueListen extends AbstractDelayQueueListen {

    private final TimeTrigger timeTrigger;

    public PromotionDelayQueueListen(RedisRepository redisRepository, TimeTrigger timeTrigger) {
        super(redisRepository);
        this.timeTrigger = timeTrigger;
    }

    @Override
    public void invoke(String jobId) {
        timeTrigger.execute(JSONUtil.toBean(jobId, TimeTriggerMsg.class));
    }

    @Override
    public String setDelayQueueName() {
        return DelayQueueEnums.PROMOTION.name();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.init();
    }
}
