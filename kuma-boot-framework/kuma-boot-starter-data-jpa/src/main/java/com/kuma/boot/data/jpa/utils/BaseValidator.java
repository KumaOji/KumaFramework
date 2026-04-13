/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.jpa.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.validator.HibernateValidator;

/**
 * 基础校验器
 *
 */
public final class BaseValidator {

   private static final Validator validator;

   static {
      validator =
              Validation.byProvider(HibernateValidator.class)
                      .configure()
                      .failFast(false)
                      .buildValidatorFactory()
                      .getValidator();
   }

   private BaseValidator() {
      super();
   }

   /**
    * 校验对象
    *
    * @param obj 对象
    * @return 错误消息集合
    */
   public static List<String> validate(Object obj) {
      Set<ConstraintViolation<Object>> set = validator.validate(obj);
      return Optional.ofNullable(set).orElse(Collections.emptySet()).stream()
              .map(ConstraintViolation::getMessage)
              .collect(Collectors.toList());
   }

   /**
    * 对象是否有效
    *
    * @param obj 对象
    * @return 是否有效
    */
   public static boolean isValid(Object obj) {
      Set<ConstraintViolation<Object>> set = validator.validate(obj);
      return CollectionUtils.isEmpty(set);
   }

   /**
    * 对象是否无效
    *
    * @param obj 对象
    * @return 是否无效
    */
   public static boolean isInvalid(Object obj) {
      return !isValid(obj);
   }
}
