/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.kuma.boot.web.validation.spel.core;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.validation.spel.core.exception.SpelNotSupportedTypeException;
import com.kuma.boot.web.validation.spel.core.exception.SpelValidatorException;
import com.kuma.boot.web.validation.spel.core.manager.AnnotationMethodManager;
import com.kuma.boot.web.validation.spel.core.manager.ValidatorInstanceManager;
import com.kuma.boot.web.validation.spel.core.message.ValidatorMessageInterpolator;
import com.kuma.boot.web.validation.spel.core.parse.SpelParser;
import com.kuma.boot.web.validation.spel.core.result.FieldValidResult;
import com.kuma.boot.web.validation.spel.core.result.ObjectValidResult;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpelValidExecutor {
    private static final String MESSAGE = "message";
    private static final String CONDITION = "condition";
    private static final String GROUP = "group";
    private static final ConcurrentHashMap<Class<?>, List<Field>> FIELD_CACHE = new ConcurrentHashMap();
    private static final ConcurrentHashMap<Field, List<Annotation>> FIELD_ANNOTATION_CACHE = new ConcurrentHashMap();
    private static final ValidatorMessageInterpolator MESSAGE_INTERPOLATOR = new ValidatorMessageInterpolator();

    private SpelValidExecutor() {
    }

    @NotNull
    public static ObjectValidResult validateObject(@NotNull Object verifiedObject) {
        return SpelValidExecutor.validateObject(verifiedObject, (String[])null);
    }

    @NotNull
    public static ObjectValidResult validateObject(@NotNull Object verifiedObject, String ... groups) {
        return SpelValidExecutor.validateObject(verifiedObject, groups, null);
    }

    @NotNull
    public static ObjectValidResult validateObject(@NotNull Object verifiedObject, String[] groups, SpelValidContext context) {
        groups = groups == null ? new String[]{} : groups;
        context = context == null ? SpelValidContext.getDefault() : context;
        return SpelValidExecutor.validateObject(verifiedObject, SpelValidExecutor.parseGroups(verifiedObject, groups), context);
    }

    @NotNull
    public static ObjectValidResult validateObject(@NotNull Object verifiedObject, @NotNull Set<Object> validateGroups, @NotNull SpelValidContext context) {
        Objects.requireNonNull(verifiedObject);
        Objects.requireNonNull(validateGroups);
        Objects.requireNonNull(context);
        long startTime = System.nanoTime();
        LogUtils.debug((String)"Spel validate start, class [{}], groups [{}], context [{}]", (Object[])new Object[]{verifiedObject.getClass().getName(), validateGroups, context});
        LogUtils.debug((String)"Verified object [{}]", (Object[])new Object[]{verifiedObject});
        ObjectValidResult validResult = new ObjectValidResult();
        List<Field> spelConstraintFields = SpelValidExecutor.getSpelConstraintFields(verifiedObject.getClass());
        for (Field field : spelConstraintFields) {
            List<Annotation> spelConstraintAnnotations = SpelValidExecutor.getSpelConstraintAnnotations(field);
            for (Annotation annotation : spelConstraintAnnotations) {
                SpelConstraintValidator<? extends Annotation> validator;
                FieldValidResult validationResult = SpelValidExecutor.validateFieldAnnotation(annotation, validator = ValidatorInstanceManager.getInstance(annotation), verifiedObject, field, validateGroups, context);
                if (validationResult == null) continue;
                validResult.addFieldResult(validationResult);
            }
        }
        LogUtils.debug((String)"Spel validate over,error list {}", (Object[])new Object[]{validResult.getErrors()});
        LogUtils.debug((String)"Spel validate cost time {} ms", (Object[])new Object[]{(System.nanoTime() - startTime) / 1000000L});
        return validResult;
    }

    @NotNull
    private static List<Field> getSpelConstraintFields(@NotNull Class<?> clazz) {
        return FIELD_CACHE.computeIfAbsent(clazz, aClass -> {
            ArrayList<Field> list = new ArrayList<Field>();
            while (aClass != null) {
                Field[] fields;
                for (Field field : fields = aClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    if (SpelValidExecutor.getSpelConstraintAnnotations(field).isEmpty()) continue;
                    list.add(field);
                }
                aClass = aClass.getSuperclass();
            }
            return Collections.unmodifiableList(list);
        });
    }

    @NotNull
    private static List<Annotation> getSpelConstraintAnnotations(@NotNull Field field) {
        return FIELD_ANNOTATION_CACHE.computeIfAbsent(field, f -> {
            Annotation[] annotations = f.getAnnotations();
            ArrayList<Annotation> tempList = new ArrayList<Annotation>();
            for (Annotation originalAnno : annotations) {
                String annoName = originalAnno.annotationType().getName();
                if (annoName.endsWith("$List") || annoName.endsWith("Container")) {
                    Class<?> clazz = originalAnno.annotationType().getDeclaringClass();
                    Annotation[] originalAnnoArray = f.getAnnotationsByType(clazz);
                    tempList.addAll(Arrays.asList(originalAnnoArray));
                    continue;
                }
                tempList.add(originalAnno);
            }
            tempList.removeIf(annotation -> !SpelValidExecutor.isSpelConstraintAnnotation(annotation.annotationType()));
            return Collections.unmodifiableList(tempList);
        });
    }

    @Nullable
    private static FieldValidResult validateFieldAnnotation(@NotNull Annotation annotation, @NotNull SpelConstraintValidator<? extends Annotation> validator, @NotNull Object verifiedObject, @NotNull Field verifiedField, @NotNull Set<Object> validateGroups, @NotNull SpelValidContext context) {
        LogUtils.debug((String)"===> Find target annotation [{}], verifiedField [{}]", (Object[])new Object[]{annotation.annotationType().getSimpleName(), verifiedField.getName()});
        LogUtils.debug((String)"===> Annotation object [{}]", (Object[])new Object[]{annotation});
        Set<Class<?>> supported = validator.supportType();
        Class<?> verifiedFieldClass = verifiedField.getType();
        if (supported.stream().noneMatch(clazz -> clazz.isAssignableFrom(verifiedFieldClass))) {
            LogUtils.error((String)"===> Object type not supported, skip validate. Current type[{}], supported types [{}]", (Object[])new Object[]{verifiedFieldClass, supported});
            throw new SpelNotSupportedTypeException(verifiedFieldClass, supported);
        }
        Set<Object> annoGroups = SpelValidExecutor.parseGroups(verifiedObject, (String[])SpelValidExecutor.getAnnotationValue(annotation, GROUP));
        if (!SpelValidExecutor.matchGroup(validateGroups, annoGroups)) {
            LogUtils.debug((String)"===> Group not matched, skip validate. annotation groups [{}]", (Object[])new Object[]{annoGroups});
            return null;
        }
        String condition = (String)SpelValidExecutor.getAnnotationValue(annotation, CONDITION);
        if (!condition.isEmpty() && !SpelParser.parse(condition, verifiedObject, Boolean.class).booleanValue()) {
            LogUtils.debug((String)"===> Condition not valid, skip validate. condition [{}]", (Object[])new Object[]{condition});
            return null;
        }
        FieldValidResult validationResult = SpelValidExecutor.doValidate(validator, annotation, verifiedObject, verifiedField);
        SpelValidExecutor.fillValidResult(validationResult, annotation, verifiedField, context.getLocale());
        LogUtils.debug((String)"===> Validate result [{}]", (Object[])new Object[]{validationResult.isSuccess()});
        return validationResult;
    }

    @NotNull
    private static <A extends Annotation> FieldValidResult doValidate(@NotNull SpelConstraintValidator<?> validator, @NotNull A annotation, @NotNull Object verifiedObject, @NotNull Field verifiedField) {
        try {
            return validator.isValid(annotation, verifiedObject, verifiedField);
        }
        catch (SpelValidatorException e) {
            LogUtils.error((String)"Spel validate error: {}; Located in the annotation [{}] of class [{}] field [{}]", (Object[])new Object[]{e.getMessage(), annotation.annotationType().getName(), verifiedObject.getClass().getName(), verifiedField.getName()});
            throw e;
        }
        catch (IllegalAccessException e) {
            LogUtils.error((String)"The validated field [{}] is not accessible in the class [{}]", (Object[])new Object[]{verifiedField.getName(), verifiedObject.getClass().getName()});
            throw new SpelValidatorException("Failed to access field value", e);
        }
    }

    private static boolean isSpelConstraintAnnotation(@NotNull Class<? extends Annotation> annotationType) {
        if (!annotationType.isAnnotationPresent(SpelConstraint.class)) {
            return false;
        }
        if (AnnotationMethodManager.get(annotationType, MESSAGE) == null) {
            LogUtils.warn((String)"The annotation [{}] must have a method named [message] that returns a string.", (Object[])new Object[]{annotationType.getName()});
            return false;
        }
        if (AnnotationMethodManager.get(annotationType, CONDITION) == null) {
            LogUtils.warn((String)"The annotation [{}] must have a method named [condition] that returns a string.", (Object[])new Object[]{annotationType.getName()});
            return false;
        }
        if (AnnotationMethodManager.get(annotationType, GROUP) == null) {
            LogUtils.warn((String)"The annotation [{}] must have a method named [group] that returns a Array<String>.", (Object[])new Object[]{annotationType.getName()});
            return false;
        }
        return true;
    }

    private static boolean matchGroup(@NotNull Set<Object> validateGroups, @NotNull Set<Object> annoGroups) {
        if (annoGroups.isEmpty() || validateGroups.isEmpty()) {
            return true;
        }
        return validateGroups.stream().anyMatch(annoGroups::contains);
    }

    @NotNull
    public static Set<Object> parseGroups(@NotNull Object object, String ... groups) {
        Objects.requireNonNull(object);
        Objects.requireNonNull(groups);
        HashSet<Object> parsedGroups = new HashSet<Object>();
        for (String group : groups) {
            parsedGroups.add(SpelParser.parse(group, object));
        }
        return parsedGroups;
    }

    private static void fillValidResult(@NotNull FieldValidResult result, @NotNull Annotation annotation, @NotNull Field verifiedField, Locale locale) {
        if (result.isSuccess()) {
            return;
        }
        String message = result.getMessage().isEmpty() ? (String)SpelValidExecutor.getAnnotationValue(annotation, MESSAGE) : result.getMessage();
        String interpolateMessage = MESSAGE_INTERPOLATOR.interpolate(message, locale, result.getArgs());
        result.setMessage(interpolateMessage);
        if (result.getFieldName().isEmpty()) {
            result.setFieldName(verifiedField.getName());
        }
    }

    private static <T> T getAnnotationValue(@NotNull Annotation annotation, @NotNull String methodName) {
        Method method = AnnotationMethodManager.get(annotation.annotationType(), methodName);
        try {
            return (T)method.invoke((Object)annotation, new Object[0]);
        }
        catch (Exception e) {
            throw new SpelValidatorException("Get method [" + annotation.annotationType().getName() + "." + methodName + "] error: " + e.getMessage(), e);
        }
    }
}

