package com.kuma.boot.common.support.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class NullPrimitiveWrapperNumberJsonSerializer<T>
        extends StdSerializer<T> {
    public static final NullPrimitiveWrapperNumberJsonSerializer<Object> INSTANCE = new NullPrimitiveWrapperNumberJsonSerializer<>(Object.class);

    protected NullPrimitiveWrapperNumberJsonSerializer(Class<?> t) {
        super(t);
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(0);
    }
}

