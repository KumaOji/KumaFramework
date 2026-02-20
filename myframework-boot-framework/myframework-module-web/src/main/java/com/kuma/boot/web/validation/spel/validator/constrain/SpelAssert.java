/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.intellij.lang.annotations.Language
 */
package com.kuma.boot.web.validation.spel.validator.constrain;

import com.kuma.boot.web.validation.spel.core.SpelConstraint;
import com.kuma.boot.web.validation.spel.validator.constraintvalidator.SpelAssertValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.intellij.lang.annotations.Language;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
@Repeatable(value=List.class)
@SpelConstraint(validatedBy=SpelAssertValidator.class)
public @interface SpelAssert {
    public String message() default "{com.kuma.boot.web.validation.spel.validator.constraint.AssertTrue.message}";

    @Language(value="SpEL")
    public String condition() default "";

    @Language(value="SpEL")
    public String[] group() default {};

    @Language(value="SpEL")
    public String assertTrue() default "true";

    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.FIELD})
    @Documented
    public static @interface List {
        public SpelAssert[] value();
    }
}

