/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.core.convert.converter.Converter
 */
package com.kuma.boot.web.mvc.converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;

public class String2LocalTimeConverter
extends BaseDateConverter<LocalTime>
implements Converter<String, LocalTime> {
    protected static final Map<String, String> FORMAT = new LinkedHashMap<String, String>(5);

    @Override
    protected Map<String, String> getFormat() {
        return FORMAT;
    }

    public LocalTime convert(String source) {
        return super.convert(source, key -> LocalTime.parse(source, DateTimeFormatter.ofPattern(key)));
    }

    static {
        FORMAT.put("HH:mm:ss", "^\\d{1,2}:\\d{1,2}:\\d{1,2}$");
    }
}

