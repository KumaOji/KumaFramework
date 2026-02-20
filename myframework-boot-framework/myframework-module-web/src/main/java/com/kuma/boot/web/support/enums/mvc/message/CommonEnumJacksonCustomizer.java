/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.enums.base.CommonEnum
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  tools.jackson.core.JacksonException
 *  tools.jackson.core.JsonGenerator
 *  tools.jackson.core.JsonParser
 *  tools.jackson.databind.DeserializationContext
 *  tools.jackson.databind.JacksonModule
 *  tools.jackson.databind.SerializationContext
 *  tools.jackson.databind.ValueDeserializer
 *  tools.jackson.databind.ValueSerializer
 *  tools.jackson.databind.module.SimpleModule
 */
package com.kuma.boot.web.support.enums.mvc.message;

import com.kuma.boot.common.enums.base.CommonEnum;
import com.kuma.boot.web.support.enums.mvc.CommonEnumRegistry;
import com.kuma.boot.web.support.enums.mvc.CommonEnumVO;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

@Configuration
public class CommonEnumJacksonCustomizer {
    @Autowired
    private CommonEnumRegistry enumRegistry;

    @Bean
    public JsonMapperBuilderCustomizer commonEnumBuilderCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule();
            Map<Class<?>, List<CommonEnum>> classDict = this.enumRegistry.getClassDict();
            classDict.forEach((aClass, commonEnums) -> {
                Class clazz = aClass;
                module.addDeserializer(clazz, (ValueDeserializer)new CommonEnumJsonDeserializer((List<CommonEnum>)commonEnums));
                module.addSerializer(clazz, (ValueSerializer)new CommonEnumJsonSerializer());
            });
            builder.addModule((JacksonModule)module);
        };
    }

    static class CommonEnumJsonDeserializer
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

    static class CommonEnumJsonSerializer
    extends ValueSerializer<CommonEnum> {
        CommonEnumJsonSerializer() {
        }

        public void serialize(CommonEnum commonEnum, JsonGenerator jsonGenerator, SerializationContext ctxt) throws JacksonException {
            CommonEnumVO commonEnumVO = CommonEnumVO.from(commonEnum);
            jsonGenerator.writeStartObject((Object)commonEnumVO);
        }
    }
}

