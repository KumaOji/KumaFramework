/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.expression;

import com.kuma.boot.common.support.expression.Context;
import com.kuma.boot.common.support.expression.DefaultContextFactory;
import java.lang.reflect.Method;
import java.util.Map;

@FunctionalInterface
public interface ContextFactory {
    public static final ContextFactory DEFAULT_FACTORY = new DefaultContextFactory();

    public Context create(Method var1, Object[] var2);

    @Deprecated(since="1.4.2", forRemoval=true)
    default public Context merge(Method method, Object[] args, Map<String, ?> expandMap) {
        Context context = this.create(method, args);
        return context.putAll(expandMap);
    }
}

