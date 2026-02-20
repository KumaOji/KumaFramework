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

/**
 * This class contains the logic for getting the matching error message for the given {@link
 * Throwable}.
 */
public class ErrorMessageMapper {

    private final ErrorHandlingProperties properties;

    public ErrorMessageMapper( ErrorHandlingProperties properties) {
        this.properties = properties;
    }

    public String getErrorMessage(Throwable exception) {
        String code = getErrorMessageFromProperties(exception.getClass());
        if (code != null) {
            return code;
        }
        return exception.getMessage();
    }

    public String getErrorMessage(String fieldSpecificCode, String code, String defaultMessage) {
        if (properties.getMessages().containsKey(fieldSpecificCode)) {
            return properties.getMessages().get(fieldSpecificCode);
        }

        return getErrorMessage(code, defaultMessage);
    }

    public String getErrorMessage(String code, String defaultMessage) {
        if (properties.getMessages().containsKey(code)) {
            return properties.getMessages().get(code);
        }

        return defaultMessage;
    }

    private String getErrorMessageFromProperties(Class<?> exceptionClass) {
        if (exceptionClass == null) {
            return null;
        }
        String exceptionClassName = exceptionClass.getName();
        if (properties.getMessages().containsKey(exceptionClassName)) {
            return properties.getMessages().get(exceptionClassName);
        }
        if (properties.isSearchSuperClassHierarchy()) {
            return getErrorMessageFromProperties(exceptionClass.getSuperclass());
        } else {
            return null;
        }
    }
}
