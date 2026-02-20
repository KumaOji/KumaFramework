/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.test.util;

import com.kuma.boot.web.validation.spel.core.result.FieldError;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConstraintViolationSet {
    private final Map<String, List<FieldError>> verifyMap;

    public ConstraintViolationSet(Collection<FieldError> fieldErrors) {
        if (fieldErrors == null || fieldErrors.isEmpty()) {
            this.verifyMap = Collections.emptyMap();
            return;
        }
        this.verifyMap = fieldErrors.stream().collect(Collectors.groupingBy(FieldError::getFieldName));
    }

    public static ConstraintViolationSet of(List<FieldError> fieldErrors) {
        return new ConstraintViolationSet(fieldErrors);
    }

    public FieldError getAndRemove(String fieldName, String expectMessage) {
        List<FieldError> violationList = this.verifyMap.get(fieldName);
        if (violationList == null || violationList.isEmpty()) {
            return null;
        }
        if (violationList.size() == 1 || expectMessage == null) {
            FieldError violation = violationList.get(0);
            this.verifyMap.remove(fieldName);
            return violation;
        }
        for (FieldError violation : violationList) {
            if (!expectMessage.equals(violation.getErrorMessage())) continue;
            violationList.remove(violation);
            return violation;
        }
        return violationList.remove(0);
    }

    public Set<FieldError> getAll() {
        return this.verifyMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }
}

