/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.data.redis.connection.Message
 *  org.springframework.data.redis.core.StringRedisTemplate
 *  org.springframework.data.redis.serializer.RedisSerializer
 */
package com.kuma.boot.cache.redis.listener;

import com.kuma.boot.common.utils.json.JacksonUtils;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

public abstract class AbstractMessageEventListener<T>
implements MessageEventListener {
    @Autowired
    protected StringRedisTemplate stringRedisTemplate;
    protected final Class<T> clz;

    protected AbstractMessageEventListener() {
        Type superClass = this.getClass().getGenericSuperclass();
        ParameterizedType type = (ParameterizedType)superClass;
        this.clz = (Class)type.getActualTypeArguments()[0];
    }

    public void onMessage(Message message, byte[] pattern) {
        byte[] channelBytes = message.getChannel();
        RedisSerializer stringSerializer = this.stringRedisTemplate.getStringSerializer();
        String channelTopic = (String)stringSerializer.deserialize(channelBytes);
        String topic = this.topic().getTopic();
        if (topic.equals(channelTopic)) {
            byte[] bodyBytes = message.getBody();
            String body = (String)stringSerializer.deserialize(bodyBytes);
            Object decodeMessage = JacksonUtils.toObject((String)body, this.clz);
            this.handleMessage(decodeMessage);
        }
    }

    protected abstract void handleMessage(T var1);
}

