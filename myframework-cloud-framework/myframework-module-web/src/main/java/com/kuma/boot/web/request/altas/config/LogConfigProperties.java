/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 */
package com.kuma.boot.web.request.altas.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="atlas.log")
public class LogConfigProperties {
    private boolean enabled = true;
    private String defaultLevel = "INFO";
    private String dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";
    private boolean prettyPrint = false;
    private int maxMessageLength = 2000;
    private boolean spelEnabled = true;
    private boolean conditionEnabled = true;
    private List<String> enabledTags = new ArrayList<String>();
    private List<String> enabledGroups = new ArrayList<String>();
    private List<String> exclusions = new ArrayList<String>();
    private TraceIdConfig traceId = new TraceIdConfig();
    private PerformanceConfig performance = new PerformanceConfig();
    private ConditionConfig condition = new ConditionConfig();
    private SensitiveConfig sensitive = new SensitiveConfig();
    private ArgumentFormatConfig argumentFormat = new ArgumentFormatConfig();
    private HttpLogConfig httpLog = new HttpLogConfig();
    private ResultLogConfig resultLog = new ResultLogConfig();

    public LogConfigProperties(LogConfigProperties other) {
        if (other != null) {
            this.enabled = other.enabled;
            this.defaultLevel = other.defaultLevel;
            this.dateFormat = other.dateFormat;
            this.prettyPrint = other.prettyPrint;
            this.maxMessageLength = other.maxMessageLength;
            this.spelEnabled = other.spelEnabled;
            this.conditionEnabled = other.conditionEnabled;
            this.enabledTags = new ArrayList<String>(other.enabledTags);
            this.enabledGroups = new ArrayList<String>(other.enabledGroups);
            this.exclusions = new ArrayList<String>(other.exclusions);
            this.traceId = new TraceIdConfig(other.traceId);
            this.performance = new PerformanceConfig(other.performance);
            this.condition = new ConditionConfig(other.condition);
            this.sensitive = new SensitiveConfig(other.sensitive);
            this.argumentFormat = new ArgumentFormatConfig(other.argumentFormat);
            this.httpLog = new HttpLogConfig(other.httpLog);
            this.resultLog = new ResultLogConfig(other.resultLog);
        }
    }

    public LogConfigProperties() {
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDefaultLevel() {
        return this.defaultLevel;
    }

    public void setDefaultLevel(String defaultLevel) {
        this.defaultLevel = defaultLevel;
    }

    public String getDateFormat() {
        return this.dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public boolean isPrettyPrint() {
        return this.prettyPrint;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public int getMaxMessageLength() {
        return this.maxMessageLength;
    }

    public void setMaxMessageLength(int maxMessageLength) {
        this.maxMessageLength = maxMessageLength;
    }

    public boolean isSpelEnabled() {
        return this.spelEnabled;
    }

    public void setSpelEnabled(boolean spelEnabled) {
        this.spelEnabled = spelEnabled;
    }

    public boolean isConditionEnabled() {
        return this.conditionEnabled;
    }

    public void setConditionEnabled(boolean conditionEnabled) {
        this.conditionEnabled = conditionEnabled;
    }

    public List<String> getEnabledTags() {
        return this.enabledTags;
    }

    public void setEnabledTags(List<String> enabledTags) {
        this.enabledTags = enabledTags;
    }

    public List<String> getEnabledGroups() {
        return this.enabledGroups;
    }

    public void setEnabledGroups(List<String> enabledGroups) {
        this.enabledGroups = enabledGroups;
    }

    public List<String> getExclusions() {
        return this.exclusions;
    }

    public void setExclusions(List<String> exclusions) {
        this.exclusions = exclusions;
    }

    public TraceIdConfig getTraceId() {
        return this.traceId;
    }

    public void setTraceId(TraceIdConfig traceId) {
        this.traceId = traceId;
    }

    public PerformanceConfig getPerformance() {
        return this.performance;
    }

    public void setPerformance(PerformanceConfig performance) {
        this.performance = performance;
    }

    public ConditionConfig getCondition() {
        return this.condition;
    }

    public void setCondition(ConditionConfig condition) {
        this.condition = condition;
    }

    public SensitiveConfig getSensitive() {
        return this.sensitive;
    }

    public void setSensitive(SensitiveConfig sensitive) {
        this.sensitive = sensitive;
    }

    public ArgumentFormatConfig getArgumentFormat() {
        return this.argumentFormat;
    }

    public void setArgumentFormat(ArgumentFormatConfig argumentFormat) {
        this.argumentFormat = argumentFormat;
    }

    public HttpLogConfig getHttpLog() {
        return this.httpLog;
    }

    public void setHttpLog(HttpLogConfig httpLog) {
        this.httpLog = httpLog;
    }

    public ResultLogConfig getResultLog() {
        return this.resultLog;
    }

    public void setResultLog(ResultLogConfig resultLog) {
        this.resultLog = resultLog;
    }

    public static class TraceIdConfig {
        private boolean enabled = true;
        private String headerName = "X-Trace-Id";
        private String generator = "uuid";

        public TraceIdConfig(TraceIdConfig other) {
            if (other != null) {
                this.enabled = other.enabled;
                this.headerName = other.headerName;
                this.generator = other.generator;
            }
        }

        public TraceIdConfig() {
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getHeaderName() {
            return this.headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }

        public String getGenerator() {
            return this.generator;
        }

        public void setGenerator(String generator) {
            this.generator = generator;
        }
    }

    public static class PerformanceConfig {
        private boolean enabled = true;
        private long slowThreshold = 1000L;
        private boolean logSlowMethods = true;

        public PerformanceConfig(PerformanceConfig other) {
            if (other != null) {
                this.enabled = other.enabled;
                this.slowThreshold = other.slowThreshold;
                this.logSlowMethods = other.logSlowMethods;
            }
        }

        public PerformanceConfig() {
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public long getSlowThreshold() {
            return this.slowThreshold;
        }

        public void setSlowThreshold(long slowThreshold) {
            this.slowThreshold = slowThreshold;
        }

        public boolean isLogSlowMethods() {
            return this.logSlowMethods;
        }

        public void setLogSlowMethods(boolean logSlowMethods) {
            this.logSlowMethods = logSlowMethods;
        }
    }

    public static class ConditionConfig {
        private boolean cacheEnabled = true;
        private long timeoutMs = 1000L;
        private boolean failSafe = true;

        public ConditionConfig(ConditionConfig other) {
            if (other != null) {
                this.cacheEnabled = other.cacheEnabled;
                this.timeoutMs = other.timeoutMs;
                this.failSafe = other.failSafe;
            }
        }

        public ConditionConfig() {
        }

        public boolean isCacheEnabled() {
            return this.cacheEnabled;
        }

        public void setCacheEnabled(boolean cacheEnabled) {
            this.cacheEnabled = cacheEnabled;
        }

        public long getTimeoutMs() {
            return this.timeoutMs;
        }

        public void setTimeoutMs(long timeoutMs) {
            this.timeoutMs = timeoutMs;
        }

        public boolean isFailSafe() {
            return this.failSafe;
        }

        public void setFailSafe(boolean failSafe) {
            this.failSafe = failSafe;
        }
    }

    public static class SensitiveConfig {
        private boolean enabled = true;
        private List<String> customFields = new ArrayList<String>();
        private String maskValue = "***";

        public SensitiveConfig(SensitiveConfig other) {
            if (other != null) {
                this.enabled = other.enabled;
                this.customFields = new ArrayList<String>(other.customFields);
                this.maskValue = other.maskValue;
            }
        }

        public SensitiveConfig() {
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getCustomFields() {
            return this.customFields;
        }

        public void setCustomFields(List<String> customFields) {
            this.customFields = customFields;
        }

        public String getMaskValue() {
            return this.maskValue;
        }

        public void setMaskValue(String maskValue) {
            this.maskValue = maskValue;
        }
    }

    public static class ArgumentFormatConfig {
        private ArgumentFormatType type = ArgumentFormatType.JSON;
        private String separator = "&";
        private String keyValueSeparator = "=";
        private boolean includeParameterIndex = true;
        private String customFormatterName = "";
        private Map<String, Object> customFormatterConfig = new HashMap<String, Object>();

        public ArgumentFormatConfig(ArgumentFormatConfig other) {
            if (other != null) {
                this.type = other.type;
                this.separator = other.separator;
                this.keyValueSeparator = other.keyValueSeparator;
                this.includeParameterIndex = other.includeParameterIndex;
                this.customFormatterName = other.customFormatterName;
                this.customFormatterConfig = new HashMap<String, Object>(other.customFormatterConfig);
            }
        }

        public ArgumentFormatConfig() {
        }

        public ArgumentFormatType getType() {
            return this.type;
        }

        public void setType(ArgumentFormatType type) {
            this.type = type;
        }

        public String getSeparator() {
            return this.separator;
        }

        public void setSeparator(String separator) {
            this.separator = separator;
        }

        public String getKeyValueSeparator() {
            return this.keyValueSeparator;
        }

        public void setKeyValueSeparator(String keyValueSeparator) {
            this.keyValueSeparator = keyValueSeparator;
        }

        public boolean isIncludeParameterIndex() {
            return this.includeParameterIndex;
        }

        public void setIncludeParameterIndex(boolean includeParameterIndex) {
            this.includeParameterIndex = includeParameterIndex;
        }

        public String getCustomFormatterName() {
            return this.customFormatterName;
        }

        public void setCustomFormatterName(String customFormatterName) {
            this.customFormatterName = customFormatterName;
        }

        public Map<String, Object> getCustomFormatterConfig() {
            return this.customFormatterConfig;
        }

        public void setCustomFormatterConfig(Map<String, Object> customFormatterConfig) {
            this.customFormatterConfig = customFormatterConfig;
        }
    }

    public static class HttpLogConfig {
        private boolean logFullParameters = true;
        private String urlFormat = "{uri}{queryString}";
        private boolean includeQueryString = true;
        private boolean includeHeaders = false;
        private List<String> excludeHeaders = new ArrayList<String>(this){
            final /* synthetic */ HttpLogConfig this$0;
            {
                HttpLogConfig httpLogConfig = this$0;
                Objects.requireNonNull(httpLogConfig);
                this.this$0 = httpLogConfig;
                this.add("authorization");
                this.add("cookie");
                this.add("x-auth-token");
            }
        };

        public HttpLogConfig(HttpLogConfig other) {
            if (other != null) {
                this.logFullParameters = other.logFullParameters;
                this.urlFormat = other.urlFormat;
                this.includeQueryString = other.includeQueryString;
                this.includeHeaders = other.includeHeaders;
                this.excludeHeaders = new ArrayList<String>(other.excludeHeaders);
            }
        }

        public HttpLogConfig() {
        }

        public boolean isLogFullParameters() {
            return this.logFullParameters;
        }

        public void setLogFullParameters(boolean logFullParameters) {
            this.logFullParameters = logFullParameters;
        }

        public String getUrlFormat() {
            return this.urlFormat;
        }

        public void setUrlFormat(String urlFormat) {
            this.urlFormat = urlFormat;
        }

        public boolean isIncludeQueryString() {
            return this.includeQueryString;
        }

        public void setIncludeQueryString(boolean includeQueryString) {
            this.includeQueryString = includeQueryString;
        }

        public boolean isIncludeHeaders() {
            return this.includeHeaders;
        }

        public void setIncludeHeaders(boolean includeHeaders) {
            this.includeHeaders = includeHeaders;
        }

        public List<String> getExcludeHeaders() {
            return this.excludeHeaders;
        }

        public void setExcludeHeaders(List<String> excludeHeaders) {
            this.excludeHeaders = excludeHeaders;
        }
    }

    public static class ResultLogConfig {
        private boolean enabled = true;
        private int maxLength = 1000;
        private boolean printAll = false;
        private String truncateMessage = "[TRUNCATED]";

        public ResultLogConfig(ResultLogConfig other) {
            if (other != null) {
                this.enabled = other.enabled;
                this.maxLength = other.maxLength;
                this.printAll = other.printAll;
                this.truncateMessage = other.truncateMessage;
            }
        }

        public ResultLogConfig() {
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getMaxLength() {
            return this.maxLength;
        }

        public void setMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }

        public boolean isPrintAll() {
            return this.printAll;
        }

        public void setPrintAll(boolean printAll) {
            this.printAll = printAll;
        }

        public String getTruncateMessage() {
            return this.truncateMessage;
        }

        public void setTruncateMessage(String truncateMessage) {
            this.truncateMessage = truncateMessage;
        }
    }

    public static enum ArgumentFormatType {
        JSON,
        KEY_VALUE,
        CUSTOM;

    }
}

