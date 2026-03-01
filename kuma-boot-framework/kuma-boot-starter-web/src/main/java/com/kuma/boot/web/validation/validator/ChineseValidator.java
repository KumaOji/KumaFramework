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
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.web.validation.annotation.Chinese;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 验证是否都为汉字
 *
 * @author kuma
 * @version 2022.05
 * @since 2022-05-11 10:14:29
 */
public class ChineseValidator implements ConstraintValidator<Chinese, Object> {

    private boolean notNull;

    @Override
    public void initialize( Chinese constraintAnnotation) {
        this.notNull = constraintAnnotation.notNull();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        String validValue = null;
        if ((value != null && CharUtil.isChar(value) && !CharUtil.isBlankChar((char) value))
                || (value instanceof String && StrUtil.isNotBlank((String) value))) {
            validValue = StrUtil.toString(value);
        }

        if (StringUtils.isNotBlank(validValue)) {
            return Validator.isChinese(validValue);
        }

        return !notNull;
    }
}
