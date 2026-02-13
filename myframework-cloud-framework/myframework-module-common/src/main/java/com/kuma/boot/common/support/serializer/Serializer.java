/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.serializer;

import com.kuma.boot.common.extension.SPI;
import java.io.IOException;

@SPI(value="Serializer")
public interface Serializer {
    public String name();

    public byte[] serialize(Object var1) throws IOException;

    public Object deserialize(byte[] var1, ClassLoader var2) throws IOException, ClassNotFoundException;
}

