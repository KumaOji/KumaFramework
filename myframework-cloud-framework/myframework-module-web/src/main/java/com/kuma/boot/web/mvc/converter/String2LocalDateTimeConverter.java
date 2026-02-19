/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.core.convert.converter.Converter
 */
package com.kuma.boot.web.mvc.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;

public class String2LocalDateTimeConverter
extends BaseDateConverter<LocalDateTime>
implements Converter<String, LocalDateTime> {
    protected static final Map<String, String> FORMAT = new LinkedHashMap<String, String>(5);

    @Override
    protected Map<String, String> getFormat() {
        return FORMAT;
    }

    public LocalDateTime convert(String source) {
        return super.convert(source, key -> LocalDateTime.parse(source, DateTimeFormatter.ofPattern(key)));
    }

    static {
        FORMAT.put("yyyy-MM-dd HH:mm:ss", "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$");
        FORMAT.put("yyyy/MM/dd HH:mm:ss", "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$");
        FORMAT.put("", "^\\d{4}\u5e74\\d{1,2}\u6708\\d{1,2}\u65e5\\d{1,2}\u65f6\\d{1,2}\u5206\\d{1,2}\u79d2$");
    }
}

