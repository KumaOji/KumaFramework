/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.error.mapper;

import com.kuma.boot.web.error.ErrorHandlingProperties;

public class ErrorMessageMapper {
    private final ErrorHandlingProperties properties;

    public ErrorMessageMapper(ErrorHandlingProperties properties) {
        this.properties = properties;
    }

    public String getErrorMessage(Throwable exception) {
        String code = this.getErrorMessageFromProperties(exception.getClass());
        if (code != null) {
            return code;
        }
        return exception.getMessage();
    }

    public String getErrorMessage(String fieldSpecificCode, String code, String defaultMessage) {
        if (this.properties.getMessages().containsKey(fieldSpecificCode)) {
            return this.properties.getMessages().get(fieldSpecificCode);
        }
        return this.getErrorMessage(code, defaultMessage);
    }

    public String getErrorMessage(String code, String defaultMessage) {
        if (this.properties.getMessages().containsKey(code)) {
            return this.properties.getMessages().get(code);
        }
        return defaultMessage;
    }

    private String getErrorMessageFromProperties(Class<?> exceptionClass) {
        if (exceptionClass == null) {
            return null;
        }
        String exceptionClassName = exceptionClass.getName();
        if (this.properties.getMessages().containsKey(exceptionClassName)) {
            return this.properties.getMessages().get(exceptionClassName);
        }
        if (this.properties.isSearchSuperClassHierarchy()) {
            return this.getErrorMessageFromProperties(exceptionClass.getSuperclass());
        }
        return null;
    }
}

