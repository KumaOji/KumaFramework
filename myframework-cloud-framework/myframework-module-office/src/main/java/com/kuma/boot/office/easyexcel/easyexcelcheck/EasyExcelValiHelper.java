/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.excel.annotation.ExcelProperty
 *  jakarta.validation.ConstraintViolation
 *  jakarta.validation.Validation
 *  jakarta.validation.Validator
 *  jakarta.validation.groups.Default
 */
package com.kuma.boot.office.easyexcel.easyexcelcheck;

import com.alibaba.excel.annotation.ExcelProperty;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import java.lang.reflect.Field;
import java.util.Set;

public class EasyExcelValiHelper {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private EasyExcelValiHelper() {
    }

    public static <T> String validateEntity(T obj) throws NoSuchFieldException {
        StringBuilder result = new StringBuilder();
        Set set = VALIDATOR.validate(obj, new Class[]{Default.class});
        if (set != null && !set.isEmpty()) {
            for (ConstraintViolation cv : set) {
                Field declaredField = obj.getClass().getDeclaredField(cv.getPropertyPath().toString());
                ExcelProperty annotation = declaredField.getAnnotation(ExcelProperty.class);
                result.append(annotation.value()[0]).append(cv.getMessage()).append(";");
            }
        }
        return result.toString();
    }
}

