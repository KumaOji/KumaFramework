/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.springframework.core.DefaultParameterNameDiscoverer
 *  org.springframework.core.ParameterNameDiscoverer
 *  org.springframework.util.Assert
 */
package com.kuma.boot.common.support.expression;

import com.google.common.collect.Maps;
import com.kuma.boot.common.support.expression.Context;
import com.kuma.boot.common.support.expression.ContextFactory;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.Assert;

class DefaultContextFactory
implements ContextFactory {
    private final Map<Method, String[]> parameterNamesCache = new ConcurrentHashMap<Method, String[]>(64);
    private final ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    DefaultContextFactory() {
    }

    @Override
    public Context create(Method method, Object[] args) {
        Assert.notNull((Object)method, (String)"method not be null");
        String[] parameterNames = this.parameterNamesCache.computeIfAbsent(method, this::parseParameterNames);
        if (StringUtils.isEmpty(parameterNames)) {
            return Context.create();
        }
        HashMap map = Maps.newHashMapWithExpectedSize((int)parameterNames.length);
        if (args != null && parameterNames.length == args.length) {
            for (int i = 0; i < parameterNames.length; ++i) {
                map.put(parameterNames[i], args[i]);
            }
        }
        return Context.create(map);
    }

    private String[] parseParameterNames(Method method) {
        String[] parameterNames = this.discoverer.getParameterNames(method);
        return parameterNames == null ? new String[]{} : parameterNames;
    }
}

