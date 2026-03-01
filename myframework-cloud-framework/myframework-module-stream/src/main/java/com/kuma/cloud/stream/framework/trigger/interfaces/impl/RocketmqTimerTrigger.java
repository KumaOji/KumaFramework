/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.json.JSONUtil
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  com.kuma.boot.common.utils.common.RandomUtils
 *  com.kuma.boot.common.utils.date.DateUtils
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.apache.rocketmq.client.producer.SendCallback
 *  org.apache.rocketmq.spring.core.RocketMQTemplate
 *  org.springframework.messaging.Message
 *  org.springframework.messaging.support.MessageBuilder
 *  org.springframework.stereotype.Component
 */
package com.kuma.cloud.stream.framework.trigger.interfaces.impl;

import cn.hutool.json.JSONUtil;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.common.RandomUtils;
import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.cloud.stream.framework.rocketmq.RocketmqSendCallbackBuilder;
import com.kuma.cloud.stream.framework.trigger.delay.DelayQueueMachine;
import com.kuma.cloud.stream.framework.trigger.interfaces.TimeTrigger;
import com.kuma.cloud.stream.framework.trigger.model.TimeTriggerMsg;
import com.kuma.cloud.stream.framework.trigger.util.DelayQueueTools;
import java.util.List;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class RocketmqTimerTrigger
implements TimeTrigger {
    private final RocketMQTemplate rocketMQTemplate;
    private final RedisRepository redisRepository;
    private final List<DelayQueueMachine> delayQueueMachines;

    public RocketmqTimerTrigger(RocketMQTemplate rocketMQTemplate, RedisRepository redisRepository, List<DelayQueueMachine> delayQueueMachines) {
        this.rocketMQTemplate = rocketMQTemplate;
        this.redisRepository = redisRepository;
        this.delayQueueMachines = delayQueueMachines;
    }

    @Override
    public void addDelay(TimeTriggerMsg timeTriggerMsg) {
        String uniqueKey = timeTriggerMsg.getUniqueKey();
        if (StringUtils.isEmpty((String)uniqueKey)) {
            uniqueKey = RandomUtils.randomString((int)10);
        }
        String generateKey = DelayQueueTools.generateKey(timeTriggerMsg.getTriggerExecutor(), timeTriggerMsg.getTriggerTime(), uniqueKey);
        this.redisRepository.set(generateKey, (Object)1);
        for (DelayQueueMachine delayQueueMachine : this.delayQueueMachines) {
            if (!delayQueueMachine.getDelayQueueName().equals(timeTriggerMsg.getType())) continue;
            if (Boolean.TRUE.equals(delayQueueMachine.addJob(JSONUtil.toJsonStr((Object)timeTriggerMsg), timeTriggerMsg.getTriggerTime()))) {
                LogUtils.info((String)"\u5ef6\u65f6\u4efb\u52a1\u6807\u8bc6\uff1a {}", (Object[])new Object[]{generateKey});
                LogUtils.info((String)("\u5b9a\u65f6\u6267\u884c\u5728\u3010" + DateUtils.toString((Long)timeTriggerMsg.getTriggerTime(), (String)"yyyy-MM-dd HH:mm:ss") + "\u3011\uff0c\u6d88\u8d39\u3010" + timeTriggerMsg.getParam().toString() + "\u3011"), (Object[])new Object[0]);
                continue;
            }
            LogUtils.error((String)"\u5ef6\u65f6\u4efb\u52a1\u6dfb\u52a0\u5931\u8d25:{}", (Object[])new Object[]{timeTriggerMsg});
        }
    }

    @Override
    public void execute(TimeTriggerMsg timeTriggerMsg) {
        this.addExecute(timeTriggerMsg.getTriggerExecutor(), timeTriggerMsg.getParam(), timeTriggerMsg.getTriggerTime(), timeTriggerMsg.getUniqueKey(), timeTriggerMsg.getTopic());
    }

    private void addExecute(String executorName, Object param, Long triggerTime, String uniqueKey, String topic) {
        TimeTriggerMsg timeTriggerMsg = new TimeTriggerMsg(executorName, triggerTime, param, uniqueKey, topic);
        Message message = MessageBuilder.withPayload((Object)timeTriggerMsg).build();
        LogUtils.info((String)"\u5ef6\u65f6\u4efb\u52a1\u53d1\u9001\u4fe1\u606f\uff1a{}", (Object[])new Object[]{message});
        this.rocketMQTemplate.asyncSend(topic, message, (SendCallback)RocketmqSendCallbackBuilder.commonCallback());
    }

    @Override
    public void edit(String executorName, Object param, Long oldTriggerTime, Long triggerTime, String uniqueKey, int delayTime, String topic) {
        this.delete(executorName, oldTriggerTime, uniqueKey, topic);
        this.addDelay(new TimeTriggerMsg(executorName, triggerTime, param, uniqueKey, topic));
    }

    @Override
    public void delete(String executorName, Long triggerTime, String uniqueKey, String topic) {
        String generateKey = DelayQueueTools.generateKey(executorName, triggerTime, uniqueKey);
        LogUtils.info((String)"\u5220\u9664\u5ef6\u65f6\u4efb\u52a1{}", (Object[])new Object[]{generateKey});
        this.redisRepository.del(new String[]{generateKey});
    }
}

