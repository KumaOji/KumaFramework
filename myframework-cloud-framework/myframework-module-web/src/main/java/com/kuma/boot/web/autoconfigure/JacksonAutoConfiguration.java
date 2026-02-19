/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  com.kuma.boot.common.support.jackson.JacksonModule
 *  com.kuma.boot.common.support.jackson.MyBeanSerializerModifier
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
 *  org.springframework.context.annotation.Bean
 *  tools.jackson.core.json.JsonReadFeature
 *  tools.jackson.databind.DeserializationFeature
 *  tools.jackson.databind.JacksonModule
 *  tools.jackson.databind.PropertyNamingStrategies
 *  tools.jackson.databind.SerializationFeature
 *  tools.jackson.databind.cfg.DatatypeFeature
 *  tools.jackson.databind.cfg.DateTimeFeature
 *  tools.jackson.databind.ser.ValueSerializerModifier
 */
package com.kuma.boot.web.autoconfigure;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kuma.boot.common.support.jackson.JacksonModule;
import com.kuma.boot.common.support.jackson.MyBeanSerializerModifier;
import com.kuma.boot.common.utils.log.LogUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DatatypeFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ser.ValueSerializerModifier;

@AutoConfiguration
public class JacksonAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(JacksonAutoConfiguration.class, (String)"kuma-boot-starter-web", (String[])new String[0]);
    }

    @Bean
    public JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer() {
        return customizer -> {
            customizer.findAndAddModules();
            customizer.defaultLocale(Locale.CHINA);
            customizer.defaultTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
            customizer.configure((DatatypeFeature)DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            customizer.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            customizer.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            customizer.configure(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES, true);
            customizer.configure(JsonReadFeature.ALLOW_SINGLE_QUOTES, true);
            customizer.configure(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS, true);
            customizer.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true);
            customizer.configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
            customizer.changeDefaultVisibility(vc -> vc.withFieldVisibility(JsonAutoDetect.Visibility.NONE));
            customizer.changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.ALWAYS));
            customizer.changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.ALWAYS));
            customizer.propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
            customizer.defaultDateFormat((DateFormat)new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA));
            customizer.serializerFactory().withSerializerModifier((ValueSerializerModifier)new MyBeanSerializerModifier());
            customizer.addModule((tools.jackson.databind.JacksonModule)new JacksonModule());
        };
    }
}

