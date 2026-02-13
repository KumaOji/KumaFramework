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

import com.kuma.boot.common.enums.base.CommonEnum;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class EnumJacksonSerializer
extends StdSerializer<CommonEnum> {
    public static final EnumJacksonSerializer INSTANCE = new EnumJacksonSerializer();
    public static final String ALL_ENUM_KEY_FIELD = "code";
    public static final String ALL_ENUM_DESC_FIELD = "desc";

    public EnumJacksonSerializer() {
        super(CommonEnum.class);
    }

    public void serialize(CommonEnum distance, JsonGenerator generator, SerializationContext provider) throws JacksonException {
        generator.writeStartObject();
        generator.writeNumberProperty(ALL_ENUM_KEY_FIELD, distance.getCode());
        generator.writeStringProperty(ALL_ENUM_DESC_FIELD, distance.getDesc());
        generator.writeEndObject();
    }
}

