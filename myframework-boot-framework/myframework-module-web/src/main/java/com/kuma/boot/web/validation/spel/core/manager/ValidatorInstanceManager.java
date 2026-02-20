/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.kuma.boot.web.validation.spel.core.manager;

import com.kuma.boot.web.validation.spel.core.SpelConstraint;
import com.kuma.boot.web.validation.spel.core.SpelConstraintValidator;
import com.kuma.boot.web.validation.spel.core.exception.SpelValidatorException;
import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public class ValidatorInstanceManager {
    private static final ConcurrentHashMap<Annotation, SpelConstraintValidator<?>> VALIDATOR_INSTANCE_CACHE = new ConcurrentHashMap();

    private ValidatorInstanceManager() {
    }

    @NotNull
    public static SpelConstraintValidator<? extends Annotation> getInstance(@NotNull Annotation annotation) {
        return VALIDATOR_INSTANCE_CACHE.computeIfAbsent(annotation, key -> {
            try {
                Class<? extends Annotation> annoClazz = annotation.annotationType();
                SpelConstraint constraint = annoClazz.getAnnotation(SpelConstraint.class);
                if (constraint == null) {
                    throw new SpelValidatorException("Annotation [" + annoClazz.getName() + "] is not a Spel Constraint annotation");
                }
                Class<SpelConstraintValidator<?>> validatorClass = constraint.validatedBy();
                return validatorClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Exception e) {
                throw new SpelValidatorException("Failed to create validator instance, annotation [" + annotation.annotationType().getName() + "]", e);
            }
        });
    }
}

