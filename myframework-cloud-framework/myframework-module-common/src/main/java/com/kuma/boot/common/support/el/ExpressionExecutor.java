/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson2.util.TypeUtils
 */
package com.kuma.boot.common.support.el;

import com.alibaba.fastjson2.util.TypeUtils;
import java.util.Map;

public interface ExpressionExecutor {
    public Object execute(String var1, Map<String, Object> var2);

    default public <T> T execute(String expressionText, Map<String, Object> variables, Class<T> resultType) {
        return (T)TypeUtils.cast((Object)this.execute(expressionText, variables), resultType, null);
    }
}

