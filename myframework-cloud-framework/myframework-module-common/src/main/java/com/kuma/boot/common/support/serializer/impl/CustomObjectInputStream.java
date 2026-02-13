/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.serializer.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class CustomObjectInputStream
extends ObjectInputStream {
    private final ClassLoader classLoader;

    public CustomObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
        super(in);
        this.classLoader = classLoader;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String name = desc.getName();
        Class<?> aClass = this.classLoader.loadClass(name);
        if (aClass != null) {
            return aClass;
        }
        return super.resolveClass(desc);
    }
}

