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

package com.kuma.boot.common.utils.validator;

import com.kuma.boot.common.enums.ValidatorExceptionEnum;
import com.kuma.boot.common.exception.CheckException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Iterator;
import java.util.Set;
import cn.hutool.core.util.StrUtil;

/**
 * 参数校验器，静态方法调用
 *
 * @author kuma
 * @version 2022.09
 * @since 2023-01-03 11:28:37
 */
public class ValidatorUtils {

    /** 验证器实例 */
    private static final Validator VALIDATOR_INSTANCE =
            Validation.buildDefaultValidatorFactory().getValidator();

    // private static final Validator VALIDATOR_INSTANCE =
    // ContextUtils.getBean(Validator.class,
    // true);

    /**
     * 校验参数是否合法，返回校验的结果
     * @param object 被校验的包装类参数
     * @param groups 校验组
     * @return {@link Set }<{@link ConstraintViolation }<{@link Object }>>
     * @since 2023-01-03 11:28:37
     */
    // public static Set<ConstraintViolation<Object>> validate(Object object, Class<?>...
    // groups)
    // {
    // return VALIDATOR_INSTANCE.validate(object, groups);
    // }

    public static <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        return VALIDATOR_INSTANCE.validate(object, groups);
    }

    /**
     * 校验参数是否合法，直接返回成功和失败
     * @param object 被校验的包装类参数
     * @param groups 校验组
     * @return boolean
     * @since 2023-01-03 11:28:37
     */
    public static boolean simpleValidate(Object object, Class<?>... groups) {
        Set<ConstraintViolation<Object>> constraintViolations =
                VALIDATOR_INSTANCE.validate(object, groups);
        return constraintViolations.isEmpty();
    }

    /**
     * 校验参数是否合法，不返回结果，有问题直接抛出异常
     * @param object 被校验的包装类参数
     * @param groups 校验组
     * @since 2023-01-03 11:28:37
     */
    public static void validateThrowMessage(Object object, Class<?>... groups) {
        String errorMessage = validateGetMessage(object, groups);
        if (errorMessage != null) {
            throw new CheckException(
                    ValidatorExceptionEnum.VALIDATED_RESULT_ERROR.getDesc());
        }
    }

    /**
     * 校验参数是否合法
     *
     * <p>
     * 不合法会返回不合法的提示，合法的话会返回null
     * @param object 被校验的包装类参数
     * @param groups 校验组
     * @return {@link String }
     * @since 2023-01-03 11:28:37
     */
    public static String validateGetMessage(Object object, Class<?>... groups) {
        Set<ConstraintViolation<Object>> constraintViolations =
                VALIDATOR_INSTANCE.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (Iterator<ConstraintViolation<Object>> it = constraintViolations.iterator();
                 it.hasNext(); ) {
                ConstraintViolation<Object> violation = it.next();
                errorMessage.append(violation.getMessage());
                if (it.hasNext()) {
                    errorMessage.append(", ");
                }
            }
            return StrUtil.format(ValidatorExceptionEnum.VALIDATED_RESULT_ERROR.getUserTip());
        }
        return null;
    }
}
