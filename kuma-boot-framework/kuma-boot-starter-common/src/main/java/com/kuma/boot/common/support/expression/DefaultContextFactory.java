/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.support.expression;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link ContextFactory} pure-Java implementation.
 * Uses {@link Parameter#getName()} for parameter name resolution (requires -parameters compile flag).
 */
class DefaultContextFactory implements ContextFactory {

    private final Map<Method, String[]> parameterNamesCache = new ConcurrentHashMap<>(64);

    @Override
    public Context create(Method method, Object[] args) {
        if (method == null) throw new IllegalArgumentException("method must not be null");
        String[] parameterNames = parameterNamesCache.computeIfAbsent(method, this::parseParameterNames);
        if (parameterNames.length == 0) {
            return Context.create();
        }
        HashMap<String, Object> map = new HashMap<>(parameterNames.length * 2);
        if (args != null && parameterNames.length == args.length) {
            for (int i = 0; i < parameterNames.length; i++) {
                map.put(parameterNames[i], args[i]);
            }
        }
        return Context.create(map);
    }

    private String[] parseParameterNames(Method method) {
        Parameter[] parameters = method.getParameters();
        String[] names = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            names[i] = parameters[i].getName();
        }
        return names;
    }
}
