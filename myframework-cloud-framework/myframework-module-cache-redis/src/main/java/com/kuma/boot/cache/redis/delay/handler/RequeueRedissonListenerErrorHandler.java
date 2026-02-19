/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.messaging.Message
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.cache.redis.delay.handler;

import com.kuma.boot.cache.redis.delay.config.RedissonTemplate;
import com.kuma.boot.cache.redis.delay.message.RedissonMessage;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class RequeueRedissonListenerErrorHandler
implements RedissonListenerErrorHandler {
    private static final long DEFAULT_MAX_REQUEUE_TIMES = 1000L;
    private final RedissonTemplate redissonTemplate;
    private final long maxRequeueTimes;
    private final RequeueDelayStrategy delayStrategy;

    public RequeueRedissonListenerErrorHandler(RedissonTemplate redissonTemplate) {
        this(redissonTemplate, 1000L);
    }

    public RequeueRedissonListenerErrorHandler(RedissonTemplate redissonTemplate, long maxRequeueTimes) {
        this(redissonTemplate, maxRequeueTimes, new DefaultRequeueDelayStrategy());
    }

    public RequeueRedissonListenerErrorHandler(RedissonTemplate redissonTemplate, long maxRequeueTimes, RequeueDelayStrategy delayStrategy) {
        Assert.notNull((Object)redissonTemplate, (String)"redissonTemplate must not be null");
        Assert.isTrue((maxRequeueTimes > 0L ? 1 : 0) != 0, (String)"maxRequeueTimes must be positive");
        Assert.notNull((Object)delayStrategy, (String)"requeueDelayStrategy must not be null");
        this.redissonTemplate = redissonTemplate;
        this.maxRequeueTimes = maxRequeueTimes;
        this.delayStrategy = delayStrategy;
    }

    @Override
    public void handleError(RedissonMessage message, Message<?> messagingMessage, Throwable throwable) {
        Object payload = messagingMessage.getPayload();
        if (message == null && payload instanceof List) {
            List payloadList = (List)payload;
            List batchHeaders = (List)messagingMessage.getHeaders().get((Object)"batch_converted_headers");
            for (int i = 0; i < payloadList.size(); ++i) {
                Object payloadToRequeue = payloadList.get(i);
                Map rawHeaders = (Map)batchHeaders.get(i);
                this.requeue(payloadToRequeue, new HashMap<String, Object>(rawHeaders), throwable);
            }
            return;
        }
        this.requeue(payload, new HashMap<String, Object>((Map<String, Object>)messagingMessage.getHeaders()), throwable);
    }

    private void requeue(Object payload, Map<String, Object> headers, Throwable throwable) {
        String queueName = (String)headers.get("delivery_queue_name");
        if (!StringUtils.hasText((String)queueName)) {
            LogUtils.warn((String)"message [{}] delivery queue name is empty, abandon it", (Object[])new Object[]{JacksonUtils.toJSONString((Object)payload), throwable});
            return;
        }
        Long requeueTimes = RequeueRedissonListenerErrorHandler.getLongVal(headers.get("requeue_times"));
        if (requeueTimes >= this.maxRequeueTimes) {
            LogUtils.warn((String)"message [{}] reach the max requeue times, abandon it", (Object[])new Object[]{JacksonUtils.toJSONString((Object)payload), throwable});
            return;
        }
        requeueTimes = requeueTimes + 1L;
        headers.put("requeue_times", requeueTimes);
        long delay = this.delayStrategy.getDelay(payload, headers);
        if (delay > 0L) {
            this.redissonTemplate.sendWithDelay(queueName, payload, headers, delay);
            return;
        }
        this.redissonTemplate.send(queueName, payload, headers);
    }

    private static Long getLongVal(Object target) {
        if (target == null) {
            return 0L;
        }
        if (target instanceof Number) {
            return ((Number)target).longValue();
        }
        return 0L;
    }

    private static class DefaultRequeueDelayStrategy
    implements RequeueDelayStrategy {
        private DefaultRequeueDelayStrategy() {
        }

        @Override
        public long getDelay(Object payload, Map<String, Object> headers) {
            Object delay = headers.get("expected_delay_millis");
            return RequeueRedissonListenerErrorHandler.getLongVal(delay);
        }
    }

    public static interface RequeueDelayStrategy {
        public long getDelay(Object var1, Map<String, Object> var2);
    }
}

