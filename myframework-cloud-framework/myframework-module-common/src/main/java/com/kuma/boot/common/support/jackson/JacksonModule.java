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

import static com.kuma.boot.common.utils.date.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.datatype.jsr353.PackageVersion;
import com.kuma.boot.common.enums.base.CommonEnum;
import com.kuma.boot.common.utils.date.DateUtils;
import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * jackson 自定义序列化 反序列化 规则
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 19:20:49
 */
public class JacksonModule extends SimpleModule {

    @Serial private static final long serialVersionUID = 1L;

    /**
     * JacksonModule
     *
     * @since 2021-09-02 19:21:04
     */
    public JacksonModule() {
        super(PackageVersion.VERSION);

        this.addSerializer(
                LocalTime.class,
                new LocalTimeSerializer(
                        DateTimeFormatter.ofPattern(DateUtils.DEFAULT_TIME_FORMAT)));
        this.addDeserializer(
                LocalTime.class,
                new LocalTimeDeserializer(
                        DateTimeFormatter.ofPattern(DateUtils.DEFAULT_TIME_FORMAT)));

        this.addDeserializer(
                LocalDateTime.class,
                new LocalDateTimeDeserializer(
                        DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        this.addSerializer(
                LocalDateTime.class,
                new LocalDateTimeSerializer(
                        DateTimeFormatter.ofPattern(DateUtils.DEFAULT_DATE_TIME_FORMAT)));

        this.addDeserializer(
                LocalDate.class,
                new LocalDateDeserializer(
                        DateTimeFormatter.ofPattern(DateUtils.DEFAULT_DATE_FORMAT)));
        this.addSerializer(
                LocalDate.class,
                new LocalDateSerializer(
                        DateTimeFormatter.ofPattern(DateUtils.DEFAULT_DATE_FORMAT)));

        this.addSerializer(CommonEnum.class, EnumJacksonSerializer.INSTANCE);
        // this.addSerializer(Long.TYPE, ToStringSerializer.instance);
        // this.addSerializer(BigInteger.class, ToStringSerializer.instance);
        // this.addSerializer(BigDecimal.class, ToStringSerializer.instance);
    }
}
