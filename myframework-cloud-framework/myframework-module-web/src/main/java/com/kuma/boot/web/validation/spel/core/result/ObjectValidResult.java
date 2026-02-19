/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.core.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectValidResult {
    private final List<FieldError> errors = new ArrayList<FieldError>();
    public static final ObjectValidResult EMPTY = new ObjectValidResult();

    public boolean hasError() {
        return !this.errors.isEmpty();
    }

    public boolean noneError() {
        return this.errors.isEmpty();
    }

    public List<FieldError> getErrors() {
        return Collections.unmodifiableList(this.errors);
    }

    public int getErrorSize() {
        return this.errors.size();
    }

    public void addFieldResults(List<FieldValidResult> results) {
        for (FieldValidResult result : results) {
            this.addFieldResult(result);
        }
    }

    public void addFieldResult(FieldValidResult result) {
        if (!result.isSuccess()) {
            this.errors.add(FieldError.of(result.getFieldName(), result.getMessage()));
        }
    }

    public void addFieldError(List<FieldError> fieldErrorList) {
        if (fieldErrorList != null && !fieldErrorList.isEmpty()) {
            this.errors.addAll(fieldErrorList);
        }
    }
}

