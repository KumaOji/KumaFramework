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

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.util.List;
import java.util.Map;

/**
 * ApiErrorResponseSerializer
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-01-12 08:57:05
 */
//@JsonComponent
public class ApiErrorResponseSerializer extends ValueSerializer<com.kuma.boot.web.error.ApiErrorResponse> {

    private final ErrorHandlingProperties properties;

    public ApiErrorResponseSerializer( ErrorHandlingProperties properties) {
        this.properties = properties;
    }

    @Override
    public void serialize(com.kuma.boot.web.error.ApiErrorResponse errorResponse, JsonGenerator jsonGenerator, SerializationContext ctxt)
            throws JacksonException {
        jsonGenerator.writeStartObject();
        if (properties.isHttpStatusInJsonResponse()) {
            jsonGenerator.writeNumberProperty("status", errorResponse.getHttpStatus().value());
        }
        ErrorHandlingProperties.JsonFieldNames fieldNames = properties.getJsonFieldNames();
        jsonGenerator.writeStringProperty(fieldNames.getCode(), errorResponse.getCode());
        jsonGenerator.writeStringProperty(fieldNames.getMessage(), errorResponse.getMessage());

        List<ApiFieldError> fieldErrors = errorResponse.getFieldErrors();
        if (!fieldErrors.isEmpty()) {
//			jsonGenerator.writeArrayFieldStart(fieldNames.getFieldErrors());
            for (ApiFieldError fieldError : fieldErrors) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringProperty(fieldNames.getCode(), fieldError.getCode());
                jsonGenerator.writeStringProperty(fieldNames.getMessage(), fieldError.getMessage());
                jsonGenerator.writeStringProperty("property", fieldError.getProperty());
//				jsonGenerator.writeObjectProperty("rejectedValue", fieldError.getRejectedValue());
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();
        }

        List<ApiGlobalError> globalErrors = errorResponse.getGlobalErrors();
        if (!globalErrors.isEmpty()) {
//			jsonGenerator.writeArrayFieldStart(fieldNames.getGlobalErrors());
            for (ApiGlobalError globalError : globalErrors) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringProperty(fieldNames.getCode(), globalError.getCode());
                jsonGenerator.writeStringProperty(fieldNames.getMessage(), globalError.getMessage());
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();
        }

        List<ApiParameterError> parameterErrors = errorResponse.getParameterErrors();
        if (!parameterErrors.isEmpty()) {
//			jsonGenerator.writeArrayFieldStart(fieldNames.getParameterErrors());
            for (ApiParameterError parameterError : parameterErrors) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringProperty(fieldNames.getCode(), parameterError.getCode());
                jsonGenerator.writeStringProperty(
                        fieldNames.getMessage(), parameterError.getMessage());
                jsonGenerator.writeStringProperty("parameter", parameterError.getParameter());
//				jsonGenerator.writeObjectField("rejectedValue", parameterError.getRejectedValue());
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();
        }

        Map<String, Object> properties = errorResponse.getProperties();
        for (String property : properties.keySet()) {
//			jsonGenerator.writeObjectField(property, properties.get(property));
        }

        jsonGenerator.writeEndObject();
    }
}
