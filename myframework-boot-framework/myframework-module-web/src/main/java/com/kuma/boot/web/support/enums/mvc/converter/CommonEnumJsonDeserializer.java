/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.enums.base.CommonEnum
 *  tools.jackson.core.JacksonException
 *  tools.jackson.core.JsonParser
 *  tools.jackson.databind.DeserializationContext
 *  tools.jackson.databind.ValueDeserializer
 */
package com.kuma.boot.web.support.enums.mvc.converter;

import com.kuma.boot.common.enums.base.CommonEnum;
import java.util.List;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class CommonEnumJsonDeserializer
extends ValueDeserializer<CommonEnum> {
    private final List<CommonEnum> commonEnums;

    CommonEnumJsonDeserializer(List<CommonEnum> commonEnums) {
        this.commonEnums = commonEnums;
    }

    public CommonEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws JacksonException {
        String value = (String)jsonParser.readValueAs(String.class);
        return this.commonEnums.stream().filter(commonEnum -> commonEnum.match(value)).findFirst().orElse(null);
    }
}

