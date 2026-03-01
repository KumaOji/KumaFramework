/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.json.JSONUtil
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  com.kuma.boot.common.utils.context.ContextUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.apache.rocketmq.spring.annotation.RocketMQMessageListener
 *  org.apache.rocketmq.spring.core.RocketMQListener
 *  org.springframework.stereotype.Component
 */
package com.kuma.cloud.stream.consumer.trigger;

import cn.hutool.json.JSONUtil;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.cloud.stream.framework.trigger.model.TimeTriggerMsg;
import com.kuma.cloud.stream.framework.trigger.util.DelayQueueTools;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic="${kuma.data.rocketmq.promotion-topic}", consumerGroup="${kuma.data.rocketmq.promotion-group}")
public class TimeTriggerConsumer
implements RocketMQListener<TimeTriggerMsg> {
    private final RedisRepository redisRepository;

    public TimeTriggerConsumer(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    public void onMessage(TimeTriggerMsg timeTriggerMsg) {
        try {
            String key = DelayQueueTools.generateKey(timeTriggerMsg.getTriggerExecutor(), timeTriggerMsg.getTriggerTime(), timeTriggerMsg.getUniqueKey());
            if (this.redisRepository.get(key) == null) {
                LogUtils.info((String)"\u6267\u884c\u5668\u6267\u884c\u88ab\u53d6\u6d88\uff1a{} | \u4efb\u52a1\u6807\u8bc6\uff1a{}", (Object[])new Object[]{timeTriggerMsg.getTriggerExecutor(), timeTriggerMsg.getUniqueKey()});
                return;
            }
            LogUtils.info((String)("\u6267\u884c\u5668\u6267\u884c\uff1a" + timeTriggerMsg.getTriggerExecutor()), (Object[])new Object[0]);
            LogUtils.info((String)("\u6267\u884c\u5668\u53c2\u6570\uff1a" + JSONUtil.toJsonStr((Object)timeTriggerMsg.getParam())), (Object[])new Object[0]);
            this.redisRepository.del(new String[]{key});
            TimeTriggerExecutor executor = (TimeTriggerExecutor)ContextUtils.getBean((String)timeTriggerMsg.getTriggerExecutor(), (boolean)true);
            executor.execute(timeTriggerMsg.getParam());
        }
        catch (Exception e) {
            LogUtils.error((String)"mq\u5ef6\u65f6\u4efb\u52a1\u5f02\u5e38", (Object[])new Object[]{e});
        }
    }
}

