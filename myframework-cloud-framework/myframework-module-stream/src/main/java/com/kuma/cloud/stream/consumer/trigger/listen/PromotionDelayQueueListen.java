/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.json.JSONUtil
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  org.springframework.boot.ApplicationArguments
 *  org.springframework.stereotype.Component
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

@Component
public class PromotionDelayQueueListen
extends AbstractDelayQueueListen {
    private final TimeTrigger timeTrigger;

    public PromotionDelayQueueListen(RedisRepository redisRepository, TimeTrigger timeTrigger) {
        super(redisRepository);
        this.timeTrigger = timeTrigger;
    }

    @Override
    public void invoke(String jobId) {
        this.timeTrigger.execute((TimeTriggerMsg)JSONUtil.toBean((String)jobId, TimeTriggerMsg.class));
    }

    @Override
    public String setDelayQueueName() {
        return DelayQueueEnums.PROMOTION.name();
    }

    public void run(ApplicationArguments args) throws Exception {
        this.init();
    }
}

