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

package com.kuma.cloud.mq.client.consumer.support.listener;

import com.alibaba.fastjson2.JSON;
import com.kuma.cloud.mq.client.consumer.api.MqConsumerListener;
import com.kuma.cloud.mq.client.consumer.api.MqConsumerListenerContext;
import com.kuma.cloud.mq.common.dto.req.MqMessage;
import com.kuma.cloud.mq.common.resp.ConsumerStatus;
import javax.annotation.concurrent.NotThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kuma
 * @since 2024.05
 */
@NotThreadSafe
public class DefaultMqListenerService implements MqListenerService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultMqListenerService.class);

    private MqConsumerListener mqConsumerListener;

    @Override
    public void register( MqConsumerListener listener) {
        this.mqConsumerListener = listener;
    }

    @Override
    public ConsumerStatus consumer(MqMessage mqMessage, MqConsumerListenerContext context) {
        if (mqConsumerListener == null) {
            LOG.warn("当前监听类为空，直接忽略处理。message: {}", JSON.toJSON(mqMessage));
            return ConsumerStatus.SUCCESS;
        } else {
            return mqConsumerListener.consumer(mqMessage, context);
        }
    }
}
