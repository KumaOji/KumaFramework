/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.core.serializer.support.DeserializingConverter
 *  org.springframework.core.serializer.support.SerializingConverter
 *  org.springframework.data.redis.serializer.RedisSerializer
 */
package com.kuma.boot.cache.redis.serializer;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.serializer.RedisSerializer;

public class RedisObjectSerializer
implements RedisSerializer<Object> {
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private final Converter<Object, byte[]> serializingConverter = new SerializingConverter();
    private final Converter<byte[], Object> deSerializingConverter = new DeserializingConverter();

    public byte[] serialize(Object obj) {
        if (obj == null) {
            return EMPTY_BYTE_ARRAY;
        }
        return (byte[])this.serializingConverter.convert(obj);
    }

    public Object deserialize(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        return this.deSerializingConverter.convert((Object)data);
    }
}

