/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.core.annotation.AnnotationUtils
 */
package com.kuma.boot.web.error.mapper;

import com.kuma.boot.web.error.ErrorHandlingProperties;
import com.kuma.boot.web.error.ResponseErrorCode;
import java.util.Locale;
import org.springframework.core.annotation.AnnotationUtils;

public class ErrorCodeMapper {
    private final ErrorHandlingProperties properties;

    public ErrorCodeMapper(ErrorHandlingProperties properties) {
        this.properties = properties;
    }

    public String getErrorCode(Throwable exception) {
        String code = this.getErrorCodeFromPropertiesOrAnnotation(exception.getClass());
        if (code != null) {
            return code;
        }
        switch (this.properties.getDefaultErrorCodeStrategy()) {
            case FULL_QUALIFIED_NAME: {
                return exception.getClass().getName();
            }
            case ALL_CAPS: {
                return this.convertToAllCaps(exception.getClass().getSimpleName());
            }
        }
        throw new IllegalArgumentException("Unknown default error code strategy: " + String.valueOf((Object)this.properties.getDefaultErrorCodeStrategy()));
    }

    public String getErrorCode(String fieldSpecificErrorCode, String errorCode) {
        if (this.properties.getCodes().containsKey(fieldSpecificErrorCode)) {
            return this.properties.getCodes().get(fieldSpecificErrorCode);
        }
        return this.getErrorCode(errorCode);
    }

    public String getErrorCode(String errorCode) {
        if (this.properties.getCodes().containsKey(errorCode)) {
            return this.properties.getCodes().get(errorCode);
        }
        return errorCode;
    }

    private String convertToAllCaps(String exceptionClassName) {
        String result = exceptionClassName.replaceFirst("Exception$", "");
        result = result.replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase(Locale.ENGLISH);
        return result;
    }

    private String getErrorCodeFromPropertiesOrAnnotation(Class<?> exceptionClass) {
        if (exceptionClass == null) {
            return null;
        }
        String exceptionClassName = exceptionClass.getName();
        if (this.properties.getCodes().containsKey(exceptionClassName)) {
            return this.properties.getCodes().get(exceptionClassName);
        }
        ResponseErrorCode errorCodeAnnotation = (ResponseErrorCode)AnnotationUtils.getAnnotation(exceptionClass, ResponseErrorCode.class);
        if (errorCodeAnnotation != null) {
            return errorCodeAnnotation.value();
        }
        if (this.properties.isSearchSuperClassHierarchy()) {
            return this.getErrorCodeFromPropertiesOrAnnotation(exceptionClass.getSuperclass());
        }
        return null;
    }
}

