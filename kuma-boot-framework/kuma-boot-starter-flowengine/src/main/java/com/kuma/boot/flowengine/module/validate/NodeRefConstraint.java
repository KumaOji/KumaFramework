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
   String message() default "{非法的节点引用为null或\"\"}";

   Class<?>[] groups() default {};

   Class<? extends Payload>[] payload() default {};
}
