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

package com.kuma.boot.web.validation.validator;

import com.kuma.boot.web.validation.annotation.RangeCompare;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class RangeCompareValidator implements ConstraintValidator<RangeCompare, Object> {

    private String from;
    private String to;

    @Override
    public void initialize( RangeCompare constraint) {
        from = constraint.from();
        to = constraint.to();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        BeanWrapper beanWrapper = new BeanWrapperImpl(value);
        Object fromValue = beanWrapper.getPropertyValue(from);
        Object toValue = beanWrapper.getPropertyValue(to);

        if (fromValue == null || toValue == null) {
            return true;
        }

        if (fromValue instanceof Number && toValue instanceof Number) {
            return ((Number) fromValue).doubleValue() <= ((Number) toValue).doubleValue();
        }

        if (fromValue instanceof LocalDate && toValue instanceof LocalDate) {
            return !((LocalDate) fromValue).isAfter(((LocalDate) toValue));
        }

        if (fromValue instanceof LocalDateTime && toValue instanceof LocalDateTime) {
            return !((LocalDateTime) fromValue).isAfter(((LocalDateTime) toValue));
        }

        if (fromValue instanceof LocalTime && toValue instanceof LocalTime) {
            return !((LocalTime) fromValue).isAfter(((LocalTime) toValue));
        }

        throw new IllegalArgumentException("只支持数字或日期类型的比较");
    }
}
