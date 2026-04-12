package com.kuma.boot.ddd.model.domain;

import com.kuma.boot.ddd.model.exception.ValidateException;
import java.util.stream.Stream;

public interface ValueObjectEnum extends ValueObject {
   Object getValue();

   String getDesc();

   static ValueObjectEnum of(Object value, Class valueObjectEnumClass) {
      return (ValueObjectEnum)Stream.of((ValueObjectEnum[])valueObjectEnumClass.getEnumConstants()).filter((curEnum) -> curEnum.getValue().equals(value)).findFirst().orElseThrow(() -> new ValidateException(String.format("%s.value is illegal", valueObjectEnumClass.getSimpleName())));
   }
}
