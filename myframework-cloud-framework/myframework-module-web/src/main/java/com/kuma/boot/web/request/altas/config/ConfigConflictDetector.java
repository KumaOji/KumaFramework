/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.web.request.altas.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConfigConflictDetector {
    private static final Logger logger = LoggerFactory.getLogger(ConfigConflictDetector.class);

    public void detectConflicts(LogConfigProperties annotationConfig, LogConfigProperties propertiesConfig) {
        if (annotationConfig == null || propertiesConfig == null) {
            logger.debug("Skipping conflict detection: one of the configurations is null");
            return;
        }
        logger.debug("Starting configuration conflict detection...");
        ArrayList<String> conflicts = new ArrayList<String>();
        this.detectBasicConfigConflicts(annotationConfig, propertiesConfig, conflicts);
        this.detectListConfigConflicts(annotationConfig, propertiesConfig, conflicts);
        this.detectNestedConfigConflicts(annotationConfig, propertiesConfig, conflicts);
        if (!conflicts.isEmpty()) {
            this.logConflicts(conflicts);
        } else {
            logger.debug("No configuration conflicts detected");
        }
    }

    private void detectBasicConfigConflicts(LogConfigProperties annotationConfig, LogConfigProperties propertiesConfig, List<String> conflicts) {
        this.detectConflict("enabled", annotationConfig.isEnabled(), propertiesConfig.isEnabled(), conflicts);
        this.detectConflict("defaultLevel", annotationConfig.getDefaultLevel(), propertiesConfig.getDefaultLevel(), conflicts);
        this.detectConflict("dateFormat", annotationConfig.getDateFormat(), propertiesConfig.getDateFormat(), conflicts);
        this.detectConflict("prettyPrint", annotationConfig.isPrettyPrint(), propertiesConfig.isPrettyPrint(), conflicts);
        this.detectConflict("maxMessageLength", annotationConfig.getMaxMessageLength(), propertiesConfig.getMaxMessageLength(), conflicts);
        this.detectConflict("spelEnabled", annotationConfig.isSpelEnabled(), propertiesConfig.isSpelEnabled(), conflicts);
        this.detectConflict("conditionEnabled", annotationConfig.isConditionEnabled(), propertiesConfig.isConditionEnabled(), conflicts);
    }

    private void detectListConfigConflicts(LogConfigProperties annotationConfig, LogConfigProperties propertiesConfig, List<String> conflicts) {
        this.detectListConflict("enabledTags", annotationConfig.getEnabledTags(), propertiesConfig.getEnabledTags(), conflicts);
        this.detectListConflict("enabledGroups", annotationConfig.getEnabledGroups(), propertiesConfig.getEnabledGroups(), conflicts);
        this.detectListConflict("exclusions", annotationConfig.getExclusions(), propertiesConfig.getExclusions(), conflicts);
    }

    private void detectNestedConfigConflicts(LogConfigProperties annotationConfig, LogConfigProperties propertiesConfig, List<String> conflicts) {
        this.detectTraceConfigConflicts(annotationConfig.getTraceId(), propertiesConfig.getTraceId(), conflicts);
        this.detectPerformanceConfigConflicts(annotationConfig.getPerformance(), propertiesConfig.getPerformance(), conflicts);
        this.detectConditionConfigConflicts(annotationConfig.getCondition(), propertiesConfig.getCondition(), conflicts);
        this.detectSensitiveConfigConflicts(annotationConfig.getSensitive(), propertiesConfig.getSensitive(), conflicts);
    }

    private void detectTraceConfigConflicts(LogConfigProperties.TraceIdConfig annotationConfig, LogConfigProperties.TraceIdConfig propertiesConfig, List<String> conflicts) {
        this.detectConflict("trace.enabled", annotationConfig.isEnabled(), propertiesConfig.isEnabled(), conflicts);
        this.detectConflict("trace.headerName", annotationConfig.getHeaderName(), propertiesConfig.getHeaderName(), conflicts);
        this.detectConflict("trace.generator", annotationConfig.getGenerator(), propertiesConfig.getGenerator(), conflicts);
    }

    private void detectPerformanceConfigConflicts(LogConfigProperties.PerformanceConfig annotationConfig, LogConfigProperties.PerformanceConfig propertiesConfig, List<String> conflicts) {
        this.detectConflict("performance.enabled", annotationConfig.isEnabled(), propertiesConfig.isEnabled(), conflicts);
        this.detectConflict("performance.slowThreshold", annotationConfig.getSlowThreshold(), propertiesConfig.getSlowThreshold(), conflicts);
        this.detectConflict("performance.logSlowMethods", annotationConfig.isLogSlowMethods(), propertiesConfig.isLogSlowMethods(), conflicts);
    }

    private void detectConditionConfigConflicts(LogConfigProperties.ConditionConfig annotationConfig, LogConfigProperties.ConditionConfig propertiesConfig, List<String> conflicts) {
        this.detectConflict("condition.cacheEnabled", annotationConfig.isCacheEnabled(), propertiesConfig.isCacheEnabled(), conflicts);
        this.detectConflict("condition.timeoutMs", annotationConfig.getTimeoutMs(), propertiesConfig.getTimeoutMs(), conflicts);
        this.detectConflict("condition.failSafe", annotationConfig.isFailSafe(), propertiesConfig.isFailSafe(), conflicts);
    }

    private void detectSensitiveConfigConflicts(LogConfigProperties.SensitiveConfig annotationConfig, LogConfigProperties.SensitiveConfig propertiesConfig, List<String> conflicts) {
        this.detectConflict("sensitive.enabled", annotationConfig.isEnabled(), propertiesConfig.isEnabled(), conflicts);
        this.detectConflict("sensitive.maskValue", annotationConfig.getMaskValue(), propertiesConfig.getMaskValue(), conflicts);
        this.detectListConflict("sensitive.customFields", annotationConfig.getCustomFields(), propertiesConfig.getCustomFields(), conflicts);
    }

    private <T> void detectConflict(String configName, T annotationValue, T propertiesValue, List<String> conflicts) {
        if (annotationValue != null && propertiesValue != null && !Objects.equals(annotationValue, propertiesValue)) {
            conflicts.add(String.format("%s: annotation=%s, properties=%s", configName, annotationValue, propertiesValue));
        }
    }

    private void detectListConflict(String configName, List<String> annotationValue, List<String> propertiesValue, List<String> conflicts) {
        if (!(annotationValue == null || propertiesValue == null || annotationValue.isEmpty() || propertiesValue.isEmpty() || Objects.equals(annotationValue, propertiesValue))) {
            conflicts.add(String.format("%s: annotation=%s, properties=%s", configName, annotationValue, propertiesValue));
        }
    }

    private void logConflicts(List<String> conflicts) {
        logger.warn("\n===============================================\n  Atlas Log Configuration Conflicts Detected  \n===============================================\nAnnotation configuration will override properties configuration:\n");
        for (String conflict : conflicts) {
            logger.warn("  > {}", (Object)conflict);
        }
        logger.warn("\nPriority: Annotation > Properties > Environment > Default\n================================================\n");
    }
}

