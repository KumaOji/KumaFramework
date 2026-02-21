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

import com.kuma.boot.web.validation.validator.CarDrivingLicenceValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 验证是否为驾驶证（别名：驾驶证档案编号、行驶证编号）<br>
 *
 * <p>仅限：中国驾驶证档案编号
 *
 * <p>只支持以下一种格式：
 *
 * <ul>
 *   <li>12位数字字符串,eg:430101758218
 * </ul>
 *
 * @author kuma
 * @version 2022.05
 * @since 2022-05-11 10:13:35
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = {CarDrivingLicenceValidator.class})
@Repeatable(CarDrivingLicence.List.class)
public @interface CarDrivingLicence {

    /**
     * 是否不允许为空 {@linkplain NotNull}
     *
     * @return 默认：true
     */
    boolean notNull() default true;

    String message() default "不是一个合法的驾驶证格式";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /** Defines several {@code @since} annotations on the same element. */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        CarDrivingLicence[] value();
    }
}
