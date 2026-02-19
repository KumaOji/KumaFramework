/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.web.request.altas.config;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AnnotationConfigValidator {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationConfigValidator.class);
    private static final List<String> VALID_LOG_LEVELS = Arrays.asList("TRACE", "DEBUG", "INFO", "WARN", "ERROR", "OFF");
    private static final List<String> VALID_GENERATORS = Arrays.asList("uuid", "snowflake");
    private static final Pattern METHOD_PATTERN = Pattern.compile("^[*a-zA-Z_$][a-zA-Z0-9_.$]*\\.[*a-zA-Z_$][a-zA-Z0-9_$]*$");

    public void validate(LogConfigProperties config) {
        if (config == null) {
            throw new IllegalArgumentException("Atlas Log configuration cannot be null");
        }
        logger.debug("Starting Atlas Log configuration validation...");
        try {
            this.validateBasicConfig(config);
            this.validateTagsConfig(config);
            this.validateExclusionsConfig(config);
            this.validateNestedConfigs(config);
            logger.info("Atlas Log configuration validation passed successfully");
        }
        catch (Exception e) {
            logger.error("Atlas Log configuration validation failed: {}", (Object)e.getMessage());
            throw new IllegalArgumentException("Invalid Atlas Log configuration: " + e.getMessage(), e);
        }
    }

    private void validateBasicConfig(LogConfigProperties config) {
        this.validateLogLevel(config.getDefaultLevel());
        this.validateDateFormat(config.getDateFormat());
        if (config.getMaxMessageLength() <= 0) {
            throw new IllegalArgumentException("maxMessageLength must be positive, got: " + config.getMaxMessageLength());
        }
        if (config.getMaxMessageLength() > 100000) {
            logger.warn("maxMessageLength is very large ({}), this might affect performance", (Object)config.getMaxMessageLength());
        }
        logger.debug("Basic configuration validation passed");
    }

    private void validateLogLevel(String level) {
        if (level == null || level.trim().isEmpty()) {
            throw new IllegalArgumentException("defaultLevel cannot be null or empty");
        }
        String upperLevel = level.toUpperCase();
        if (!VALID_LOG_LEVELS.contains(upperLevel)) {
            throw new IllegalArgumentException("Invalid log level: " + level + ". Valid levels are: " + String.valueOf(VALID_LOG_LEVELS));
        }
    }

    private void validateDateFormat(String dateFormat) {
        if (dateFormat == null || dateFormat.trim().isEmpty()) {
            throw new IllegalArgumentException("dateFormat cannot be null or empty");
        }
        try {
            new SimpleDateFormat(dateFormat);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date format: " + dateFormat, e);
        }
    }

    private void validateTagsConfig(LogConfigProperties config) {
        this.validateStringList(config.getEnabledTags(), "enabledTags");
        this.validateStringList(config.getEnabledGroups(), "enabledGroups");
        this.validateNonEmptyElements(config.getEnabledTags(), "enabledTags");
        this.validateNonEmptyElements(config.getEnabledGroups(), "enabledGroups");
        logger.debug("Tags configuration validation passed");
    }

    private void validateExclusionsConfig(LogConfigProperties config) {
        this.validateStringList(config.getExclusions(), "exclusions");
        for (String exclusion : config.getExclusions()) {
            if (exclusion == null || exclusion.trim().isEmpty()) continue;
            this.validateMethodPattern(exclusion);
        }
        logger.debug("Exclusions configuration validation passed");
    }

    private void validateMethodPattern(String pattern) {
        if (!METHOD_PATTERN.matcher(pattern).matches()) {
            throw new IllegalArgumentException("Invalid method exclusion pattern: " + pattern + ". Pattern should be like 'ClassName.methodName', '*.methodName' or 'ClassName.*'");
        }
    }

    private void validateNestedConfigs(LogConfigProperties config) {
        this.validateTraceConfig(config.getTraceId());
        this.validatePerformanceConfig(config.getPerformance());
        this.validateConditionConfig(config.getCondition());
        this.validateSensitiveConfig(config.getSensitive());
        logger.debug("Nested configurations validation passed");
    }

    private void validateTraceConfig(LogConfigProperties.TraceIdConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("TraceId configuration cannot be null");
        }
        if (config.getHeaderName() == null || config.getHeaderName().trim().isEmpty()) {
            throw new IllegalArgumentException("TraceId headerName cannot be null or empty");
        }
        if (config.getGenerator() == null || config.getGenerator().trim().isEmpty()) {
            throw new IllegalArgumentException("TraceId generator cannot be null or empty");
        }
        if (!VALID_GENERATORS.contains(config.getGenerator().toLowerCase())) {
            throw new IllegalArgumentException("Invalid TraceId generator: " + config.getGenerator() + ". Valid generators are: " + String.valueOf(VALID_GENERATORS));
        }
    }

    private void validatePerformanceConfig(LogConfigProperties.PerformanceConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Performance configuration cannot be null");
        }
        if (config.getSlowThreshold() < 0L) {
            throw new IllegalArgumentException("Performance slowThreshold must be non-negative, got: " + config.getSlowThreshold());
        }
        if (config.getSlowThreshold() > 60000L) {
            logger.warn("Performance slowThreshold is very large ({}ms), consider reducing it", (Object)config.getSlowThreshold());
        }
    }

    private void validateConditionConfig(LogConfigProperties.ConditionConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Condition configuration cannot be null");
        }
        if (config.getTimeoutMs() <= 0L) {
            throw new IllegalArgumentException("Condition timeoutMs must be positive, got: " + config.getTimeoutMs());
        }
        if (config.getTimeoutMs() > 10000L) {
            logger.warn("Condition timeoutMs is very large ({}ms), this might affect performance", (Object)config.getTimeoutMs());
        }
    }

    private void validateSensitiveConfig(LogConfigProperties.SensitiveConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Sensitive configuration cannot be null");
        }
        if (config.getMaskValue() == null || config.getMaskValue().isEmpty()) {
            throw new IllegalArgumentException("Sensitive maskValue cannot be null or empty");
        }
        this.validateStringList(config.getCustomFields(), "sensitive.customFields");
        this.validateNonEmptyElements(config.getCustomFields(), "sensitive.customFields");
    }

    private void validateStringList(List<String> list, String fieldName) {
        if (list == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    private void validateNonEmptyElements(List<String> list, String fieldName) {
        for (int i = 0; i < list.size(); ++i) {
            String element = list.get(i);
            if (element != null && !element.trim().isEmpty()) continue;
            throw new IllegalArgumentException(fieldName + "[" + i + "] cannot be null or empty");
        }
    }
}

