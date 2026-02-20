/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.validator.constraintvalidator;

import com.kuma.boot.web.validation.spel.core.SpelConstraintValidator;
import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import java.lang.annotation.Annotation;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractSpelTemporalValidator<T extends Annotation>
implements SpelConstraintValidator<T> {
    private static final Set<Class<?>> SUPPORT_TYPE;

    protected FieldValidResult isValid(Object fieldValue) {
        if (fieldValue == null) {
            return FieldValidResult.success();
        }
        return FieldValidResult.of(this.isValidTemporal(fieldValue));
    }

    protected abstract boolean isValidTemporal(Object var1);

    protected int compareTemporal(Object temporal, Object now) {
        if (temporal instanceof ChronoLocalDate) {
            return ((ChronoLocalDate)temporal).compareTo((ChronoLocalDate)now);
        }
        if (temporal.getClass() != now.getClass()) {
            throw new IllegalArgumentException("Cannot compare different types: " + temporal.getClass().getName() + " and " + now.getClass().getName());
        }
        if (temporal instanceof Comparable) {
            return ((Comparable)temporal).compareTo(now);
        }
        throw new IllegalArgumentException("Unsupported non-comparable temporal type: " + temporal.getClass().getName());
    }

    protected Object getNow(Object temporal) {
        if (temporal instanceof Instant) {
            return Instant.now();
        }
        if (temporal instanceof LocalDate) {
            return LocalDate.now();
        }
        if (temporal instanceof LocalDateTime) {
            return LocalDateTime.now();
        }
        if (temporal instanceof LocalTime) {
            return LocalTime.now();
        }
        if (temporal instanceof OffsetDateTime) {
            return OffsetDateTime.now();
        }
        if (temporal instanceof OffsetTime) {
            return OffsetTime.now();
        }
        if (temporal instanceof ZonedDateTime) {
            return ZonedDateTime.now();
        }
        if (temporal instanceof Year) {
            return Year.now();
        }
        if (temporal instanceof YearMonth) {
            return YearMonth.now();
        }
        if (temporal instanceof MonthDay) {
            return MonthDay.now();
        }
        if (temporal instanceof Date) {
            return new Date();
        }
        if (temporal instanceof Calendar) {
            return Calendar.getInstance();
        }
        if (temporal instanceof ChronoLocalDate) {
            return LocalDate.now();
        }
        throw new IllegalArgumentException("Unsupported temporal type: " + String.valueOf(temporal.getClass()));
    }

    @Override
    public Set<Class<?>> supportType() {
        return SUPPORT_TYPE;
    }

    static {
        HashSet<Class<Calendar>> supportTypes = new HashSet<Class<Calendar>>();
        supportTypes.add(Instant.class);
        supportTypes.add(LocalDate.class);
        supportTypes.add(LocalDateTime.class);
        supportTypes.add(LocalTime.class);
        supportTypes.add(MonthDay.class);
        supportTypes.add(OffsetDateTime.class);
        supportTypes.add(OffsetTime.class);
        supportTypes.add(Year.class);
        supportTypes.add(YearMonth.class);
        supportTypes.add(ZonedDateTime.class);
        supportTypes.add(ChronoLocalDate.class);
        supportTypes.add(ChronoLocalDateTime.class);
        supportTypes.add(ChronoZonedDateTime.class);
        supportTypes.add(Date.class);
        supportTypes.add(Calendar.class);
        SUPPORT_TYPE = Collections.unmodifiableSet(supportTypes);
    }
}

