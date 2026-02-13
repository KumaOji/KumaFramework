/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  tools.jackson.core.JacksonException
 *  tools.jackson.core.JsonGenerator
 *  tools.jackson.databind.SerializationContext
 *  tools.jackson.databind.ValueSerializer
 */
package com.kuma.boot.common.support.jackson;

import java.text.DecimalFormat;
import java.util.List;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class NumberJsonSerializer
extends ValueSerializer<Number> {
    private static final List<Class<?>> SUPPORT_PRIMITIVE_CLASS = List.of(Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE);
    private String format;

    public NumberJsonSerializer(String format) {
        this.format = format;
    }

    public void serialize(Number value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        gen.writeString(new DecimalFormat(this.format).format(value));
    }
}

