/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.expression;

import com.kuma.boot.common.support.expression.Context;
import com.kuma.boot.common.support.expression.ContextFactory;
import com.kuma.boot.common.support.expression.DefaultContextFactory;
import com.kuma.boot.common.support.expression.ExpressionResolver;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractExpressionResolver
implements ExpressionResolver {
    protected final ContextFactory contextFactory;

    protected AbstractExpressionResolver(ContextFactory contextFactory) {
        this.contextFactory = Objects.requireNonNull(contextFactory, "ContextFactory must not be null");
    }

    protected AbstractExpressionResolver() {
        this(new DefaultContextFactory());
    }

    @Override
    public final <T> T evaluate(String value, Map<String, ?> contextMap, Class<T> returnType) {
        Context context = Context.create(contextMap);
        return this.evaluate(value, context, returnType);
    }

    @Override
    public final <T> T evaluate(String value, Method method, Object[] args, Class<T> returnType) {
        Context context = this.contextFactory.create(method, args);
        return this.evaluate(value, context, returnType);
    }

    @Override
    public final String evaluate(String value, Context context) {
        return ExpressionResolver.super.evaluate(value, context);
    }

    @Override
    public final String evaluate(String value, Map<String, ?> contextMap) {
        return ExpressionResolver.super.evaluate(value, contextMap);
    }

    @Override
    public final String evaluate(String value, Method method, Object[] args) {
        return ExpressionResolver.super.evaluate(value, method, args);
    }
}

