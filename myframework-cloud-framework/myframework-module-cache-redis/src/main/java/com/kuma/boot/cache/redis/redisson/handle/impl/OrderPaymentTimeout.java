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

package com.kuma.boot.cache.redis.redisson.handle.impl;

import com.kuma.boot.cache.redis.redisson.handle.RedisDelayQueueHandle;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Map;
import org.springframework.stereotype.Component;

/** 订单支付超时处理类 */
@Component
public class OrderPaymentTimeout implements RedisDelayQueueHandle {

    @Override
    public void execute(Object obj) {
        if (obj instanceof Map map) {
            // TODO 订单支付超时，自动取消订单处理业务...
            Long now = System.currentTimeMillis();
            Long timestamp = Long.valueOf(String.valueOf(map.get("timestamp")));
            Long delayTime = now - timestamp;
            Long random = Long.valueOf(String.valueOf(map.get("random")));
            Long diffTime = delayTime - random * 1000;
            LogUtils.info(
                    "(OrderPaymentTimeout) orderId：{}, 预计延迟时间：{} 秒，实际延迟时间：{} 毫秒，相差：{} 毫秒",
                    map.get("orderId"),
                    random,
                    delayTime,
                    diffTime);
        }
    }
}
