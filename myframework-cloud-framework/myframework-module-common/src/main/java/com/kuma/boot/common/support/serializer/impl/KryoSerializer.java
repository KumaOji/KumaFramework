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

package com.kuma.boot.common.support.serializer.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.kuma.boot.common.support.serializer.Serializer;
import com.kuma.boot.common.support.serializer.SerializerConstants;
import java.io.ByteArrayOutputStream;
import org.objenesis.strategy.StdInstantiatorStrategy;

/** kryo 序列化 */
public class KryoSerializer implements Serializer {

    public static final ThreadLocal<Kryo> kryos =
            ThreadLocal.withInitial(
                    () -> {
                        Kryo kryo = new Kryo();
                        kryo.setInstantiatorStrategy(
                                new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
                        return kryo;
                    });

    @Override
    public String name() {
        return SerializerConstants.KRYO;
    }

    @Override
    public byte[] serialize(Object data) {
        if (data == null) {
            return new byte[0];
        }
        Kryo kryo = kryos.get();
        kryo.setReferences(false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        kryo.writeClassAndObject(output, data);
        output.flush();
        return baos.toByteArray();
    }

    @Override
    public Object deserialize(byte[] bytes, ClassLoader classLoader) {
        if (bytes == null) {
            return null;
        }

        Kryo kryo = kryos.get();
        kryo.setClassLoader(classLoader);
        kryo.setReferences(false);
        Input input = new Input(bytes);
        return kryo.readClassAndObject(input);
    }
}
