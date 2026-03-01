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

package com.kuma.boot.data.mybatis.delay;

import com.alibaba.fastjson2.JSON;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 延迟消息超时事件监听器
 */
@Component
public class DelayMessageListener {

    /** 延迟消息生产者 */
    @Autowired private com.kuma.boot.data.mybatis.delay.DelayQueueProducer producer;

    /** 创建超时消息处理服务接口 */
    @Autowired private AppDelayMessageService service;

    /**
     * 事件监听处理方法
     * @param event {@link com.kuma.boot.data.mybatis.delay.InvokeTimeoutEvent}
     */
    @EventListener
    public void onApplicationEvent(com.kuma.boot.data.mybatis.delay.InvokeTimeoutEvent event) {
        // 监听延迟消息触发事件
        AppDelayMessage source = (AppDelayMessage) event.getSource();
        if (service.save(source)) {
            // 转换为毫秒
            long ttl = 2 * 60 * 60 * 1000;
            if (Objects.nonNull(source.getTtl())) {
                ttl = source.getTtl() * 60 * 60 * 1000;
            }
            producer.offer(new DelayMessage(JSON.toJSONString(source), ttl));
        }
    }
}
