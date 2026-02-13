/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.compiler;

import com.kuma.boot.common.support.compiler.InMemoryJavaCompiler;
import com.kuma.boot.common.support.function.CheckedFunction;
import com.kuma.boot.common.support.function.Unchecked;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import java.security.SecureClassLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ByteCodeLoader
extends SecureClassLoader {
    private static final ConcurrentMap<String, Class<?>> JAVA_FILE_OBJECT_MAP = new ConcurrentHashMap();
    private final String className;
    private final byte[] byteCode;

    public ByteCodeLoader(String className, byte[] byteCode) {
        this.className = className;
        this.byteCode = byteCode;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (!name.equals(this.className)) {
            throw new ClassNotFoundException(name);
        }
        return this.defineClass(name, this.byteCode, 0, this.byteCode.length);
    }

    public static Class<?> load(String className, byte[] byteCode) {
        CheckedFunction<String, Class> classLoadFunc = key -> new ByteCodeLoader((String)key, byteCode).loadClass(className);
        return CollectionUtils.computeIfAbsent(JAVA_FILE_OBJECT_MAP, className, Unchecked.function(classLoadFunc));
    }

    public static Class<?> load(String className, CharSequence sourceCode) {
        return ByteCodeLoader.load(className, InMemoryJavaCompiler.compile(className, sourceCode));
    }
}

