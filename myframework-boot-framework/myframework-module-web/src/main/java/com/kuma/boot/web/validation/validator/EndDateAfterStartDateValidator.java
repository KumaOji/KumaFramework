/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.model.request.DateQuery
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 */
package com.kuma.boot.web.validation.validator;

import com.kuma.boot.common.model.request.DateQuery;
import com.kuma.boot.web.validation.annotation.EndDateAfterStartDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EndDateAfterStartDateValidator
implements ConstraintValidator<EndDateAfterStartDate, DateQuery> {
    public boolean isValid(DateQuery taskForm, ConstraintValidatorContext context) {
        if (taskForm.getStartDate() == null || taskForm.getEndDate() == null) {
            return true;
        }
        return taskForm.getEndDate().isAfter(taskForm.getStartDate());
    }
}

