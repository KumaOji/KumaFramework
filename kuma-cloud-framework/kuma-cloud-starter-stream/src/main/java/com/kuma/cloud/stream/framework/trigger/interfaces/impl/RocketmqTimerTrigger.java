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
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/** 延时任务实现 */
@Component
public class RocketmqTimerTrigger implements TimeTrigger {

    private final RocketMQTemplate rocketMQTemplate;

    private final RedisRepository redisRepository;

    private final List<DelayQueueMachine> delayQueueMachines;

    public RocketmqTimerTrigger(RocketMQTemplate rocketMQTemplate, RedisRepository redisRepository,
                                List<DelayQueueMachine> delayQueueMachines) {
        this.rocketMQTemplate = rocketMQTemplate;
        this.redisRepository = redisRepository;
        this.delayQueueMachines = delayQueueMachines;
    }

    @Override
    public void addDelay(TimeTriggerMsg timeTriggerMsg) {
        // 执行器唯一key
        String uniqueKey = timeTriggerMsg.getUniqueKey();
        if (StringUtils.isEmpty(uniqueKey)) {
            uniqueKey = RandomUtils.randomString(10);
        }

        // 执行任务key
        String generateKey = DelayQueueTools.generateKey(
                timeTriggerMsg.getTriggerExecutor(), timeTriggerMsg.getTriggerTime(), uniqueKey);
        redisRepository.set(generateKey, 1);

        for (DelayQueueMachine delayQueueMachine : delayQueueMachines) {
            if (delayQueueMachine.getDelayQueueName().equals(timeTriggerMsg.getType())) {
                // 设置延时任务
                if (Boolean.TRUE.equals(delayQueueMachine.addJob(
                        JSONUtil.toJsonStr(timeTriggerMsg), timeTriggerMsg.getTriggerTime()))) {
                    LogUtils.info("延时任务标识： {}", generateKey);
                    LogUtils.info("定时执行在【"
                            + DateUtils.toString(timeTriggerMsg.getTriggerTime(), "yyyy-MM-dd HH:mm:ss")
                            + "】，消费【"
                            + timeTriggerMsg.getParam().toString()
                            + "】");
                } else {
                    LogUtils.error("延时任务添加失败:{}", timeTriggerMsg);
                }
            }
        }
    }

    @Override
    public void execute(TimeTriggerMsg timeTriggerMsg) {
        this.addExecute(
                timeTriggerMsg.getTriggerExecutor(),
                timeTriggerMsg.getParam(),
                timeTriggerMsg.getTriggerTime(),
                timeTriggerMsg.getUniqueKey(),
                timeTriggerMsg.getTopic());
    }

    /**
     * 将任务添加到mq，mq异步队列执行。
     *
     * <p>本系统中redis相当于延时任务吊起机制，而mq才是实际的业务消费，执行任务的存在
     *
     * @param executorName 执行器beanId
     * @param param 执行参数
     * @param triggerTime 执行时间 时间戳 秒为单位
     * @param uniqueKey 如果是一个 需要有 修改/取消 延时任务功能的延时任务，<br>
     *     请填写此参数，作为后续删除，修改做为唯一凭证 <br>
     *     建议参数为：COUPON_{ACTIVITY_ID} 例如 coupon_123<br>
     *     业务内全局唯一
     * @param topic rocketmq topic
     */
    private void addExecute(String executorName, Object param, Long triggerTime, String uniqueKey, String topic) {

        TimeTriggerMsg timeTriggerMsg = new TimeTriggerMsg(executorName, triggerTime, param, uniqueKey, topic);
        Message<TimeTriggerMsg> message =
                MessageBuilder.withPayload(timeTriggerMsg).build();
        LogUtils.info("延时任务发送信息：{}", message);
        this.rocketMQTemplate.asyncSend(topic, message, RocketmqSendCallbackBuilder.commonCallback());
    }

    @Override
    public void edit(
            String executorName,
            Object param,
            Long oldTriggerTime,
            Long triggerTime,
            String uniqueKey,
            int delayTime,
            String topic) {
        this.delete(executorName, oldTriggerTime, uniqueKey, topic);
        this.addDelay(new TimeTriggerMsg(executorName, triggerTime, param, uniqueKey, topic));
    }

    @Override
    public void delete(String executorName, Long triggerTime, String uniqueKey, String topic) {
        String generateKey = DelayQueueTools.generateKey(executorName, triggerTime, uniqueKey);
        LogUtils.info("删除延时任务{}", generateKey);
        redisRepository.del(generateKey);
    }
}
