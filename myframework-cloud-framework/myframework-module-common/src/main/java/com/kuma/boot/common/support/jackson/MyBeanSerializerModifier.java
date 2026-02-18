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

import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * bean对象序列化规则
 *
 * @since 2022-04-30 16:21:28
 */
public class MyBeanSerializerModifier extends ValueSerializerModifier {

    @Override
    public List<BeanPropertyWriter> changeProperties( SerializationConfig config, BeanDescription.Supplier beanDesc,
                                                      List<BeanPropertyWriter> beanProperties ) {
        // 循环所有的beanProperties
        for (BeanPropertyWriter writer : beanProperties) {
            // 给writer注册一个自己的nullSerializer
            if (isArrayType(writer)) {
                // 这里使用单例的原因是每个被序列化的bean都要执行一次这个方法,且这个类里面的方法是线程安全的,如果每次都new,想想就挺可怕的
                writer.assignNullSerializer(NullArrayJsonSerializer.INSTANCE);
            } else if (isMapType(writer)) {
                writer.assignNullSerializer(NullMapJsonSerializer.INSTANCE);
            } else if (isBooleanType(writer)) {
                writer.assignNullSerializer(NullPrimitiveWrapperBooleanJsonSerializer.INSTANCE);
            } else if (isDoubleType(writer)
                    || isFloatType(writer)
                    || isDoubleType(writer)
                    || isIntegerType(writer)
                    || isLongType(writer)
                    || isShortType(writer)) {
                writer.assignNullSerializer(NullPrimitiveWrapperNumberJsonSerializer.INSTANCE);
            } else {
                // 除了list和map外其它的都赋值为"",要是还想加别的类型,可以再这里再写else if
                writer.assignNullSerializer(NullObjectJsonSerializer.INSTANCE);
            }
        }

        return beanProperties;
    }


    /**
     * 是否是数组
     * @param writer writer
     */
    private boolean isArrayType(BeanPropertyWriter writer) {
        Class<?> rawClass = writer.getType().getRawClass();
        return rawClass.isArray() || Collection.class.isAssignableFrom(rawClass);
    }

    /**
     * 是否是map
     * @param writer writer
     */
    private boolean isMapType(BeanPropertyWriter writer) {
        Class<?> rawClass = writer.getType().getRawClass();
        return Map.class.isAssignableFrom(rawClass);
    }

    private boolean isBooleanType(BeanPropertyWriter writer) {
        Class<?> rawClass = writer.getType().getRawClass();
        return Boolean.class.isAssignableFrom(rawClass);
    }

    private boolean isDoubleType(BeanPropertyWriter writer) {
        Class<?> rawClass = writer.getType().getRawClass();
        return Double.class.isAssignableFrom(rawClass);
    }

    private boolean isFloatType(BeanPropertyWriter writer) {
        Class<?> rawClass = writer.getType().getRawClass();
        return Float.class.isAssignableFrom(rawClass);
    }

    private boolean isIntegerType(BeanPropertyWriter writer) {
        Class<?> rawClass = writer.getType().getRawClass();
        return Integer.class.isAssignableFrom(rawClass);
    }

    private boolean isLongType(BeanPropertyWriter writer) {
        Class<?> rawClass = writer.getType().getRawClass();
        return Long.class.isAssignableFrom(rawClass);
    }

    private boolean isShortType(BeanPropertyWriter writer) {
        Class<?> rawClass = writer.getType().getRawClass();
        return Short.class.isAssignableFrom(rawClass);
        // return ClassUtils.isAssignable(rawClass, Short.class);
    }
}
