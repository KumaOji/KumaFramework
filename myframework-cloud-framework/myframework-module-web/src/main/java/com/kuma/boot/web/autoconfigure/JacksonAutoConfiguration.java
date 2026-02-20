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

package com.kuma.boot.web.autoconfigure;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.support.jackson.JacksonModule;
import com.kuma.boot.common.support.jackson.MyBeanSerializerModifier;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.SerializationFeature;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

import static tools.jackson.databind.cfg.DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS;
import com.fasterxml.jackson.annotation.JsonInclude;
/**
 * jackson 自动配置
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 21:28:08
 */
@AutoConfiguration
public class JacksonAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(JacksonAutoConfiguration.class, StarterNameConstants.WEB_STARTER);
    }

    @Bean
    public JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer() {

        return customizer -> {
            //customizer.createXmlMapper(true);

            customizer.findAndAddModules();
            customizer.defaultLocale(Locale.CHINA);
            // 时区
            customizer.defaultTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
            // 去掉默认的时间戳格式
            customizer.configure(WRITE_DATES_AS_TIMESTAMPS, false);
            // 忽略在json字符串中存在，但是在java对象中不存在对应属性的情况
            // 忽略未知字段
            customizer.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // 忽略空Bean转json的错误
            // 在使用spring boot +
            // jpa/hibernate，如果实体字段上加有FetchType.LAZY，并使用jackson序列化为json串时，会遇到SerializationFeature.FAIL_ON_EMPTY_BEANS异常
            customizer.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            // 允许不带引号的字段名称
            customizer.configure(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES, true);
            // 允许单引号
            customizer.configure(JsonReadFeature.ALLOW_SINGLE_QUOTES, true);
            // allow int startWith 0
            customizer.configure(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS, true);
            // 允许字符串存在转义字符：\r \n \t
            // 该特性决定parser是否允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）。
            // 如果该属性关闭，则如果遇到这些字符，则会抛出异常。JSON标准说明书要求所有控制符必须使用引号，因此这是一个非标准的特性
            customizer.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true);
            // 忽略不能转义的字符
            customizer.configure(
                    JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
            customizer
                    .changeDefaultVisibility(vc ->
                            vc.withFieldVisibility(JsonAutoDetect.Visibility.NONE));
            // 包含null
            customizer.changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.ALWAYS));
            customizer.changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.ALWAYS));
            // 使用驼峰式
            customizer.propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
            // 所有日期格式都统一为固定格式
            customizer.defaultDateFormat(new SimpleDateFormat(CommonConstants.DATETIME_FORMAT, Locale.CHINA));
            customizer.serializerFactory().withSerializerModifier(new MyBeanSerializerModifier());

            // 注册自定义模块
            //customizer.addModule(new Jdk8Module());
            customizer.addModule(new JacksonModule());

//            customizer.serializerFactory(
//                    LocalDateTime.class,
//                    new LocalDateTimeSerializer(
//                            DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
//            customizer.deserializerByType(
//                    LocalDateTime.class,
//                    new LocalDateTimeDeserializer(
//                            DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
//
//            customizer.deserializerByType(
//                    LocalDate.class,
//                    new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
//            customizer.serializerByType(
//                    LocalDate.class,
//                    new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
//
//            customizer.deserializerByType(
//                    LocalTime.class,
//                    new LocalTimeDeserializer(
//                            DateTimeFormatter.ofPattern(DateUtils.DEFAULT_TIME_FORMAT)));
//            customizer.serializerByType(
//                    LocalTime.class,
//                    new LocalTimeSerializer(
//                            DateTimeFormatter.ofPattern(DateUtils.DEFAULT_TIME_FORMAT)));
//
//            customizer.failOnEmptyBeans(false);
//            customizer.failOnUnknownProperties(false);
        };
    }
}
