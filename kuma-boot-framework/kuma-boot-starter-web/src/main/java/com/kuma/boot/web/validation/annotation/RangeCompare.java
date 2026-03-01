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

import com.kuma.boot.web.validation.validator.RangeCompareValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 只支持数字或日期类型的比较
 * @Data
 * @RangeCompare(from = "fromDate", to = "toDate", message = "起始日期必须小于或等于结束日期")
 * public class TimeRangeParam  {
 *     @ApiModelProperty("起始日期")
 *     private LocalDate fromDate;
 *     @ApiModelProperty("结束日期")
 *     private LocalDate toDate;
 * }
 *
 *
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {RangeCompareValidator.class})
@Documented
public @interface RangeCompare {

    String message() default "起始值必须小于或等于结束值";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String from();

    String to();

    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
        RangeCompare[] value();
    }
}
