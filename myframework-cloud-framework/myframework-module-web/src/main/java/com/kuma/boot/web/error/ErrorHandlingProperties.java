/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.cloud.context.config.annotation.RefreshScope
 *  org.springframework.http.HttpStatus
 */
package com.kuma.boot.web.error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;

@RefreshScope
@ConfigurationProperties(prefix="kuma.boot.web.error.handling")
public class ErrorHandlingProperties {
    public static final String PREFIX = "kuma.boot.web.error.handling";
    private boolean enabled = true;
    private JsonFieldNames jsonFieldNames = new JsonFieldNames();
    private ExceptionLogging exceptionLogging = ExceptionLogging.MESSAGE_ONLY;
    private List<Class<? extends Throwable>> fullStacktraceClasses = new ArrayList<Class<? extends Throwable>>();
    private List<String> fullStacktraceHttpStatuses = new ArrayList<String>();
    private DefaultErrorCodeStrategy defaultErrorCodeStrategy = DefaultErrorCodeStrategy.ALL_CAPS;
    private boolean httpStatusInJsonResponse = false;
    private Map<String, HttpStatus> httpStatuses = new HashMap<String, HttpStatus>();
    private Map<String, String> codes = new HashMap<String, String>();
    private Map<String, String> messages = new HashMap<String, String>();
    private boolean searchSuperClassHierarchy = false;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public JsonFieldNames getJsonFieldNames() {
        return this.jsonFieldNames;
    }

    public void setJsonFieldNames(JsonFieldNames jsonFieldNames) {
        this.jsonFieldNames = jsonFieldNames;
    }

    public ExceptionLogging getExceptionLogging() {
        return this.exceptionLogging;
    }

    public void setExceptionLogging(ExceptionLogging exceptionLogging) {
        this.exceptionLogging = exceptionLogging;
    }

    public List<Class<? extends Throwable>> getFullStacktraceClasses() {
        return this.fullStacktraceClasses;
    }

    public void setFullStacktraceClasses(List<Class<? extends Throwable>> fullStacktraceClasses) {
        this.fullStacktraceClasses = fullStacktraceClasses;
    }

    public List<String> getFullStacktraceHttpStatuses() {
        return this.fullStacktraceHttpStatuses;
    }

    public void setFullStacktraceHttpStatuses(List<String> fullStacktraceHttpStatuses) {
        this.fullStacktraceHttpStatuses = fullStacktraceHttpStatuses;
    }

    public DefaultErrorCodeStrategy getDefaultErrorCodeStrategy() {
        return this.defaultErrorCodeStrategy;
    }

    public void setDefaultErrorCodeStrategy(DefaultErrorCodeStrategy defaultErrorCodeStrategy) {
        this.defaultErrorCodeStrategy = defaultErrorCodeStrategy;
    }

    public boolean isHttpStatusInJsonResponse() {
        return this.httpStatusInJsonResponse;
    }

    public void setHttpStatusInJsonResponse(boolean httpStatusInJsonResponse) {
        this.httpStatusInJsonResponse = httpStatusInJsonResponse;
    }

    public Map<String, HttpStatus> getHttpStatuses() {
        return this.httpStatuses;
    }

    public void setHttpStatuses(Map<String, HttpStatus> httpStatuses) {
        this.httpStatuses = httpStatuses;
    }

    public Map<String, String> getCodes() {
        return this.codes;
    }

    public void setCodes(Map<String, String> codes) {
        this.codes = codes;
    }

    public Map<String, String> getMessages() {
        return this.messages;
    }

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }

    public boolean isSearchSuperClassHierarchy() {
        return this.searchSuperClassHierarchy;
    }

    public void setSearchSuperClassHierarchy(boolean searchSuperClassHierarchy) {
        this.searchSuperClassHierarchy = searchSuperClassHierarchy;
    }

    public static class JsonFieldNames {
        private String code = "code";
        private String message = "message";
        private String fieldErrors = "fieldErrors";
        private String globalErrors = "globalErrors";
        private String parameterErrors = "parameterErrors";

        public String getCode() {
            return this.code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getFieldErrors() {
            return this.fieldErrors;
        }

        public void setFieldErrors(String fieldErrors) {
            this.fieldErrors = fieldErrors;
        }

        public String getGlobalErrors() {
            return this.globalErrors;
        }

        public void setGlobalErrors(String globalErrors) {
            this.globalErrors = globalErrors;
        }

        public String getParameterErrors() {
            return this.parameterErrors;
        }

        public void setParameterErrors(String parameterErrors) {
            this.parameterErrors = parameterErrors;
        }
    }

    static enum ExceptionLogging {
        NO_LOGGING,
        MESSAGE_ONLY,
        WITH_STACKTRACE;

    }

    public static enum DefaultErrorCodeStrategy {
        FULL_QUALIFIED_NAME,
        ALL_CAPS;

    }
}

