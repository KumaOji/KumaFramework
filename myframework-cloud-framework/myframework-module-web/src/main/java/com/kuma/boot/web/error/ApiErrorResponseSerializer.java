/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  tools.jackson.core.JacksonException
 *  tools.jackson.core.JsonGenerator
 *  tools.jackson.databind.SerializationContext
 *  tools.jackson.databind.ValueSerializer
 */
package com.kuma.boot.web.error;

import java.util.List;
import java.util.Map;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class ApiErrorResponseSerializer
extends ValueSerializer<ApiErrorResponse> {
    private final ErrorHandlingProperties properties;

    public ApiErrorResponseSerializer(ErrorHandlingProperties properties) {
        this.properties = properties;
    }

    public void serialize(ApiErrorResponse errorResponse, JsonGenerator jsonGenerator, SerializationContext ctxt) throws JacksonException {
        List<ApiParameterError> list;
        List<ApiGlobalError> globalErrors;
        jsonGenerator.writeStartObject();
        if (this.properties.isHttpStatusInJsonResponse()) {
            jsonGenerator.writeNumberProperty("status", errorResponse.getHttpStatus().value());
        }
        ErrorHandlingProperties.JsonFieldNames fieldNames = this.properties.getJsonFieldNames();
        jsonGenerator.writeStringProperty(fieldNames.getCode(), errorResponse.getCode());
        jsonGenerator.writeStringProperty(fieldNames.getMessage(), errorResponse.getMessage());
        List<ApiFieldError> fieldErrors = errorResponse.getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            for (ApiFieldError apiFieldError : fieldErrors) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringProperty(fieldNames.getCode(), apiFieldError.getCode());
                jsonGenerator.writeStringProperty(fieldNames.getMessage(), apiFieldError.getMessage());
                jsonGenerator.writeStringProperty("property", apiFieldError.getProperty());
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();
        }
        if (!(globalErrors = errorResponse.getGlobalErrors()).isEmpty()) {
            for (ApiGlobalError apiGlobalError : globalErrors) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringProperty(fieldNames.getCode(), apiGlobalError.getCode());
                jsonGenerator.writeStringProperty(fieldNames.getMessage(), apiGlobalError.getMessage());
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();
        }
        if (!(list = errorResponse.getParameterErrors()).isEmpty()) {
            for (ApiParameterError parameterError : list) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringProperty(fieldNames.getCode(), parameterError.getCode());
                jsonGenerator.writeStringProperty(fieldNames.getMessage(), parameterError.getMessage());
                jsonGenerator.writeStringProperty("parameter", parameterError.getParameter());
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();
        }
        Map<String, Object> map = errorResponse.getProperties();
        for (String string : map.keySet()) {
        }
        jsonGenerator.writeEndObject();
    }
}

