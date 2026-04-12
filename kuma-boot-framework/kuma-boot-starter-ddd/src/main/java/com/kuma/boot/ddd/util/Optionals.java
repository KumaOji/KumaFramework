package com.kuma.boot.ddd.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class Optionals {
   public static Object defaultNull(Object initValue, Function mapFunc) {
      return Optional.ofNullable(initValue).map(mapFunc).orElse((Object)null);
   }

   public static Object defaultNull(Object initValue, Function mapFunc1, Function mapFunc2) {
      return Optional.ofNullable(initValue).map(mapFunc1).map(mapFunc2).orElse((Object)null);
   }

   public static Object defaultNull(Object initValue, Function mapFunc1, Function mapFunc2, Function mapFunc3) {
      return Optional.ofNullable(initValue).map(mapFunc1).map(mapFunc2).map(mapFunc3).orElse((Object)null);
   }

   public static Boolean isNull(Object initValue, Function mapFunc) {
      return Objects.isNull(defaultNull(initValue, mapFunc));
   }

   public static Boolean isNull(Object initValue, Function mapFunc1, Function mapFunc2) {
      return Objects.isNull(defaultNull(initValue, mapFunc1, mapFunc2));
   }

   public static Boolean isNull(Object initValue, Function mapFunc1, Function mapFunc2, Function mapFunc3) {
      return Objects.isNull(defaultNull(initValue, mapFunc1, mapFunc2, mapFunc3));
   }
}
