/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonFormat$Shape
 *  tools.jackson.core.JacksonException
 *  tools.jackson.core.JsonParser
 *  tools.jackson.core.JsonToken
 *  tools.jackson.databind.DeserializationContext
 *  tools.jackson.databind.DeserializationFeature
 *  tools.jackson.databind.cfg.DatatypeFeature
 *  tools.jackson.databind.cfg.DateTimeFeature
 *  tools.jackson.databind.ext.javatime.deser.JSR310DateTimeDeserializerBase
 *  tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer
 */
package com.kuma.boot.common.support.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.cfg.DatatypeFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.deser.JSR310DateTimeDeserializerBase;

public class LocalDateTimeDeserializer
extends JSR310DateTimeDeserializerBase<LocalDateTime> {
    private static final long serialVersionUID = 1L;
    public static final LocalDateTimeDeserializer INSTANCE = new LocalDateTimeDeserializer();
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DEFAULT_DATE_FORMAT_DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DEFAULT_DATE_FORMAT_EN_DTF = DateTimeFormatter.ofPattern("yyyy\u5e74MM\u6708dd\u65e5");
    private static final DateTimeFormatter SLASH_DATE_FORMAT_DTF = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMAT_DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMAT_EN_DTF = DateTimeFormatter.ofPattern("");
    private static final DateTimeFormatter SLASH_DATE_TIME_FORMAT_DTF = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    protected JSR310DateTimeDeserializerBase<LocalDateTime> withShape(JsonFormat.Shape shape) {
        return this;
    }

    private LocalDateTimeDeserializer() {
        this(DEFAULT_FORMATTER);
    }

    public LocalDateTimeDeserializer(DateTimeFormatter formatter) {
        super(LocalDateTime.class, formatter);
    }

    protected LocalDateTimeDeserializer(LocalDateTimeDeserializer base, Boolean leniency) {
        super((JSR310DateTimeDeserializerBase)base, leniency);
    }

    protected LocalDateTimeDeserializer withLeniency(Boolean leniency) {
        return new LocalDateTimeDeserializer(this, leniency);
    }

    protected JSR310DateTimeDeserializerBase<LocalDateTime> withDateFormat(DateTimeFormatter formatter) {
        return new tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer(formatter);
    }

    private LocalDateTime convert(String source) {
        if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            return LocalDateTime.of(LocalDate.parse(source, DEFAULT_DATE_FORMAT_DTF), LocalTime.MIN);
        }
        if (source.matches("^\\d{4}\u5e74\\d{1,2}\u6708\\d{1,2}\u65e5$")) {
            return LocalDateTime.of(LocalDate.parse(source, DEFAULT_DATE_FORMAT_EN_DTF), LocalTime.MIN);
        }
        if (source.matches("^\\d{4}/\\d{1,2}/\\d{1,2}$")) {
            return LocalDateTime.of(LocalDate.parse(source, SLASH_DATE_FORMAT_DTF), LocalTime.MIN);
        }
        if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return LocalDateTime.parse(source, DEFAULT_DATE_TIME_FORMAT_DTF);
        }
        if (source.matches("^\\d{4}\u5e74\\d{1,2}\u6708\\d{1,2}\u65e5\\d{1,2}\u65f6\\d{1,2}\u5206\\d{1,2}\u79d2$")) {
            return LocalDateTime.parse(source, DEFAULT_DATE_TIME_FORMAT_EN_DTF);
        }
        if (source.matches("^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return LocalDateTime.parse(source, SLASH_DATE_TIME_FORMAT_DTF);
        }
        return null;
    }

    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        if (parser.hasTokenId(6)) {
            String string = parser.getText().trim();
            if (string.length() == 0) {
                return null;
            }
            try {
                if (this._formatter == null) {
                    return this.convert(string);
                }
                if (this._formatter == DEFAULT_FORMATTER) {
                    if (string.length() > 10 && string.charAt(10) == 'T') {
                        if (string.endsWith("Z")) {
                            return LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC);
                        }
                        return LocalDateTime.parse(string, DEFAULT_FORMATTER);
                    }
                    return this.convert(string);
                }
                return LocalDateTime.parse(string, this._formatter);
            }
            catch (DateTimeException e) {
                return (LocalDateTime)this._handleDateTimeException(context, e, string);
            }
        }
        if (parser.isExpectedStartArrayToken()) {
            JsonToken t = parser.nextToken();
            if (t == JsonToken.END_ARRAY) {
                return null;
            }
            if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT) && context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                LocalDateTime parsed = this.deserialize(parser, context);
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    this.handleMissingEndArrayForSingle(parser, context);
                }
                return parsed;
            }
            if (t == JsonToken.VALUE_NUMBER_INT) {
                LocalDateTime result;
                int year = parser.getIntValue();
                int month = parser.nextIntValue(-1);
                int day = parser.nextIntValue(-1);
                int hour = parser.nextIntValue(-1);
                int minute = parser.nextIntValue(-1);
                t = parser.nextToken();
                if (t == JsonToken.END_ARRAY) {
                    result = LocalDateTime.of(year, month, day, hour, minute);
                } else {
                    int second = parser.getIntValue();
                    t = parser.nextToken();
                    if (t == JsonToken.END_ARRAY) {
                        result = LocalDateTime.of(year, month, day, hour, minute, second);
                    } else {
                        int partialSecond = parser.getIntValue();
                        if (partialSecond < 1000 && !context.isEnabled((DatatypeFeature)DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
                            partialSecond *= 1000000;
                        }
                        if (parser.nextToken() != JsonToken.END_ARRAY) {
                            throw context.wrongTokenException(parser, this.handledType(), JsonToken.END_ARRAY, "Expected array to end");
                        }
                        result = LocalDateTime.of(year, month, day, hour, minute, second, partialSecond);
                    }
                }
                return result;
            }
            context.reportInputMismatch(this.handledType(), "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT", new Object[]{t});
        }
        if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return Instant.ofEpochMilli(parser.getLongValue()).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (LocalDateTime)parser.getEmbeddedObject();
        }
        return (LocalDateTime)this._handleUnexpectedToken(context, parser, "\u5f53\u524d\u53c2\u6570\u9700\u8981\u6570\u7ec4\u3001\u5b57\u7b26\u4e32\u3001\u65f6\u95f4\u6233\u3002", new Object[0]);
    }
}

