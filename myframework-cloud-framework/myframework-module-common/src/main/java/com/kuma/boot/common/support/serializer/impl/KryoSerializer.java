/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.esotericsoftware.kryo.Kryo
 *  com.esotericsoftware.kryo.io.Input
 *  com.esotericsoftware.kryo.io.Output
 *  com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy
 *  org.objenesis.strategy.InstantiatorStrategy
 *  org.objenesis.strategy.StdInstantiatorStrategy
 */
package com.kuma.boot.common.support.serializer.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.kuma.boot.common.support.serializer.Serializer;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.objenesis.strategy.InstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;

public class KryoSerializer
implements Serializer {
    public static final ThreadLocal<Kryo> kryos = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy((InstantiatorStrategy)new DefaultInstantiatorStrategy((InstantiatorStrategy)new StdInstantiatorStrategy()));
        return kryo;
    });

    @Override
    public String name() {
        return "kryo";
    }

    @Override
    public byte[] serialize(Object data) {
        if (data == null) {
            return new byte[0];
        }
        Kryo kryo = kryos.get();
        kryo.setReferences(false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output((OutputStream)baos);
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

