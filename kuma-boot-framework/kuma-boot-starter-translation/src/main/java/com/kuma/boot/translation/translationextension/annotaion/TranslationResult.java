package com.kuma.boot.translation.translationextension.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TranslationResult {
   boolean enable() default true;

   ConvertType convertType() default TranslationResult.ConvertType.OBJECT;

   public static enum ConvertType {
      MAP,
      OBJECT;

      private ConvertType() {
      }

      // $FF: synthetic method
      private static ConvertType[] $values() {
         return new ConvertType[]{MAP, OBJECT};
      }
   }
}
