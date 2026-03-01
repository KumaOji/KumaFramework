/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.web.error.mapper;

import com.kuma.boot.web.error.ErrorHandlingProperties;
import com.kuma.boot.web.error.ResponseErrorCode;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Locale;

/**
 * This class contains the logic for getting the matching error code for the given {@link
 * Throwable}.
 */
public class ErrorCodeMapper {

    private final ErrorHandlingProperties properties;

    public ErrorCodeMapper( ErrorHandlingProperties properties) {
        this.properties = properties;
    }

    public String getErrorCode(Throwable exception) {
        String code = getErrorCodeFromPropertiesOrAnnotation(exception.getClass());
        if (code != null) {
            return code;
        }
        switch (properties.getDefaultErrorCodeStrategy()) {
            case FULL_QUALIFIED_NAME:
                return exception.getClass().getName();
            case ALL_CAPS:
                return convertToAllCaps(exception.getClass().getSimpleName());
            default:
                throw new IllegalArgumentException(
                        "Unknown default error code strategy: "
                                + properties.getDefaultErrorCodeStrategy());
        }
    }

    public String getErrorCode(String fieldSpecificErrorCode, String errorCode) {
        if (properties.getCodes().containsKey(fieldSpecificErrorCode)) {
            return properties.getCodes().get(fieldSpecificErrorCode);
        }

        return getErrorCode(errorCode);
    }

    public String getErrorCode(String errorCode) {
        if (properties.getCodes().containsKey(errorCode)) {
            return properties.getCodes().get(errorCode);
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
        if (properties.getCodes().containsKey(exceptionClassName)) {
            return properties.getCodes().get(exceptionClassName);
        }
        ResponseErrorCode errorCodeAnnotation =
                AnnotationUtils.getAnnotation(exceptionClass, ResponseErrorCode.class);
        if (errorCodeAnnotation != null) {
            return errorCodeAnnotation.value();
        }

        if (properties.isSearchSuperClassHierarchy()) {
            return getErrorCodeFromPropertiesOrAnnotation(exceptionClass.getSuperclass());
        } else {
            return null;
        }
    }
}
