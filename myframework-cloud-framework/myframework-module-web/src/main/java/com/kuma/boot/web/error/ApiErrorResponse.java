/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAnyGetter
 *  com.fasterxml.jackson.annotation.JsonIgnore
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  org.springframework.http.HttpStatus
 */
package com.kuma.boot.web.error;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

@JsonInclude(value=JsonInclude.Include.NON_EMPTY)
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
        this.properties = new HashMap<String, Object>();
        this.fieldErrors = new ArrayList<ApiFieldError>();
        this.globalErrors = new ArrayList<ApiGlobalError>();
        this.parameterErrors = new ArrayList<ApiParameterError>();
    }

    @JsonIgnore
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public List<ApiFieldError> getFieldErrors() {
        return this.fieldErrors;
    }

    public List<ApiGlobalError> getGlobalErrors() {
        return this.globalErrors;
    }

    public List<ApiParameterError> getParameterErrors() {
        return this.parameterErrors;
    }

    public void addErrorProperties(Map<String, Object> errorProperties) {
        this.properties.putAll(errorProperties);
    }

    public void addErrorProperty(String propertyName, Object propertyValue) {
        this.properties.put(propertyName, propertyValue);
    }

    public void addFieldError(ApiFieldError fieldError) {
        this.fieldErrors.add(fieldError);
    }

    public void addGlobalError(ApiGlobalError globalError) {
        this.globalErrors.add(globalError);
    }

    public void addParameterError(ApiParameterError parameterError) {
        this.parameterErrors.add(parameterError);
    }
}

