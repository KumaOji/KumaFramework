package com.kuma.boot.data.jpa.coverter;

import com.kuma.boot.common.enums.base.CodeEnum;
import com.kuma.boot.common.enums.base.CommonEnum;
import jakarta.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.List;

public abstract class CommonEnumAttributeConverter<E extends Enum<E> & CommonEnum> implements AttributeConverter<E, Integer> {
   private final List<E> commonEnums;

   public CommonEnumAttributeConverter(E[] commonEnums) {
      this(Arrays.asList(commonEnums));
   }

   public CommonEnumAttributeConverter(List<E> commonEnums) {
      this.commonEnums = commonEnums;
   }

   public Integer convertToDatabaseColumn(E e) {
      return ((CodeEnum)e).getCode();
   }

   public E convertToEntityAttribute(Integer code) {
      return (E)(this.commonEnums.stream().filter((commonEnum) -> ((CommonEnum)commonEnum).match(String.valueOf(code))).findFirst().orElse((Object)null));
   }
}
