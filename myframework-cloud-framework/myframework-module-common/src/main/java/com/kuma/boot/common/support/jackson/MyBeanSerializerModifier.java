/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  tools.jackson.databind.BeanDescription$Supplier
 *  tools.jackson.databind.SerializationConfig
 *  tools.jackson.databind.ser.BeanPropertyWriter
 *  tools.jackson.databind.ser.ValueSerializerModifier
 */
package com.kuma.boot.common.support.jackson;

import com.kuma.boot.common.support.jackson.NullArrayJsonSerializer;
import com.kuma.boot.common.support.jackson.NullMapJsonSerializer;
import com.kuma.boot.common.support.jackson.NullObjectJsonSerializer;
import com.kuma.boot.common.support.jackson.NullPrimitiveWrapperBooleanJsonSerializer;
import com.kuma.boot.common.support.jackson.NullPrimitiveWrapperNumberJsonSerializer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;

public class MyBeanSerializerModifier
extends ValueSerializerModifier {
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription.Supplier beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter writer : beanProperties) {
            if (this.isArrayType(writer)) {
                writer.assignNullSerializer(NullArrayJsonSerializer.INSTANCE);
                continue;
            }
            if (this.isMapType(writer)) {
                writer.assignNullSerializer(NullMapJsonSerializer.INSTANCE);
                continue;
            }
            if (this.isBooleanType(writer)) {
                writer.assignNullSerializer(NullPrimitiveWrapperBooleanJsonSerializer.INSTANCE);
                continue;
            }
            if (this.isDoubleType(writer) || this.isFloatType(writer) || this.isDoubleType(writer) || this.isIntegerType(writer) || this.isLongType(writer) || this.isShortType(writer)) {
                writer.assignNullSerializer(NullPrimitiveWrapperNumberJsonSerializer.INSTANCE);
                continue;
            }
            writer.assignNullSerializer(NullObjectJsonSerializer.INSTANCE);
        }
        return beanProperties;
    }

    private boolean isArrayType(BeanPropertyWriter writer) {
        Class rawClass = writer.getType().getRawClass();
        return rawClass.isArray() || Collection.class.isAssignableFrom(rawClass);
    }

    private boolean isMapType(BeanPropertyWriter writer) {
        Class rawClass = writer.getType().getRawClass();
        return Map.class.isAssignableFrom(rawClass);
    }

    private boolean isBooleanType(BeanPropertyWriter writer) {
        Class rawClass = writer.getType().getRawClass();
        return Boolean.class.isAssignableFrom(rawClass);
    }

    private boolean isDoubleType(BeanPropertyWriter writer) {
        Class rawClass = writer.getType().getRawClass();
        return Double.class.isAssignableFrom(rawClass);
    }

    private boolean isFloatType(BeanPropertyWriter writer) {
        Class rawClass = writer.getType().getRawClass();
        return Float.class.isAssignableFrom(rawClass);
    }

    private boolean isIntegerType(BeanPropertyWriter writer) {
        Class rawClass = writer.getType().getRawClass();
        return Integer.class.isAssignableFrom(rawClass);
    }

    private boolean isLongType(BeanPropertyWriter writer) {
        Class rawClass = writer.getType().getRawClass();
        return Long.class.isAssignableFrom(rawClass);
    }

    private boolean isShortType(BeanPropertyWriter writer) {
        Class rawClass = writer.getType().getRawClass();
        return Short.class.isAssignableFrom(rawClass);
    }
}

