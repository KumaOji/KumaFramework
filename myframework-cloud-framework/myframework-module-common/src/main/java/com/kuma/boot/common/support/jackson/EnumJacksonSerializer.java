package com.kuma.boot.common.support.jackson;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;
import com.kuma.boot.common.enums.base.CommonEnum;

public class EnumJacksonSerializer
        extends StdSerializer<CommonEnum> {
    public static final EnumJacksonSerializer INSTANCE = new EnumJacksonSerializer();
    public static final String ALL_ENUM_KEY_FIELD = "code";
    public static final String ALL_ENUM_DESC_FIELD = "desc";

    public EnumJacksonSerializer() {
        super(CommonEnum.class);
    }

    @Override
    public void serialize(CommonEnum distance, JsonGenerator generator, SerializationContext provider) throws JacksonException {
        generator.writeStartObject();
        generator.writeNumberField(ALL_ENUM_KEY_FIELD, distance.getCode());
        generator.writeStringField(ALL_ENUM_DESC_FIELD, distance.getDesc());
        generator.writeEndObject();
    }
}

