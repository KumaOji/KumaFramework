/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.cache.redis.redisson.handle.impl;

import com.kuma.boot.cache.redis.redisson.handle.RedisDelayQueueHandle;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class OrderTimeoutNotEvaluated
implements RedisDelayQueueHandle {
    @Override
    public void execute(Object obj) {
        if (obj instanceof Map) {
            Map map = (Map)obj;
            Long now = System.currentTimeMillis();
            Long timestamp = Long.valueOf(String.valueOf(map.get("timestamp")));
            Long delayTime = now - timestamp;
            Long random = Long.valueOf(String.valueOf(map.get("random")));
            Long diffTime = delayTime - random * 1000L;
            LogUtils.info((String)"(OrderTimeoutNotEvaluated) orderId\uff1a{}, \u9884\u8ba1\u5ef6\u8fdf\u65f6\u95f4\uff1a{} \u79d2\uff0c\u5b9e\u9645\u5ef6\u8fdf\u65f6\u95f4\uff1a{} \u6beb\u79d2\uff0c\u76f8\u5dee\uff1a{} \u6beb\u79d2", (Object[])new Object[]{map.get("orderId"), random, delayTime, diffTime});
        }
    }
}

