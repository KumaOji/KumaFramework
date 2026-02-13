/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ConfigurableObjectInputStream
 */
package com.kuma.boot.common.support.serializer.impl;

import com.kuma.boot.common.support.serializer.Serializer;
import com.kuma.boot.common.support.serializer.impl.JdkSerializer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.ConfigurableObjectInputStream;

public class JdkSerializationRedisSerializer
implements Serializer {
    private final JdkSerializer jdkSerializer;

    public JdkSerializationRedisSerializer(JdkSerializer jdkSerializer) {
        this.jdkSerializer = jdkSerializer;
    }

    @Override
    public String name() {
        return "dubboJdk";
    }

    @Override
    public byte[] serialize(Object data) throws IOException {
        byte[] serialize = this.jdkSerializer.serialize(data);
        return this.jdkSerializer.serialize(serialize);
    }

    @Override
    public Object deserialize(byte[] bytes, ClassLoader classLoader) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
        ConfigurableObjectInputStream objectInputStream = new ConfigurableObjectInputStream((InputStream)byteStream, classLoader);
        Object object = objectInputStream.readObject();
        return this.jdkSerializer.deserialize((byte[])object, classLoader);
    }
}

