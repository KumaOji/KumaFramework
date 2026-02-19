/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.config.BeanFactoryPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.core.annotation.Order
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.web.request.altas.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value=-2147483648)
public class ConfigMerger
implements BeanFactoryPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ConfigMerger.class);
    private static final String ANNOTATION_CONFIG_PROCESSOR_BEAN_NAME = "atlasLogAnnotationConfigProcessor";
    private static final String LOG_CONFIG_PROPERTIES_BEAN_NAME = "atlas.log-io.github.nemoob.atlas.LogUtils.config.LogConfigProperties";
    @Autowired(required=false)
    private AnnotationConfigValidator configValidator;
    @Autowired(required=false)
    private ConfigConflictDetector conflictDetector;

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        logger.info("Starting Atlas Log configuration merge process...");
        try {
            logger.debug("=== Atlas Log Configuration Processing Started ===");
            LogConfigProperties annotationConfig = this.getAnnotationConfig(beanFactory);
            logger.debug("Retrieved annotationConfig: {}", (Object)annotationConfig);
            if (annotationConfig != null && annotationConfig.getHttpLog() != null) {
                logger.debug("Annotation httpLog urlFormat: '{}'", (Object)annotationConfig.getHttpLog().getUrlFormat());
                logger.debug("Annotation httpLog includeQueryString: {}", (Object)annotationConfig.getHttpLog().isIncludeQueryString());
            } else {
                logger.debug("No annotation httpLog configuration found");
            }
            LogConfigProperties propertiesConfig = this.getPropertiesConfig(beanFactory);
            logger.debug("Retrieved propertiesConfig: {}", (Object)propertiesConfig);
            if (propertiesConfig != null && propertiesConfig.getHttpLog() != null) {
                logger.debug("Properties httpLog urlFormat: '{}'", (Object)propertiesConfig.getHttpLog().getUrlFormat());
                logger.debug("Properties httpLog includeQueryString: {}", (Object)propertiesConfig.getHttpLog().isIncludeQueryString());
            } else {
                logger.debug("No properties httpLog configuration found");
            }
            if (annotationConfig != null && this.conflictDetector != null) {
                this.conflictDetector.detectConflicts(annotationConfig, propertiesConfig);
            }
            LogConfigProperties mergedConfig = this.mergeConfigs(annotationConfig, propertiesConfig);
            logger.debug("Merged configuration: {}", (Object)mergedConfig);
            if (mergedConfig != null && mergedConfig.getHttpLog() != null) {
                logger.debug("Final merged httpLog urlFormat: '{}'", (Object)mergedConfig.getHttpLog().getUrlFormat());
            }
            if (mergedConfig != null && this.configValidator != null) {
                this.configValidator.validate(mergedConfig);
            }
            if (annotationConfig != null) {
                this.registerMergedConfig(beanFactory, mergedConfig);
                logger.info("Configuration merge completed successfully");
                logger.debug("=== Atlas Log Configuration Processing Completed ===");
            } else {
                logger.debug("No annotation configuration found, skipping merge process");
            }
        }
        catch (Exception e) {
            logger.error("Failed to merge Atlas Log configurations", (Throwable)e);
            throw new IllegalStateException("Failed to merge Atlas Log configurations", e);
        }
    }

    private LogConfigProperties getAnnotationConfig(ConfigurableListableBeanFactory beanFactory) {
        try {
            if (beanFactory.containsBean(ANNOTATION_CONFIG_PROCESSOR_BEAN_NAME)) {
                AtlasLogAnnotationConfigBeanPostProcessor processor = (AtlasLogAnnotationConfigBeanPostProcessor)beanFactory.getBean(ANNOTATION_CONFIG_PROCESSOR_BEAN_NAME, AtlasLogAnnotationConfigBeanPostProcessor.class);
                LogConfigProperties config = processor.getAnnotationConfig();
                logger.debug("Retrieved annotation configuration: {}", (Object)config);
                return config;
            }
        }
        catch (Exception e) {
            logger.warn("Failed to retrieve annotation configuration: {}", (Object)e.getMessage());
        }
        return null;
    }

    private LogConfigProperties getPropertiesConfig(ConfigurableListableBeanFactory beanFactory) {
        try {
            if (beanFactory.containsBean(LOG_CONFIG_PROPERTIES_BEAN_NAME)) {
                LogConfigProperties config = (LogConfigProperties)beanFactory.getBean(LOG_CONFIG_PROPERTIES_BEAN_NAME, LogConfigProperties.class);
                logger.debug("Retrieved properties configuration: {}", (Object)config);
                return config;
            }
        }
        catch (Exception e) {
            logger.warn("Failed to retrieve properties configuration: {}", (Object)e.getMessage());
        }
        return new LogConfigProperties();
    }

    private LogConfigProperties mergeConfigs(LogConfigProperties annotationConfig, LogConfigProperties propertiesConfig) {
        if (annotationConfig == null) {
            return propertiesConfig;
        }
        if (propertiesConfig == null) {
            return annotationConfig;
        }
        logger.info("Merging annotation configuration with properties configuration...");
        LogConfigProperties merged = new LogConfigProperties();
        this.mergeBasicConfig(merged, annotationConfig, propertiesConfig);
        this.mergeListConfig(merged, annotationConfig, propertiesConfig);
        this.mergeNestedConfigs(merged, annotationConfig, propertiesConfig);
        logger.debug("Configuration merge result: {}", (Object)merged);
        return merged;
    }

    private void mergeBasicConfig(LogConfigProperties merged, LogConfigProperties annotationConfig, LogConfigProperties propertiesConfig) {
        merged.setEnabled(this.resolveValue(annotationConfig.isEnabled(), propertiesConfig.isEnabled(), true, "enabled"));
        merged.setDefaultLevel(this.resolveValue(annotationConfig.getDefaultLevel(), propertiesConfig.getDefaultLevel(), "INFO", "defaultLevel"));
        merged.setDateFormat(this.resolveValue(annotationConfig.getDateFormat(), propertiesConfig.getDateFormat(), "yyyy-MM-dd HH:mm:ss.SSS", "dateFormat"));
        merged.setPrettyPrint(this.resolveValue(annotationConfig.isPrettyPrint(), propertiesConfig.isPrettyPrint(), false, "prettyPrint"));
        merged.setMaxMessageLength(this.resolveValue(annotationConfig.getMaxMessageLength(), propertiesConfig.getMaxMessageLength(), 2000, "maxMessageLength"));
        merged.setSpelEnabled(this.resolveValue(annotationConfig.isSpelEnabled(), propertiesConfig.isSpelEnabled(), true, "spelEnabled"));
        merged.setConditionEnabled(this.resolveValue(annotationConfig.isConditionEnabled(), propertiesConfig.isConditionEnabled(), true, "conditionEnabled"));
    }

    private void mergeListConfig(LogConfigProperties merged, LogConfigProperties annotationConfig, LogConfigProperties propertiesConfig) {
        merged.setEnabledTags(this.resolveListValue(annotationConfig.getEnabledTags(), propertiesConfig.getEnabledTags(), "enabledTags"));
        merged.setEnabledGroups(this.resolveListValue(annotationConfig.getEnabledGroups(), propertiesConfig.getEnabledGroups(), "enabledGroups"));
        merged.setExclusions(this.resolveListValue(annotationConfig.getExclusions(), propertiesConfig.getExclusions(), "exclusions"));
    }

    private void mergeNestedConfigs(LogConfigProperties merged, LogConfigProperties annotationConfig, LogConfigProperties propertiesConfig) {
        this.mergeTraceConfig(merged, annotationConfig, propertiesConfig);
        this.mergePerformanceConfig(merged, annotationConfig, propertiesConfig);
        this.mergeConditionConfig(merged, annotationConfig, propertiesConfig);
        this.mergeSensitiveConfig(merged, annotationConfig, propertiesConfig);
        this.mergeHttpLogConfig(merged, annotationConfig, propertiesConfig);
    }

    private void mergeTraceConfig(LogConfigProperties merged, LogConfigProperties annotationConfig, LogConfigProperties propertiesConfig) {
        LogConfigProperties.TraceIdConfig mergedTrace = merged.getTraceId();
        LogConfigProperties.TraceIdConfig annotationTrace = annotationConfig.getTraceId();
        LogConfigProperties.TraceIdConfig propertiesTrace = propertiesConfig.getTraceId();
        mergedTrace.setEnabled(this.resolveValue(annotationTrace.isEnabled(), propertiesTrace.isEnabled(), true, "trace.enabled"));
        mergedTrace.setHeaderName(this.resolveValue(annotationTrace.getHeaderName(), propertiesTrace.getHeaderName(), "X-Trace-Id", "trace.headerName"));
        mergedTrace.setGenerator(this.resolveValue(annotationTrace.getGenerator(), propertiesTrace.getGenerator(), "uuid", "trace.generator"));
    }

    private void mergePerformanceConfig(LogConfigProperties merged, LogConfigProperties annotationConfig, LogConfigProperties propertiesConfig) {
        LogConfigProperties.PerformanceConfig mergedPerf = merged.getPerformance();
        LogConfigProperties.PerformanceConfig annotationPerf = annotationConfig.getPerformance();
        LogConfigProperties.PerformanceConfig propertiesPerf = propertiesConfig.getPerformance();
        mergedPerf.setEnabled(this.resolveValue(annotationPerf.isEnabled(), propertiesPerf.isEnabled(), true, "performance.enabled"));
        mergedPerf.setSlowThreshold(this.resolveValue(annotationPerf.getSlowThreshold(), propertiesPerf.getSlowThreshold(), 1000L, "performance.slowThreshold"));
        mergedPerf.setLogSlowMethods(this.resolveValue(annotationPerf.isLogSlowMethods(), propertiesPerf.isLogSlowMethods(), true, "performance.logSlowMethods"));
    }

    private void mergeConditionConfig(LogConfigProperties merged, LogConfigProperties annotationConfig, LogConfigProperties propertiesConfig) {
        LogConfigProperties.ConditionConfig mergedCondition = merged.getCondition();
        LogConfigProperties.ConditionConfig annotationCondition = annotationConfig.getCondition();
        LogConfigProperties.ConditionConfig propertiesCondition = propertiesConfig.getCondition();
        mergedCondition.setCacheEnabled(this.resolveValue(annotationCondition.isCacheEnabled(), propertiesCondition.isCacheEnabled(), true, "condition.cacheEnabled"));
        mergedCondition.setTimeoutMs(this.resolveValue(annotationCondition.getTimeoutMs(), propertiesCondition.getTimeoutMs(), 1000L, "condition.timeoutMs"));
        mergedCondition.setFailSafe(this.resolveValue(annotationCondition.isFailSafe(), propertiesCondition.isFailSafe(), true, "condition.failSafe"));
    }

    private void mergeSensitiveConfig(LogConfigProperties merged, LogConfigProperties annotationConfig, LogConfigProperties propertiesConfig) {
        LogConfigProperties.SensitiveConfig mergedSensitive = merged.getSensitive();
        LogConfigProperties.SensitiveConfig annotationSensitive = annotationConfig.getSensitive();
        LogConfigProperties.SensitiveConfig propertiesSensitive = propertiesConfig.getSensitive();
        mergedSensitive.setEnabled(this.resolveValue(annotationSensitive.isEnabled(), propertiesSensitive.isEnabled(), true, "sensitive.enabled"));
        mergedSensitive.setMaskValue(this.resolveValue(annotationSensitive.getMaskValue(), propertiesSensitive.getMaskValue(), "***", "sensitive.maskValue"));
        mergedSensitive.setCustomFields(this.resolveListValue(annotationSensitive.getCustomFields(), propertiesSensitive.getCustomFields(), "sensitive.customFields"));
    }

    private void mergeHttpLogConfig(LogConfigProperties merged, LogConfigProperties annotationConfig, LogConfigProperties propertiesConfig) {
        LogConfigProperties.HttpLogConfig propertiesHttpLog;
        LogConfigProperties.HttpLogConfig mergedHttpLog = merged.getHttpLog();
        LogConfigProperties.HttpLogConfig annotationHttpLog = annotationConfig != null ? annotationConfig.getHttpLog() : null;
        LogConfigProperties.HttpLogConfig httpLogConfig = propertiesHttpLog = propertiesConfig != null ? propertiesConfig.getHttpLog() : null;
        if (annotationHttpLog != null || propertiesHttpLog != null) {
            Boolean annotationLogFullParams = annotationHttpLog != null ? Boolean.valueOf(annotationHttpLog.isLogFullParameters()) : null;
            Boolean propertiesLogFullParams = propertiesHttpLog != null ? Boolean.valueOf(propertiesHttpLog.isLogFullParameters()) : null;
            mergedHttpLog.setLogFullParameters(this.resolveValue(annotationLogFullParams, propertiesLogFullParams, Boolean.TRUE, "httpLogUtils.logFullParameters"));
            mergedHttpLog.setUrlFormat(this.resolveValue(annotationHttpLog != null ? annotationHttpLog.getUrlFormat() : null, propertiesHttpLog != null ? propertiesHttpLog.getUrlFormat() : null, "{uri}{queryString}", "httpLogUtils.urlFormat"));
            Boolean annotationIncludeQuery = annotationHttpLog != null ? Boolean.valueOf(annotationHttpLog.isIncludeQueryString()) : null;
            Boolean propertiesIncludeQuery = propertiesHttpLog != null ? Boolean.valueOf(propertiesHttpLog.isIncludeQueryString()) : null;
            mergedHttpLog.setIncludeQueryString(this.resolveValue(annotationIncludeQuery, propertiesIncludeQuery, Boolean.TRUE, "httpLogUtils.includeQueryString"));
            Boolean annotationIncludeHeaders = annotationHttpLog != null ? Boolean.valueOf(annotationHttpLog.isIncludeHeaders()) : null;
            Boolean propertiesIncludeHeaders = propertiesHttpLog != null ? Boolean.valueOf(propertiesHttpLog.isIncludeHeaders()) : null;
            mergedHttpLog.setIncludeHeaders(this.resolveValue(annotationIncludeHeaders, propertiesIncludeHeaders, Boolean.FALSE, "httpLogUtils.includeHeaders"));
            mergedHttpLog.setExcludeHeaders(this.resolveListValue(annotationHttpLog != null ? annotationHttpLog.getExcludeHeaders() : null, propertiesHttpLog != null ? propertiesHttpLog.getExcludeHeaders() : null, "httpLogUtils.excludeHeaders"));
        }
    }

    private <T> T resolveValue(T annotationValue, T propertiesValue, T defaultValue, String configName) {
        String source;
        T result;
        if (propertiesValue != null && !this.isDefaultValue(propertiesValue)) {
            result = propertiesValue;
            source = "properties";
        } else if (annotationValue != null && !this.isDefaultValue(annotationValue)) {
            result = annotationValue;
            source = "annotation";
        } else {
            result = defaultValue;
            source = "default";
        }
        logger.debug("Resolved config '{}' = {} (source: {})", new Object[]{configName, result, source});
        return result;
    }

    private List<String> resolveListValue(List<String> annotationValue, List<String> propertiesValue, String configName) {
        String source;
        ArrayList<String> result;
        if (annotationValue != null && !annotationValue.isEmpty()) {
            result = new ArrayList<String>(annotationValue);
            source = "annotation";
        } else if (propertiesValue != null && !propertiesValue.isEmpty()) {
            result = new ArrayList<String>(propertiesValue);
            source = "properties";
        } else {
            result = new ArrayList();
            source = "default";
        }
        logger.debug("Resolved list config '{}' = {} (source: {})", new Object[]{configName, result, source});
        return result;
    }

    private boolean isDefaultValue(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            String strValue = (String)value;
            if ("{uri}{queryString}".equals(strValue)) {
                return true;
            }
            if ("yyyy-MM-dd HH:mm:ss.SSS".equals(strValue)) {
                return true;
            }
            if ("INFO".equals(strValue)) {
                return true;
            }
            if ("X-Trace-Id".equals(strValue)) {
                return true;
            }
            if ("uuid".equals(strValue)) {
                return true;
            }
            if ("***".equals(strValue)) {
                return true;
            }
            return strValue.isEmpty() || "default".equals(strValue);
        }
        if (value instanceof Boolean) {
            return false;
        }
        if (value instanceof Number) {
            long longValue = ((Number)value).longValue();
            if (longValue == 1000L) {
                return true;
            }
            if (longValue == 2000L) {
                return true;
            }
            if (longValue == 32L) {
                return true;
            }
            return longValue == 0L;
        }
        if (value instanceof Collection) {
            return ((Collection)value).isEmpty();
        }
        return false;
    }

    private void registerMergedConfig(ConfigurableListableBeanFactory beanFactory, LogConfigProperties mergedConfig) {
        beanFactory.registerSingleton("atlasLogMergedConfig", (Object)mergedConfig);
        logger.info("Registered merged configuration as singleton bean: atlasLogMergedConfig");
    }
}

