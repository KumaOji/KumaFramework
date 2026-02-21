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

import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.web.validation.annotation.PhoneValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 校验手机号是否合法
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-03 08:04:24
 */
public class PhoneValueValidator implements ConstraintValidator<PhoneValue, String> {

    private Boolean required;

    @Override
    public void initialize( PhoneValue constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String phoneValue, ConstraintValidatorContext context) {
        if (StrUtil.isEmpty(phoneValue)) {
            return !required;
        } else {
            return ReUtil.isMatch(PatternPool.MOBILE, phoneValue);
        }
    }
}
