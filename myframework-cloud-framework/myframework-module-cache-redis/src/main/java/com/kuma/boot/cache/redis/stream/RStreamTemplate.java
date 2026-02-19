/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 *  org.springframework.data.redis.connection.RedisStreamCommands$XAddOptions
 *  org.springframework.data.redis.connection.stream.MapRecord
 *  org.springframework.data.redis.connection.stream.ObjectRecord
 *  org.springframework.data.redis.connection.stream.Record
 *  org.springframework.data.redis.connection.stream.RecordId
 */
package com.kuma.boot.cache.redis.stream;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.connection.stream.RecordId;

public interface RStreamTemplate {
    public static final String OBJECT_PAYLOAD_KEY = "@payload";

    default public RecordId send(String name, Object value) {
        return this.send((Record<String, ?>)ObjectRecord.create((Object)name, (Object)value));
    }

    default public RecordId send(String name, String key, Object value) {
        return this.send(name, Collections.singletonMap(key, value));
    }

    default public RecordId send(String name, String key, byte[] data) {
        return this.send(name, key, data, RedisStreamCommands.XAddOptions.none());
    }

    default public RecordId send(String name, String key, byte[] data, long maxLen) {
        return this.send(name, key, data, RedisStreamCommands.XAddOptions.maxlen((long)maxLen));
    }

    public RecordId send(String var1, String var2, byte[] var3, RedisStreamCommands.XAddOptions var4);

    default public <T> RecordId send(String name, String key, T data, Function<T, byte[]> mapper) {
        return this.send(name, key, mapper.apply(data));
    }

    default public RecordId send(String name, Map<String, Object> messages) {
        return this.send((Record<String, ?>)MapRecord.create((Object)name, messages));
    }

    public RecordId send(Record<String, ?> var1);

    public @Nullable Long delete(String var1, String ... var2);

    public @Nullable Long delete(String var1, RecordId ... var2);

    default public @Nullable Long delete(Record<String, ?> record) {
        return this.delete((String)record.getStream(), record.getId());
    }

    default public @Nullable Long trim(String name, long count) {
        return this.trim(name, count, false);
    }

    public @Nullable Long trim(String var1, long var2, boolean var4);

    public @Nullable Long acknowledge(String var1, String var2, String ... var3);

    public @Nullable Long acknowledge(String var1, String var2, RecordId ... var3);

    public @Nullable Long acknowledge(String var1, Record<String, ?> var2);
}

