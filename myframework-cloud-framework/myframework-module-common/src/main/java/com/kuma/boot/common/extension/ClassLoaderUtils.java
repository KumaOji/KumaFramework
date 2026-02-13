/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.extension;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClassLoaderUtils {
    private static final ClassLoaderWeakCache<Constructor<?>> CONSTRUCTOR_CACHE = new ClassLoaderWeakCache();
    private static final ClassLoaderWeakCache<Class<?>> CLASS_CACHE = new ClassLoaderWeakCache();
    private static final boolean CLASS_CACHE_DISABLED = Boolean.getBoolean("classloading.cache.disabled");
    private static final Map<String, Class<?>> PRIMITIVE_CLASSES;
    private static final int MAX_PRIM_CLASS_NAME_LENGTH = 7;

    public static <T> T newInstance(ClassLoader classLoaderHint, String className) throws Exception {
        Constructor<?> constructor;
        Class<?> primitiveClass = ClassLoaderUtils.tryPrimitiveClass(className);
        if (primitiveClass != null) {
            return (T)primitiveClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        ClassLoader classLoader = classLoaderHint;
        if (classLoader == null) {
            classLoader = ClassLoaderUtils.getClassLoader();
        }
        if (classLoader != null && (constructor = CONSTRUCTOR_CACHE.get(classLoader, className)) != null) {
            return (T)constructor.newInstance(new Object[0]);
        }
        return ClassLoaderUtils.newInstance0(classLoader, className);
    }

    private static <T> T newInstance0(ClassLoader classLoader, String className) throws Exception {
        Class<?> klass = classLoader == null ? Class.forName(className) : ClassLoaderUtils.tryLoadClass(className, classLoader);
        Constructor<?> constructor = klass.getDeclaredConstructor(new Class[0]);
        if (!constructor.canAccess(null)) {
            constructor.setAccessible(true);
        }
        if (classLoader != null) {
            CONSTRUCTOR_CACHE.put(classLoader, className, constructor);
        }
        return (T)constructor.newInstance(new Object[0]);
    }

    public static ClassLoader getClassLoader() {
        return ClassLoaderUtils.getClassLoader(ClassLoaderUtils.class);
    }

    public static ClassLoader getClassLoader(Class<?> clazz) {
        ClassLoader classLoader = null;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (classLoader == null && (classLoader = clazz.getClassLoader()) == null) {
            try {
                classLoader = ClassLoader.getSystemClassLoader();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return classLoader;
    }

    public static Class<?> loadClass(String className) throws ClassNotFoundException {
        return ClassLoaderUtils.loadClass(ClassLoaderUtils.getClassLoader(), className);
    }

    public static Class<?> loadClass(ClassLoader classLoaderHint, String className) throws ClassNotFoundException {
        Class<?> primitiveClass = ClassLoaderUtils.tryPrimitiveClass(className);
        if (primitiveClass != null) {
            return primitiveClass;
        }
        ClassLoader classLoader = classLoaderHint;
        if (classLoader != null) {
            try {
                return ClassLoaderUtils.tryLoadClass(className, classLoader);
            }
            catch (ClassNotFoundException ignore) {
                classLoader = ClassLoaderUtils.getClassLoader();
            }
        }
        if (classLoader != null) {
            return ClassLoaderUtils.tryLoadClass(className, classLoader);
        }
        return Class.forName(className);
    }

    private static Class<?> tryLoadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> clazz;
        if (!CLASS_CACHE_DISABLED && (clazz = CLASS_CACHE.get(classLoader, className)) != null) {
            return clazz;
        }
        clazz = className.startsWith("[") ? Class.forName(className, false, classLoader) : classLoader.loadClass(className);
        if (!CLASS_CACHE_DISABLED) {
            CLASS_CACHE.put(classLoader, className, clazz);
        }
        return clazz;
    }

    private static Class<?> tryPrimitiveClass(String className) {
        if (className.length() <= 7 && Character.isLowerCase(className.charAt(0))) {
            return PRIMITIVE_CLASSES.get(className);
        }
        return null;
    }

    static {
        HashMap<String, Class<Object>> primitives = new HashMap<String, Class<Object>>(10, 1.0f);
        primitives.put("boolean", Boolean.TYPE);
        primitives.put("byte", Byte.TYPE);
        primitives.put("int", Integer.TYPE);
        primitives.put("long", Long.TYPE);
        primitives.put("short", Short.TYPE);
        primitives.put("float", Float.TYPE);
        primitives.put("double", Double.TYPE);
        primitives.put("char", Character.TYPE);
        primitives.put("void", Void.TYPE);
        PRIMITIVE_CLASSES = Collections.unmodifiableMap(primitives);
    }

    private static final class ClassLoaderWeakCache<V> {
        private final ConcurrentMap<ClassLoader, ConcurrentMap<String, WeakReference<V>>> cache = new ConcurrentHashMap<ClassLoader, ConcurrentMap<String, WeakReference<V>>>(16);

        private ClassLoaderWeakCache() {
        }

        private void put(ClassLoader classLoader, String className, V value) {
            ConcurrentMap old;
            ClassLoader cl = classLoader == null ? ClassLoaderUtils.class.getClassLoader() : classLoader;
            ConcurrentMap<String, WeakReference<V>> innerCache = (ConcurrentHashMap<String, WeakReference<V>>)this.cache.get(cl);
            if (innerCache == null && (old = (ConcurrentMap)this.cache.putIfAbsent(cl, innerCache = new ConcurrentHashMap<String, WeakReference<V>>(100))) != null) {
                innerCache = old;
            }
            innerCache.put(className, new WeakReference<V>(value));
        }

        public V get(ClassLoader classloader, String className) {
            V value;
            ConcurrentMap innerCache = (ConcurrentMap)this.cache.get(classloader);
            if (innerCache == null) {
                return null;
            }
            WeakReference reference = (WeakReference)innerCache.get(className);
            V v = value = reference == null ? null : (V)reference.get();
            if (reference != null && value == null) {
                innerCache.remove(className);
            }
            return value;
        }
    }
}

