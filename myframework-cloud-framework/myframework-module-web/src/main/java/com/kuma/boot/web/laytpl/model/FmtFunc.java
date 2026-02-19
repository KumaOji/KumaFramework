/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.date.DateUtils
 */
package com.kuma.boot.web.laytpl.model;

import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.web.laytpl.exception.LayTplException;
import com.kuma.boot.web.laytpl.properties.LayTplProperties;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

public class FmtFunc {
    private final LayTplProperties properties;

    public FmtFunc(LayTplProperties properties) {
        this.properties = properties;
    }

    public String format(Object object) {
        if (object instanceof Number) {
            return this.format(object, this.properties.getNumPattern());
        }
        if (object instanceof Date) {
            return this.format(object, this.properties.getDatePattern());
        }
        if (object instanceof LocalTime) {
            return this.format(object, this.properties.getLocalTimePattern());
        }
        if (object instanceof LocalDate) {
            return this.format(object, this.properties.getLocalDatePattern());
        }
        if (object instanceof LocalDateTime) {
            return this.format(object, this.properties.getLocalDateTimePattern());
        }
        throw new LayTplException("\u672a\u652f\u6301\u7684\u5bf9\u8c61\u683c\u5f0f" + String.valueOf(object));
    }

    public String format(Object object, String pattern) {
        if (object instanceof Number) {
            DecimalFormat decimalFormat = new DecimalFormat(pattern);
            return decimalFormat.format(object);
        }
        if (object instanceof Date) {
            return DateUtils.format((Date)((Date)object), (String)pattern);
        }
        if (object instanceof TemporalAccessor) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
            return df.format((TemporalAccessor)object);
        }
        throw new LayTplException("\u672a\u652f\u6301\u7684\u5bf9\u8c61\u683c\u5f0f" + String.valueOf(object));
    }
}

