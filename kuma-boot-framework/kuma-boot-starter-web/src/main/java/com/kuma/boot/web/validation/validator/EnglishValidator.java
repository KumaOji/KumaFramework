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

import cn.hutool.core.lang.Validator;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.web.validation.annotation.English;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 验证该字符串是否是字母（包括大写和小写字母）
 *
 * @author kuma
 * @version 2022.05
 * @since 2022-05-11 10:14:51
 */
public class EnglishValidator implements ConstraintValidator<English, String> {

    private boolean notNull;

    @Override
    public void initialize( English constraintAnnotation) {
        this.notNull = constraintAnnotation.notNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isNotBlank(value)) {
            return Validator.isWord(value);
        }

        return !notNull;
    }
}
