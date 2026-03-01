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

package com.kuma.boot.web.validation.annotation;

import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.web.validation.validator.DateValueValidator;
import com.kuma.boot.web.validation.annotation.DateValue.List;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 日期格式的校验，根据format参数的格式校验
 *
 * <p>format可以填写：
 *
 * <p>yyyy-MM-dd
 *
 * <p>yyyy-MM-dd HH:mm:ss
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 22:07:34
 */
@Documented
@Constraint(validatedBy = DateValueValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(List.class)
public @interface DateValue {

    String message() default "日期格式不正确,格式应该为{format}";

    Class[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /** 日期校验的格式，默认 yyyy-MM-dd */
    String format() default DateUtils.PATTERN_DATE;

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        DateValue[] value();
    }
}
