//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.model.domain;

import com.kuma.boot.ddd.model.exception.ValidateException;
import java.util.stream.Stream;

public interface ValueObjectEnum<T> extends ValueObject<T> {
   T getValue();

   String getDesc();

   static <E extends ValueObjectEnum<T>, T> E of(T value, Class<E> valueObjectEnumClass) {
      return (E)(Stream.of((ValueObjectEnum[])valueObjectEnumClass.getEnumConstants()).filter((curEnum) -> curEnum.getValue().equals(value)).findFirst().orElseThrow(() -> new ValidateException(String.format("%s.value is illegal", valueObjectEnumClass.getSimpleName()))));
   }
}
