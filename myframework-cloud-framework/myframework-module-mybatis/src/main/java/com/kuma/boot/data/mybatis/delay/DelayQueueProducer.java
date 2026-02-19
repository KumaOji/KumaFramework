/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.delay;

import java.util.concurrent.DelayQueue;
import org.springframework.stereotype.Component;

/**
 * 延迟消息生产者
 */
@Component
public class DelayQueueProducer {

    /**
     * 创建延迟消息队列
     */
    private static final DelayQueue<DelayMessage> DELAY_QUEUE = new DelayQueue<>();

    /**
     * 消息入队列
     *
     * @param delayMessage 消息内容
     * @return 成功:{@code true}, 失败:{@code false}
     */
    public boolean offer(DelayMessage delayMessage) {
        return DELAY_QUEUE.offer(delayMessage);
    }

    /**
     * 获取延迟消息队列
     *
     * @return {@link DelayQueue<DelayMessage>}
     */
    public DelayQueue<DelayMessage> obtainQueue() {
        return DELAY_QUEUE;
    }
}
