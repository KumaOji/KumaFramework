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

import com.kuma.boot.common.enums.CityTypeEnum;
import com.kuma.boot.web.validation.validator.CheckCityValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 校验城市地名是否合法，不接受null
 */
@Documented
@Constraint(validatedBy = CheckCityValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface CheckCityValid {

    /**
     * 校验字段类型
     */
    CityTypeEnum value() default CityTypeEnum.CITY;

    /**
     * 提示消息
     */
    String message() default "{city.valid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
