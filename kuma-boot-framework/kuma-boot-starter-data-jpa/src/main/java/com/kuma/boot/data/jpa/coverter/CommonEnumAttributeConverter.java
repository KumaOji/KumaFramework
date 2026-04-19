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

package com.kuma.boot.data.jpa.coverter;

import com.kuma.boot.common.enums.base.CommonEnum;
import jakarta.persistence.AttributeConverter;

import java.util.Arrays;
import java.util.List;

/**
 * CommonEnumAttributeConverter
 *
 * @author kuma
 * @version 2026.03
 * @since 2025-12-19 09:30:45
 */
public abstract class CommonEnumAttributeConverter<E extends Enum<E> & CommonEnum> implements
        AttributeConverter<E, Integer> {

   private final List<E> commonEnums;

   public CommonEnumAttributeConverter( E[] commonEnums ) {
      this(Arrays.asList(commonEnums));
   }

   public CommonEnumAttributeConverter( List<E> commonEnums ) {
      this.commonEnums = commonEnums;
   }

   @Override
   public Integer convertToDatabaseColumn( E e ) {
      return e.getCode();
   }

   @Override
   public E convertToEntityAttribute( Integer code ) {
      return (E)
              commonEnums.stream()
                      .filter(commonEnum -> commonEnum.match(String.valueOf(code)))
                      .findFirst()
                      .orElse(null);
   }
}
