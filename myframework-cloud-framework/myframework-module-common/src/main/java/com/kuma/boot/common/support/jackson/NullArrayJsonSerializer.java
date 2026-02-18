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

package com.kuma.boot.common.support.jackson;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

/**
 * 针对数组的处理
 *
 * @since 2022-04-30 16:01:00
 */
public class NullArrayJsonSerializer<T> extends StdSerializer<T> {

    /** 声明为单例模式 */
    public static final NullArrayJsonSerializer<Object> INSTANCE = new NullArrayJsonSerializer<>(Object.class);

    protected NullArrayJsonSerializer(Class<?> t) {
        super(t);
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializationContext provider) throws JacksonException {
        // 这里不需要判断value的值,因为只要能进到这个方法里面,就证明值是null
        gen.writeStartArray();
        gen.writeEndArray();
    }
}
