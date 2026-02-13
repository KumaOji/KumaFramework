/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.jspecify.annotations.NonNull
 */
package com.kuma.boot.common.support.expression;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jspecify.annotations.NonNull;

public sealed interface Context {
    public static Context create() {
        return new ContextImpl();
    }

    public static Context create(Map<String, ?> map) {
        return new ContextImpl(map);
    }

    public Context putAll(Map<? extends String, ?> var1);

    public Context put(String var1, Object var2);

    public Map<String, Object> asMap();

    public static non-sealed class ContextImpl
    implements Context {
        private final Map<String, Object> variables;

        private ContextImpl() {
            this.variables = new HashMap<String, Object>();
        }

        private ContextImpl(Map<String, ?> map) {
            this.variables = Maps.newHashMap(map);
        }

        @Override
        public Context put(String key, Object value) {
            this.variables.put(key, value);
            return this;
        }

        @Override
        public Context putAll(@NonNull Map<? extends String, ?> m) {
            this.variables.putAll(m);
            return this;
        }

        @Override
        public Map<String, Object> asMap() {
            return Collections.unmodifiableMap(this.variables);
        }
    }
}

