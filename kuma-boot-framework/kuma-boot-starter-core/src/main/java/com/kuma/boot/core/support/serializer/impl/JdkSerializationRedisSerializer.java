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

package com.kuma.boot.core.support.serializer.impl;

import com.kuma.boot.common.support.serializer.Serializer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.kuma.boot.common.support.serializer.impl.JdkSerializer;
import org.springframework.core.ConfigurableObjectInputStream;

/**
 * 这个序列化类对应 spring 的
 * org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
 */
public class JdkSerializationRedisSerializer implements Serializer {

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
        final byte[] serialize = jdkSerializer.serialize(data);
        return jdkSerializer.serialize(serialize);
    }

    @Override
    public Object deserialize(byte[] bytes, ClassLoader classLoader)
            throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream =
                new ConfigurableObjectInputStream(byteStream, classLoader);
        final Object object = objectInputStream.readObject();
        return jdkSerializer.deserialize((byte[]) object, classLoader);
    }
}
