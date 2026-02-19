/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.request.altas.serializer;

public class SensitiveDataMasker {
    private final boolean enabled;

    public SensitiveDataMasker(boolean enabled) {
        this.enabled = enabled;
    }

    public void addSensitiveField(String fieldName) {
    }

    public void addSensitiveFields(String ... fieldNames) {
    }

    public Object maskSensitiveData(Object obj) {
        if (!this.enabled || obj == null) {
            return obj;
        }
        Class<?> clazz = obj.getClass();
        if (this.isPrimitiveOrWrapper(clazz) || clazz == String.class) {
            return obj;
        }
        if (this.shouldSkipClass(clazz)) {
            return "[" + clazz.getSimpleName() + "@" + Integer.toHexString(obj.hashCode()) + "]";
        }
        return "[" + clazz.getSimpleName() + "@" + Integer.toHexString(obj.hashCode()) + "]";
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == Boolean.class || clazz == Character.class || clazz == Byte.class || clazz == Short.class || clazz == Integer.class || clazz == Long.class || clazz == Float.class || clazz == Double.class;
    }

    private boolean shouldSkipClass(Class<?> clazz) {
        String className = clazz.getName();
        return className.startsWith("java.") || className.startsWith("javax.") || className.startsWith("sun.") || className.startsWith("com.sun.") || className.startsWith("org.springframework.") || className.startsWith("org.apache.") || className.startsWith("com.fasterxml.jackson.") || className.startsWith("org.apache.catalina.") || className.startsWith("org.apache.tomcat.") || className.startsWith("org.eclipse.jetty.") || className.startsWith("io.undertow.") || className.startsWith("weblogic.") || className.startsWith("com.ibm.websphere.") || className.contains("Facade") || className.contains("Wrapper") || className.contains("Proxy");
    }
}

