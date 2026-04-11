package com.kuma.boot.xss.xssvalidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Constraint(
   validatedBy = {XssValidator.class}
)
public @interface Xss {
   String message() default "\u4e0d\u5141\u8bb8\u4efb\u4f55\u811a\u672c\u8fd0\u884c";

   Class<?>[] groups() default {};

   Class<? extends Payload>[] payload() default {};
}
