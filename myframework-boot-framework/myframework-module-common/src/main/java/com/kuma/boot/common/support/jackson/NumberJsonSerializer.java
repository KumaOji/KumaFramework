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

package com.kuma.boot.common.support.jackson;

import com.google.gson.JsonSerializer;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * The type Number json serializer.
 *
 */
public class NumberJsonSerializer extends ValueSerializer<Number> {

    private static final List<Class<?>> SUPPORT_PRIMITIVE_CLASS =
            List.of(byte.class, short.class, int.class, long.class, float.class, double.class);

    private String format;

    public NumberJsonSerializer(String format) {
        this.format = format;
    }

//    @Override
//    public void serialize(Number value, JsonGenerator gen, SerializerProvider serializers)
//            throws IOException {
//        gen.writeString(new DecimalFormat(format).format(value));
//    }
//
//    @Override
//    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
//            throws JsonMappingException {
//        Class<?> rawClass = property.getType().getRawClass();
//        NumberJsonFormat jsonFormat =
//                Optional.ofNullable(property.getAnnotation((NumberJsonFormat.class)))
//                        .orElse(property.getContextAnnotation(NumberJsonFormat.class));
//        if (Number.class.isAssignableFrom(rawClass)
//                || this.simpleTypeSupport(jsonFormat.simpleTypeSupport(), rawClass)) {
//            return new NumberJsonSerializer(jsonFormat.pattern());
//        }
//        return prov.findValueSerializer(property.getType(), property);
//    }
//
//    private boolean simpleTypeSupport(boolean support, Class<?> rawClass) {
//        return support && rawClass.isPrimitive() && SUPPORT_PRIMITIVE_CLASS.contains(rawClass);
//    }

    @Override
    public void serialize(Number value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        gen.writeString(new DecimalFormat(format).format(value));
    }
}
