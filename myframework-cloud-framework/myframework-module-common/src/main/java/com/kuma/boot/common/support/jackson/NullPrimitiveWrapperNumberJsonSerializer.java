package com.kuma.boot.common.support.jackson;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class NullPrimitiveWrapperNumberJsonSerializer<T>
        extends StdSerializer<T> {
    public static final NullPrimitiveWrapperNumberJsonSerializer<Object> INSTANCE = new NullPrimitiveWrapperNumberJsonSerializer<>(Object.class);

    protected NullPrimitiveWrapperNumberJsonSerializer(Class<?> t) {
        super(t);
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializationContext provider) throws JacksonException {
        gen.writeNumber(0);
    }
}

