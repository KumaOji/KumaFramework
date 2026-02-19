/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;
import com.kuma.boot.common.enums.base.CommonEnum;

/**
 * 继承了BaseEnum接口的枚举值，将会统一按照以下格式序列化 { "code": "XX", "desc": "xxx" }
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 19:20:18
 */
public class EnumJacksonSerializer extends StdSerializer<CommonEnum> {

    /** INSTANCE */
    public static final EnumJacksonSerializer INSTANCE = new EnumJacksonSerializer();

    /** ALL_ENUM_KEY_FIELD */
    public static final String ALL_ENUM_KEY_FIELD = "code";

    /** ALL_ENUM_DESC_FIELD */
    public static final String ALL_ENUM_DESC_FIELD = "desc";

    public EnumJacksonSerializer() {
        super(CommonEnum.class);
    }

    @Override
    public void serialize(CommonEnum distance, JsonGenerator generator, SerializationContext provider)
            throws JacksonException {
        generator.writeStartObject();
        generator.writeNumberProperty(ALL_ENUM_KEY_FIELD, distance.getCode());
        generator.writeStringProperty(ALL_ENUM_DESC_FIELD, distance.getDesc());
        generator.writeEndObject();
    }
}
