/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.data.redis.connection.RedisStreamCommands
 *  org.springframework.data.redis.connection.RedisStreamCommands$XAddOptions
 *  org.springframework.data.redis.connection.stream.MapRecord
 *  org.springframework.data.redis.connection.stream.Record
 *  org.springframework.data.redis.connection.stream.RecordId
 *  org.springframework.data.redis.core.RedisTemplate
 *  org.springframework.data.redis.core.StreamOperations
 *  org.springframework.data.redis.core.convert.RedisCustomConversions
 *  org.springframework.data.redis.serializer.StringRedisSerializer
 */
package com.kuma.boot.cache.redis.stream;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.convert.RedisCustomConversions;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class DefaultRStreamTemplate
implements RStreamTemplate {
    private static final RedisCustomConversions CUSTOM_CONVERSIONS = new RedisCustomConversions();
    private final RedisTemplate<String, Object> redisTemplate;
    private final StreamOperations<String, String, Object> streamOperations;

    public DefaultRStreamTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.streamOperations = redisTemplate.opsForStream();
    }

    @Override
    public RecordId send(Record<String, ?> record) {
        if (record instanceof MapRecord) {
            return this.streamOperations.add(record);
        }
        String stream = Objects.requireNonNull((String)record.getStream(), "RStreamTemplate send stream name is null.");
        Object recordValue = Objects.requireNonNull(record.getValue(), "RStreamTemplate send stream: " + stream + " value is null.");
        Class<?> valueClass = recordValue.getClass();
        if (CUSTOM_CONVERSIONS.isSimpleType(valueClass)) {
            return this.streamOperations.add(record);
        }
        HashMap<String, Object> payload = new HashMap<String, Object>();
        payload.put("@payload", recordValue);
        MapRecord mapRecord = MapRecord.create((Object)stream, payload);
        return this.streamOperations.add(mapRecord);
    }

    @Override
    public RecordId send(String name, String key, byte[] data, RedisStreamCommands.XAddOptions options) {
        StringRedisSerializer stringSerializer = StringRedisSerializer.UTF_8;
        byte[] nameBytes = Objects.requireNonNull(stringSerializer.serialize((Object)name), "redis stream name is null.");
        byte[] keyBytes = Objects.requireNonNull(stringSerializer.serialize((Object)key), "redis stream key is null.");
        Map<byte[], byte[]> mapDate = Collections.singletonMap(keyBytes, data);
        return (RecordId)this.redisTemplate.execute(redis -> {
            RedisStreamCommands streamCommands = redis.streamCommands();
            return streamCommands.xAdd(MapRecord.create((Object)nameBytes, (Map)mapDate), options);
        });
    }

    @Override
    public Long delete(String name, String ... recordIds) {
        return this.streamOperations.delete((Object)name, recordIds);
    }

    @Override
    public Long delete(String name, RecordId ... recordIds) {
        return this.streamOperations.delete((Object)name, recordIds);
    }

    @Override
    public Long trim(String name, long count, boolean approximateTrimming) {
        return this.streamOperations.trim((Object)name, count, approximateTrimming);
    }

    @Override
    public Long acknowledge(String name, String group, String ... recordIds) {
        return this.streamOperations.acknowledge((Object)name, group, recordIds);
    }

    @Override
    public Long acknowledge(String name, String group, RecordId ... recordIds) {
        return this.streamOperations.acknowledge((Object)name, group, recordIds);
    }

    @Override
    public Long acknowledge(String group, Record<String, ?> record) {
        return this.streamOperations.acknowledge(group, record);
    }
}

