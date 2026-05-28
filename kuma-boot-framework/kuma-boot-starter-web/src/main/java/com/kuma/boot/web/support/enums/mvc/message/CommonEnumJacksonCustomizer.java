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

package com.kuma.boot.web.support.enums.mvc.message;

import com.kuma.boot.common.enums.base.CommonEnum;
import com.kuma.boot.web.support.enums.mvc.CommonEnumRegistry;
import com.kuma.boot.web.support.enums.mvc.CommonEnumVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

import java.util.List;
import java.util.Map;

@Configuration
public class CommonEnumJacksonCustomizer {

    @Autowired
    private CommonEnumRegistry enumRegistry;

    @Bean
    public JsonMapperBuilderCustomizer commonEnumBuilderCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule();
            enumRegistry.getClassDict().forEach(
                    (aClass, commonEnums) -> registerEnum(module, aClass, commonEnums));
            builder.addModule(module);
        };
    }

    @SuppressWarnings("unchecked")
    private static <T extends CommonEnum> void registerEnum(
            SimpleModule module, Class<?> rawClass, List<CommonEnum> commonEnums) {
        Class<T> clazz = (Class<T>) rawClass;
        module.addDeserializer(clazz, (ValueDeserializer<T>) new CommonEnumJsonDeserializer(commonEnums));
        module.addSerializer(clazz, (ValueSerializer<T>) new CommonEnumJsonSerializer());
    }

    static class CommonEnumJsonSerializer extends ValueSerializer<CommonEnum> {

        @Override
        public void serialize( CommonEnum commonEnum, JsonGenerator jsonGenerator, SerializationContext ctxt )
                throws JacksonException {
            CommonEnumVO commonEnumVO = CommonEnumVO.from(commonEnum);
            jsonGenerator.writeStartObject(commonEnumVO);
        }
    }

    static class CommonEnumJsonDeserializer extends ValueDeserializer<CommonEnum> {

        private final List<CommonEnum> commonEnums;

        CommonEnumJsonDeserializer( List<CommonEnum> commonEnums ) {
            this.commonEnums = commonEnums;
        }

        @Override
        public CommonEnum deserialize(
                JsonParser jsonParser, DeserializationContext deserializationContext )
                throws JacksonException {
            String value = jsonParser.readValueAs(String.class);
            return commonEnums.stream()
                    .filter(commonEnum -> commonEnum.match(value))
                    .findFirst()
                    .orElse(null);
        }
    }
}
