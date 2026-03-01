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

package com.kuma.boot.web.error;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ApiErrorResponse
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-01-12 08:56:55
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiErrorResponse {

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    private final Map<String, Object> properties;
    private final List<ApiFieldError> fieldErrors;
    private final List<ApiGlobalError> globalErrors;
    private final List<ApiParameterError> parameterErrors;

    public ApiErrorResponse(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
        this.properties = new HashMap<>();
        this.fieldErrors = new ArrayList<>();
        this.globalErrors = new ArrayList<>();
        this.parameterErrors = new ArrayList<>();
    }

    @JsonIgnore
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return properties;
    }

    public List<ApiFieldError> getFieldErrors() {
        return fieldErrors;
    }

    public List<ApiGlobalError> getGlobalErrors() {
        return globalErrors;
    }

    public List<ApiParameterError> getParameterErrors() {
        return parameterErrors;
    }

    public void addErrorProperties(Map<String, Object> errorProperties) {
        properties.putAll(errorProperties);
    }

    public void addErrorProperty(String propertyName, Object propertyValue) {
        properties.put(propertyName, propertyValue);
    }

    public void addFieldError( ApiFieldError fieldError) {
        fieldErrors.add(fieldError);
    }

    public void addGlobalError( ApiGlobalError globalError) {
        globalErrors.add(globalError);
    }

    public void addParameterError( ApiParameterError parameterError) {
        parameterErrors.add(parameterError);
    }
}
