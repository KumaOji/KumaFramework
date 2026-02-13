/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  tools.jackson.core.JacksonException
 *  tools.jackson.core.JsonGenerator
 *  tools.jackson.databind.SerializationContext
 *  tools.jackson.databind.ser.std.StdSerializer
 */
package com.kuma.boot.common.support.jackson;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class NullMapJsonSerializer<T>
extends StdSerializer<T> {
    public static final NullMapJsonSerializer<Object> INSTANCE = new NullMapJsonSerializer(Object.class);

    protected NullMapJsonSerializer(Class<?> t) {
        super(t);
    }

    public void serialize(T value, JsonGenerator gen, SerializationContext provider) throws JacksonException {
        gen.writeStartObject();
        gen.writeEndObject();
    }
}

