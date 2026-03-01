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

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.web.validation.annotation.Birthday;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * 生日格式校验器
 *
 * @author kuma
 * @version 2022.05
 * @since 2022-05-11 10:13:25
 */
public class BirthdayValidator implements ConstraintValidator<Birthday, Object> {

    private boolean notNull;

    @Override
    public void initialize( Birthday constraintAnnotation) {
        this.notNull = constraintAnnotation.notNull();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        String validValue = null;
        if (value instanceof String) {
            validValue = (String) value;
        } else if (value instanceof Date) {
            validValue = DateUtil.formatDate((Date) value);
        } else if (value instanceof TemporalAccessor) {
            validValue = DateUtils.toDateFormatter((TemporalAccessor) value);
        }

        if (StringUtils.isNotBlank(validValue)) {
            return Validator.isBirthday(validValue);
        }

        return !notNull;
    }
}
