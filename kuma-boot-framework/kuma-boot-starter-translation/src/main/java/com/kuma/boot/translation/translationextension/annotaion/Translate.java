package com.kuma.boot.translation.translationextension.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Translate {
   Type type();

   String dicCode() default "";

   String source() default "";

   String target() default "";

   public static enum Type {
      DICT,
      TABLE;

      private Type() {
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{DICT, TABLE};
      }
   }
}
