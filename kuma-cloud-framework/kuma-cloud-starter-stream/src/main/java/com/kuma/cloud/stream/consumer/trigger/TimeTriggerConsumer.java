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

import cn.hutool.json.JSONUtil;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.core.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.cloud.stream.framework.trigger.model.TimeTriggerMsg;
import com.kuma.cloud.stream.framework.trigger.util.DelayQueueTools;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/** 事件触发消费者 */
@Component
@RocketMQMessageListener(
        topic = "${kuma.data.rocketmq.promotion-topic}",
        consumerGroup = "${kuma.data.rocketmq.promotion-group}")
public class TimeTriggerConsumer implements RocketMQListener<TimeTriggerMsg> {

    private final RedisRepository redisRepository;

    public TimeTriggerConsumer(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Override
    public void onMessage(TimeTriggerMsg timeTriggerMsg) {
        try {
            String key = DelayQueueTools.generateKey(
                    timeTriggerMsg.getTriggerExecutor(),
                    timeTriggerMsg.getTriggerTime(),
                    timeTriggerMsg.getUniqueKey());

            if (redisRepository.get(key) == null) {
                LogUtils.info(
                        "执行器执行被取消：{} | 任务标识：{}", timeTriggerMsg.getTriggerExecutor(), timeTriggerMsg.getUniqueKey());
                return;
            }

            LogUtils.info("执行器执行：" + timeTriggerMsg.getTriggerExecutor());
            LogUtils.info("执行器参数：" + JSONUtil.toJsonStr(timeTriggerMsg.getParam()));

            redisRepository.del(key);

            TimeTriggerExecutor executor =
                    (TimeTriggerExecutor) ContextUtils.getBean(timeTriggerMsg.getTriggerExecutor(), true);
            executor.execute(timeTriggerMsg.getParam());
        } catch (Exception e) {
            LogUtils.error("mq延时任务异常", e);
        }
    }
}
