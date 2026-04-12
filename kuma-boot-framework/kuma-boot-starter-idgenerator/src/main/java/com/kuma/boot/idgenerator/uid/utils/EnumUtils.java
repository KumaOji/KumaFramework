package com.kuma.boot.idgenerator.uid.utils;

import org.springframework.util.Assert;

public abstract class EnumUtils {
   public EnumUtils() {
   }

   public static <T extends ValuedEnum<V>, V> T parse(Class<T> clz, V value) {
      Assert.notNull(clz, "clz can not be null");
      if (value == null) {
         return null;
      } else {
         for(T t : clz.getEnumConstants()) {
            if (value.equals(t.value())) {
               return t;
            }
         }

         return null;
      }
   }

   public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
      return (T)(name == null ? null : Enum.valueOf(enumType, name));
   }
}
