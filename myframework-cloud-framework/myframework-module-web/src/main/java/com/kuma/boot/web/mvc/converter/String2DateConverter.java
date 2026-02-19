/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.exception.BaseException
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.jspecify.annotations.Nullable
 *  org.springframework.core.convert.converter.Converter
 */
package com.kuma.boot.web.mvc.converter;

import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.utils.log.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;

public class String2DateConverter
extends BaseDateConverter<Date>
implements Converter<String, Date> {
    protected static final Map<String, String> FORMAT = new LinkedHashMap<String, String>(15);

    protected static Date parseDate(String dateStr, String format) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            dateFormat.setLenient(false);
            return dateFormat.parse(dateStr);
        }
        catch (ParseException e) {
            LogUtils.error((String)"\u8f6c\u6362\u65e5\u671f\u5931\u8d25, date={}, format={}", (Object[])new Object[]{dateStr, format, e});
            throw new BaseException(e.getMessage(), (Throwable)e);
        }
    }

    @Override
    protected Map<String, String> getFormat() {
        return FORMAT;
    }

    public @Nullable Date convert(String source) {
        return super.convert(source, key -> String2DateConverter.parseDate(source, key));
    }

    static {
        FORMAT.put("yyyy", "^\\d{4}");
        FORMAT.put("yyyy-MM", "^\\d{4}-\\d{1,2}$");
        FORMAT.put("yyyy-MM-dd", "^\\d{4}-\\d{1,2}-\\d{1,2}$");
        FORMAT.put("yyyy-MM-dd HH", "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}");
        FORMAT.put("yyyy-MM-dd HH:mm", "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$");
        FORMAT.put("yyyy-MM-dd HH:mm:ss", "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$");
        FORMAT.put("yyyy/MM", "^\\d{4}/\\d{1,2}$");
        FORMAT.put("yyyy/MM/dd", "^\\d{4}/\\d{1,2}/\\d{1,2}$");
        FORMAT.put("yyyy/MM/dd HH", "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}");
        FORMAT.put("yyyy/MM/dd HH:mm", "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}$");
        FORMAT.put("yyyy/MM/dd HH:mm:ss", "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$");
        FORMAT.put("yyyy\u5e74MM\u6708dd\u65e5", "^\\d{4}\u5e74\\d{1,2}\u6708\\d{1,2}\u65e5$");
        FORMAT.put("", "^\\d{4}\u5e74\\d{1,2}\u6708\\d{1,2}\u65e5\\d{1,2}\u65f6\\d{1,2}\u5206\\d{1,2}\u79d2$");
    }
}

