/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 *  org.springframework.beans.BeanWrapperImpl
 */
package com.kuma.boot.web.validation.validator;

import com.kuma.boot.web.validation.annotation.RangeCompare;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.beans.BeanWrapperImpl;

public class RangeCompareValidator
implements ConstraintValidator<RangeCompare, Object> {
    private String from;
    private String to;

    public void initialize(RangeCompare constraint) {
        this.from = constraint.from();
        this.to = constraint.to();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);
        Object fromValue = beanWrapper.getPropertyValue(this.from);
        Object toValue = beanWrapper.getPropertyValue(this.to);
        if (fromValue == null || toValue == null) {
            return true;
        }
        if (fromValue instanceof Number && toValue instanceof Number) {
            return ((Number)fromValue).doubleValue() <= ((Number)toValue).doubleValue();
        }
        if (fromValue instanceof LocalDate && toValue instanceof LocalDate) {
            return !((LocalDate)fromValue).isAfter((LocalDate)toValue);
        }
        if (fromValue instanceof LocalDateTime && toValue instanceof LocalDateTime) {
            return !((LocalDateTime)fromValue).isAfter((LocalDateTime)toValue);
        }
        if (fromValue instanceof LocalTime && toValue instanceof LocalTime) {
            return !((LocalTime)fromValue).isAfter((LocalTime)toValue);
        }
        throw new IllegalArgumentException("\u53ea\u652f\u6301\u6570\u5b57\u6216\u65e5\u671f\u7c7b\u578b\u7684\u6bd4\u8f83");
    }
}

