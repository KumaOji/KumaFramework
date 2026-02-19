/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.request.altas.serializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultFormatterContext
implements ArgumentFormatter.FormatterContext {
    private final String methodName;
    private final String className;
    private final int maxLength;
    private final Map<String, Object> attributes;

    public DefaultFormatterContext(String methodName, String className, int maxLength) {
        this.methodName = methodName;
        this.className = className;
        this.maxLength = maxLength;
        this.attributes = new ConcurrentHashMap<String, Object>();
    }

    @Override
    public String getMethodName() {
        return this.methodName;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public int getMaxLength() {
        return this.maxLength;
    }

    @Override
    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return new HashMap<String, Object>(this.attributes);
    }
}

