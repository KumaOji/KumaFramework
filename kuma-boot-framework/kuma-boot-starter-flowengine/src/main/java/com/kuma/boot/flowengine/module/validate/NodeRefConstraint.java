package com.kuma.boot.flowengine.module.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
   validatedBy = {NodeRefValidator.class}
)
public @interface NodeRefConstraint {
   String message() default "{\u975e\u6cd5\u7684\u8282\u70b9\u5f15\u7528\u4e3anull\u6216\"\"}";

   Class<?>[] groups() default {};

   Class<? extends Payload>[] payload() default {};
}
