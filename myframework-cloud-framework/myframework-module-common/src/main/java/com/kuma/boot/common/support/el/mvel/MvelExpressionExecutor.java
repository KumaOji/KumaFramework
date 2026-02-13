/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.el.mvel;

import com.kuma.boot.common.support.el.ExpressionExecutor;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MvelExpressionExecutor
implements ExpressionExecutor {
    private static final Map<String, Serializable> compiledExpressionMap = new ConcurrentHashMap<String, Serializable>();

    @Override
    public Object execute(String expressionText, Map<String, Object> variables) {
        return null;
    }
}

