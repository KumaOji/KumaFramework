/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.expression;

import com.kuma.boot.common.support.expression.Context;
import com.kuma.boot.common.support.expression.ContextFactory;
import java.lang.reflect.Method;
import java.util.Map;

public interface ExpressionResolver {
    public <T> T evaluate(String var1, Context var2, Class<T> var3);

    public <T> T evaluate(String var1, Map<String, ?> var2, Class<T> var3);

    public <T> T evaluate(String var1, Method var2, Object[] var3, Class<T> var4);

    @Deprecated(since="1.4.2", forRemoval=true)
    default public <T> T evaluate(String value, Method method, Object[] args, Map<String, ?> contextMap, Class<T> returnType) {
        Context context = ContextFactory.DEFAULT_FACTORY.create(method, args).putAll(contextMap);
        return this.evaluate(value, context, returnType);
    }

    default public String evaluate(String value, Method method, Object[] args) {
        return this.evaluate(value, method, args, String.class);
    }

    default public String evaluate(String value, Context context) {
        return this.evaluate(value, context, String.class);
    }

    default public String evaluate(String value, Map<String, ?> contextMap) {
        return this.evaluate(value, contextMap, String.class);
    }

    @Deprecated(since="1.4.2", forRemoval=true)
    default public String evaluate(String value, Method method, Object[] args, Map<String, ?> contextMap) {
        return this.evaluate(value, method, args, contextMap, String.class);
    }
}

