/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jspecify.annotations.NonNull;

/**
 * 解析器上下文
 *
 * @author livk
 */
public sealed interface Context permits Context.ContextImpl {

    /**
     * Instantiates a new Context.
     */
    static Context create() {
        return new ContextImpl();
    }

    /**
     * Instantiates a new Context.
     *
     * @param map the map
     */
    static Context create( Map<String, ?> map ) {
        return new ContextImpl(map);
    }

    /**
     * Put all.
     *
     * @param m the m
     */
    Context putAll( Map<? extends String, ?> m );

    /**
     * Put object.
     *
     * @param key the key
     * @param value the value
     * @return the object
     */
    Context put( String key, Object value );

    Map<String, Object> asMap();

    /**
     * ContextImpl
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-17 10:30:45
     */
    non-sealed class ContextImpl implements Context {

        private final Map<String, Object> variables;

        private ContextImpl() {
            variables = new HashMap<>();
        }

        private ContextImpl( Map<String, ?> map ) {
            variables = Maps.newHashMap(map);
        }

        @Override
        public Context put( String key, Object value ) {
            variables.put(key, value);
            return this;
        }

        @Override
        public Context putAll( @NonNull Map<? extends String, ?> m ) {
            variables.putAll(m);
            return this;
        }

        @Override
        public Map<String, Object> asMap() {
            return Collections.unmodifiableMap(variables);
        }
    }
}
