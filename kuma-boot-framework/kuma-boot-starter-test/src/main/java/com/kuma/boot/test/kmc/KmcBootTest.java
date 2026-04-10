package com.kuma.boot.test.kmc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@BootstrapWith(KmcTestContextBootstrapper.class)
@ExtendWith({SpringExtension.class})
public @interface KmcBootTest {
   @AliasFor("properties")
   String[] value() default {};

   @AliasFor("value")
   String[] properties() default {};

   String[] args() default {};

   Class<?>[] classes() default {};

   WebEnvironment webEnvironment() default KmcBootTest.WebEnvironment.MOCK;

   UseMainMethod useMainMethod() default KmcBootTest.UseMainMethod.NEVER;

   public static enum WebEnvironment {
      MOCK(false),
      RANDOM_PORT(true),
      DEFINED_PORT(true),
      NONE(false);

      private final boolean embedded;

      private WebEnvironment(boolean embedded) {
         this.embedded = embedded;
      }

      public boolean isEmbedded() {
         return this.embedded;
      }

      // $FF: synthetic method
      private static WebEnvironment[] $values() {
         return new WebEnvironment[]{MOCK, RANDOM_PORT, DEFINED_PORT, NONE};
      }
   }

   public static enum UseMainMethod {
      ALWAYS,
      NEVER,
      WHEN_AVAILABLE;

      private UseMainMethod() {
      }

      // $FF: synthetic method
      private static UseMainMethod[] $values() {
         return new UseMainMethod[]{ALWAYS, NEVER, WHEN_AVAILABLE};
      }
   }
}
