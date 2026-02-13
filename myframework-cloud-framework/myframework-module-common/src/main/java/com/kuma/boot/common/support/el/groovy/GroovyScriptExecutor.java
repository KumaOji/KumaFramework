/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.el.groovy;

import com.kuma.boot.common.support.el.ExpressionExecutor;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class GroovyScriptExecutor
implements ExpressionExecutor {
    private static final Map<String, Class<?>> classMap = new ConcurrentHashMap();
    private static final List<String> defaultImports = new ArrayList<String>();

    @Override
    public Object execute(String expressionText, Map<String, Object> variables) {
        if (StringUtils.isBlank(expressionText)) {
            return null;
        }
        return null;
    }

    static {
        defaultImports.add("java.util.*");
        defaultImports.add("com.alibaba.fastjson2.*");
        defaultImports.add("org.apache.commons.lang3.StringUtils");
        defaultImports.add("org.apache.commons.collections4.CollectionUtils");
        defaultImports.add("groovy.json.*");
    }
}

