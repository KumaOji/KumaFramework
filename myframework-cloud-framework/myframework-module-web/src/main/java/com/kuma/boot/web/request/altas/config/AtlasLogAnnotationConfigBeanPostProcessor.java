/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.annotation.PostConstruct
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.kuma.boot.web.request.altas.config;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class AtlasLogAnnotationConfigBeanPostProcessor
implements BeanPostProcessor,
ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(AtlasLogAnnotationConfigBeanPostProcessor.class);
    private final Map<String, Object> annotationAttributes;
    private ApplicationContext applicationContext;
    private LogConfigProperties annotationConfig;

    public AtlasLogAnnotationConfigBeanPostProcessor(Map<String, Object> annotationAttributes) {
        this.annotationAttributes = annotationAttributes;
        logger.debug("=== AtlasLogAnnotationConfigProcessor Constructor Debug ===");
        logger.debug("Received annotationAttributes: {}", annotationAttributes);
        if (annotationAttributes != null) {
            logger.debug("annotationAttributes keys: {}", annotationAttributes.keySet());
            Object httpLog = annotationAttributes.get("httpLog");
            logger.debug("httpLog attribute: {}", httpLog);
            logger.debug("httpLog type: {}", httpLog != null ? httpLog.getClass() : "null");
            if (httpLog instanceof Map) {
                Map httpLogMap = (Map)httpLog;
                logger.debug("httpLog map keys: {}", httpLogMap.keySet());
                Object urlFormat = httpLogMap.get("urlFormat");
                logger.debug("urlFormat from httpLog: '{}'", urlFormat);
            }
        } else {
            logger.debug("annotationAttributes is null!");
        }
        logger.debug("============================================================");
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void processAnnotationConfig() {
        logger.info("Processing Atlas Log annotation configuration...");
        try {
            this.annotationConfig = this.parseAnnotationConfig();
            logger.info("Annotation configuration processed successfully");
            logger.debug("Parsed configuration: {}", (Object)this.annotationConfig);
        }
        catch (Exception e) {
            logger.error("Failed to process annotation configuration", (Throwable)e);
            throw new IllegalStateException("Failed to process Atlas Log annotation configuration", e);
        }
    }

    public LogConfigProperties getAnnotationConfig() {
        return this.annotationConfig;
    }

    private LogConfigProperties parseAnnotationConfig() {
        LogConfigProperties config = new LogConfigProperties();
        this.parseBasicConfig(config);
        this.parseTagsAndGroupsConfig(config);
        this.parseExclusionsConfig(config);
        this.parseNestedConfigs(config);
        return config;
    }

    private void parseBasicConfig(LogConfigProperties config) {
        config.setEnabled(this.getAttributeValue("enabled", Boolean.class, true));
        config.setDefaultLevel(this.getAttributeValue("defaultLevel", String.class, "INFO"));
        config.setDateFormat(this.getAttributeValue("dateFormat", String.class, "yyyy-MM-dd HH:mm:ss.SSS"));
        config.setPrettyPrint(this.getAttributeValue("prettyPrint", Boolean.class, false));
        config.setMaxMessageLength(this.getAttributeValue("maxMessageLength", Integer.class, 2000));
        config.setSpelEnabled(this.getAttributeValue("spelEnabled", Boolean.class, true));
        config.setConditionEnabled(this.getAttributeValue("conditionEnabled", Boolean.class, true));
        logger.debug("Parsed basic configuration: enabled={}, defaultLevel={}, dateFormat={}", new Object[]{config.isEnabled(), config.getDefaultLevel(), config.getDateFormat()});
    }

    private void parseTagsAndGroupsConfig(LogConfigProperties config) {
        String[] enabledTags = this.getAttributeValue("enabledTags", String[].class, new String[]{"business", "security", "api"});
        config.setEnabledTags(Arrays.asList(enabledTags));
        String[] enabledGroups = this.getAttributeValue("enabledGroups", String[].class, new String[]{"default", "business"});
        config.setEnabledGroups(Arrays.asList(enabledGroups));
        logger.debug("Parsed tags and groups: enabledTags={}, enabledGroups={}", config.getEnabledTags(), config.getEnabledGroups());
    }

    private void parseExclusionsConfig(LogConfigProperties config) {
        String[] exclusions = this.getAttributeValue("exclusions", String[].class, new String[]{"*.toString", "*.hashCode", "*.equals"});
        config.setExclusions(Arrays.asList(exclusions));
        logger.debug("Parsed exclusions: {}", config.getExclusions());
    }

    private void parseNestedConfigs(LogConfigProperties config) {
        this.parseTraceConfig(config);
        this.parsePerformanceConfig(config);
        this.parseConditionConfig(config);
        this.parseSensitiveConfig(config);
        this.parseHttpLogConfig(config);
        this.parseResultLogConfig(config);
    }

    private void parseTraceConfig(LogConfigProperties config) {
        Map traceAttrs = this.getAttributeValue("trace", Map.class, null);
        if (traceAttrs != null) {
            LogConfigProperties.TraceIdConfig traceConfig = config.getTraceId();
            traceConfig.setEnabled(this.getNestedAttributeValue(traceAttrs, "enabled", Boolean.class, true));
            traceConfig.setHeaderName(this.getNestedAttributeValue(traceAttrs, "headerName", String.class, "X-Trace-Id"));
            traceConfig.setGenerator(this.getNestedAttributeValue(traceAttrs, "generator", String.class, "uuid"));
            logger.debug("Parsed trace configuration: {}", (Object)traceConfig);
        }
    }

    private void parsePerformanceConfig(LogConfigProperties config) {
        Map perfAttrs = this.getAttributeValue("performance", Map.class, null);
        if (perfAttrs != null) {
            LogConfigProperties.PerformanceConfig perfConfig = config.getPerformance();
            perfConfig.setEnabled(this.getNestedAttributeValue(perfAttrs, "enabled", Boolean.class, true));
            perfConfig.setSlowThreshold(this.getNestedAttributeValue(perfAttrs, "slowThreshold", Long.class, 1000L));
            perfConfig.setLogSlowMethods(this.getNestedAttributeValue(perfAttrs, "logSlowMethods", Boolean.class, true));
            logger.debug("Parsed performance configuration: {}", (Object)perfConfig);
        }
    }

    private void parseConditionConfig(LogConfigProperties config) {
        Map conditionAttrs = this.getAttributeValue("condition", Map.class, null);
        if (conditionAttrs != null) {
            LogConfigProperties.ConditionConfig conditionConfig = config.getCondition();
            conditionConfig.setCacheEnabled(this.getNestedAttributeValue(conditionAttrs, "cacheEnabled", Boolean.class, true));
            conditionConfig.setTimeoutMs(this.getNestedAttributeValue(conditionAttrs, "timeoutMs", Long.class, 1000L));
            conditionConfig.setFailSafe(this.getNestedAttributeValue(conditionAttrs, "failSafe", Boolean.class, true));
            logger.debug("Parsed condition configuration: {}", (Object)conditionConfig);
        }
    }

    private void parseSensitiveConfig(LogConfigProperties config) {
        Map sensitiveAttrs = this.getAttributeValue("sensitive", Map.class, null);
        if (sensitiveAttrs != null) {
            LogConfigProperties.SensitiveConfig sensitiveConfig = config.getSensitive();
            sensitiveConfig.setEnabled(this.getNestedAttributeValue(sensitiveAttrs, "enabled", Boolean.class, true));
            sensitiveConfig.setMaskValue(this.getNestedAttributeValue(sensitiveAttrs, "maskValue", String.class, "***"));
            String[] customFields = this.getNestedAttributeValue(sensitiveAttrs, "customFields", String[].class, new String[]{"bankCard", "idCard", "socialSecurityNumber"});
            sensitiveConfig.setCustomFields(Arrays.asList(customFields));
            logger.debug("Parsed sensitive configuration: {}", (Object)sensitiveConfig);
        }
    }

    private void parseHttpLogConfig(LogConfigProperties config) {
        Map httpLogAttrs = this.getAttributeValue("httpLog", Map.class, null);
        logger.debug("parseHttpLogConfig - httpLogAttrs: {}", (Object)httpLogAttrs);
        if (httpLogAttrs != null) {
            LogConfigProperties.HttpLogConfig httpLogConfig = config.getHttpLog();
            String urlFormat = this.getNestedAttributeValue(httpLogAttrs, "urlFormat", String.class, "");
            logger.debug("parseHttpLogConfig - urlFormat from annotation: '{}'", (Object)urlFormat);
            httpLogConfig.setUrlFormat(urlFormat);
            httpLogConfig.setLogFullParameters(this.getNestedAttributeValue(httpLogAttrs, "logFullParameters", Boolean.class, true));
            httpLogConfig.setIncludeQueryString(this.getNestedAttributeValue(httpLogAttrs, "includeQueryString", Boolean.class, true));
            httpLogConfig.setIncludeHeaders(this.getNestedAttributeValue(httpLogAttrs, "includeHeaders", Boolean.class, false));
            String[] excludeHeaders = this.getNestedAttributeValue(httpLogAttrs, "excludeHeaders", String[].class, new String[]{"authorization", "cookie", "x-auth-token"});
            httpLogConfig.setExcludeHeaders(Arrays.asList(excludeHeaders));
            logger.debug("parseHttpLogConfig - Final HTTP log configuration: urlFormat='{}', includeQueryString={}, logFullParameters={}", new Object[]{httpLogConfig.getUrlFormat(), httpLogConfig.isIncludeQueryString(), httpLogConfig.isLogFullParameters()});
        } else {
            logger.debug("parseHttpLogConfig - No httpLog annotation found, using defaults");
        }
    }

    private void parseResultLogConfig(LogConfigProperties config) {
        Map resultLogAttrs = this.getAttributeValue("resultLog", Map.class, null);
        if (resultLogAttrs != null) {
            LogConfigProperties.ResultLogConfig resultLogConfig = config.getResultLog();
            resultLogConfig.setEnabled(this.getNestedAttributeValue(resultLogAttrs, "enabled", Boolean.class, true));
            resultLogConfig.setMaxLength(this.getNestedAttributeValue(resultLogAttrs, "maxLength", Integer.class, 1000));
            resultLogConfig.setPrintAll(this.getNestedAttributeValue(resultLogAttrs, "printAll", Boolean.class, false));
            resultLogConfig.setTruncateMessage(this.getNestedAttributeValue(resultLogAttrs, "truncateMessage", String.class, "[TRUNCATED]"));
            logger.debug("Parsed result log configuration: {}", (Object)resultLogConfig);
        }
    }

    private <T> T getAttributeValue(String attributeName, Class<T> expectedType, T defaultValue) {
        Object value = this.annotationAttributes.get(attributeName);
        if (value == null) {
            return defaultValue;
        }
        if (expectedType.isInstance(value)) {
            return (T)value;
        }
        logger.warn("Attribute '{}' expected type {} but got {}, using default value", new Object[]{attributeName, expectedType.getSimpleName(), value.getClass().getSimpleName()});
        return defaultValue;
    }

    private <T> T getNestedAttributeValue(Map<String, Object> nestedAttrs, String attributeName, Class<T> expectedType, T defaultValue) {
        Object value = nestedAttrs.get(attributeName);
        if (value == null) {
            return defaultValue;
        }
        if (expectedType.isInstance(value)) {
            return (T)value;
        }
        logger.warn("Nested attribute '{}' expected type {} but got {}, using default value", new Object[]{attributeName, expectedType.getSimpleName(), value.getClass().getSimpleName()});
        return defaultValue;
    }
}

