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

public class NullPrimitiveWrapperBooleanJsonSerializer<T>
extends StdSerializer<T> {
    public static final NullPrimitiveWrapperBooleanJsonSerializer<Object> INSTANCE = new NullPrimitiveWrapperBooleanJsonSerializer(Object.class);

    protected NullPrimitiveWrapperBooleanJsonSerializer(Class<?> t) {
        super(t);
    }

    public void serialize(T value, JsonGenerator gen, SerializationContext provider) throws JacksonException {
        gen.writeBoolean(false);
    }
}

