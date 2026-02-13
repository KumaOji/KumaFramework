/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  tools.jackson.databind.ValueDeserializer
 *  tools.jackson.databind.ValueSerializer
 *  tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer
 *  tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer
 *  tools.jackson.databind.ext.javatime.ser.LocalDateSerializer
 *  tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer
 *  tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer
 *  tools.jackson.databind.module.SimpleModule
 *  tools.jackson.datatype.jsr353.PackageVersion
 */
package com.kuma.boot.common.support.jackson;

import com.kuma.boot.common.enums.base.CommonEnum;
import com.kuma.boot.common.support.jackson.EnumJacksonSerializer;
import com.kuma.boot.common.support.jackson.LocalDateTimeDeserializer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.datatype.jsr353.PackageVersion;

public class JacksonModule
extends SimpleModule {
    private static final long serialVersionUID = 1L;

    public JacksonModule() {
        super(PackageVersion.VERSION);
        this.addSerializer(LocalTime.class, (ValueSerializer)new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        this.addDeserializer(LocalTime.class, (ValueDeserializer)new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        this.addDeserializer(LocalDateTime.class, (ValueDeserializer)new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        this.addSerializer(LocalDateTime.class, (ValueSerializer)new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        this.addDeserializer(LocalDate.class, (ValueDeserializer)new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        this.addSerializer(LocalDate.class, (ValueSerializer)new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        this.addSerializer(CommonEnum.class, (ValueSerializer)EnumJacksonSerializer.INSTANCE);
    }
}

