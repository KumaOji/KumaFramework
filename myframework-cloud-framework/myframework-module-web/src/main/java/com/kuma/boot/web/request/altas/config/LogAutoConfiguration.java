/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.servlet.Filter
 *  org.springframework.boot.autoconfigure.AutoConfigureOrder
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.boot.web.servlet.FilterRegistrationBean
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.core.task.TaskDecorator
 *  org.springframework.web.servlet.HandlerInterceptor
 *  org.springframework.web.servlet.config.annotation.InterceptorRegistry
 *  org.springframework.web.servlet.config.annotation.WebMvcConfigurer
 */
package com.kuma.boot.web.request.altas.config;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.request.altas.aspect.AtlasLogAspect;
import com.kuma.boot.web.request.altas.async.TraceIdTaskDecorator;
import com.kuma.boot.web.request.altas.comparator.JsonPathValueExtractor;
import com.kuma.boot.web.request.altas.expression.SpelExpressionEvaluator;
import com.kuma.boot.web.request.altas.processor.JsonPathCompareProcessor;
import com.kuma.boot.web.request.altas.serializer.ArgumentFormatConfig;
import com.kuma.boot.web.request.altas.serializer.ArgumentFormatType;
import com.kuma.boot.web.request.altas.serializer.ArgumentFormatter;
import com.kuma.boot.web.request.altas.serializer.ArgumentFormatterManager;
import com.kuma.boot.web.request.altas.serializer.ArgumentSerializer;
import com.kuma.boot.web.request.altas.serializer.FastjsonArgumentSerializer;
import com.kuma.boot.web.request.altas.serializer.JsonArgumentFormatter;
import com.kuma.boot.web.request.altas.serializer.KeyValueArgumentFormatter;
import com.kuma.boot.web.request.altas.serializer.SensitiveDataMasker;
import com.kuma.boot.web.request.altas.web.LoggingFilter;
import com.kuma.boot.web.request.altas.web.TraceIdInterceptor;
import jakarta.servlet.Filter;
import java.util.Map;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(value={LogConfigProperties.class})
@AutoConfigureOrder(value=-2147483648)
@ConditionalOnProperty(prefix="atlas.log", name={"enabled"}, havingValue="true", matchIfMissing=true)
public class LogAutoConfiguration {
    private LogConfigProperties getEffectiveConfig(ApplicationContext applicationContext, LogConfigProperties defaultConfig) {
        try {
            LogConfigProperties mergedConfig = (LogConfigProperties)applicationContext.getBean("atlasLogMergedConfig", LogConfigProperties.class);
            LogUtils.debug((String)"Using merged annotation configuration", (Object[])new Object[0]);
            return mergedConfig;
        }
        catch (Exception e) {
            LogUtils.debug((String)"Using default properties configuration", (Object[])new Object[0]);
            return defaultConfig;
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public SensitiveDataMasker sensitiveDataMasker(LogConfigProperties properties, ApplicationContext applicationContext) {
        LogConfigProperties effectiveConfig = this.getEffectiveConfig(applicationContext, properties);
        SensitiveDataMasker masker = new SensitiveDataMasker(effectiveConfig.getSensitive().isEnabled());
        if (effectiveConfig.getSensitive().getCustomFields() != null) {
            for (String field : effectiveConfig.getSensitive().getCustomFields()) {
                masker.addSensitiveField(field);
            }
        }
        LogUtils.info((String)"Sensitive data masker configured, enabled: {}", (Object[])new Object[]{effectiveConfig.getSensitive().isEnabled()});
        return masker;
    }

    @Bean
    @ConditionalOnMissingBean
    public ArgumentFormatterManager argumentFormatterManager(SensitiveDataMasker sensitiveDataMasker, LogConfigProperties properties, ApplicationContext applicationContext) {
        LogConfigProperties effectiveConfig = this.getEffectiveConfig(applicationContext, properties);
        LogConfigProperties.ArgumentFormatConfig config = effectiveConfig.getArgumentFormat();
        JsonArgumentFormatter jsonFormatter = new JsonArgumentFormatter(sensitiveDataMasker);
        KeyValueArgumentFormatter keyValueFormatter = new KeyValueArgumentFormatter(sensitiveDataMasker, config.getSeparator(), config.getKeyValueSeparator(), config.isIncludeParameterIndex());
        ArgumentFormatterManager manager = new ArgumentFormatterManager(jsonFormatter, "json");
        manager.registerFormatter("json", jsonFormatter);
        manager.registerFormatter("key-value", keyValueFormatter);
        this.registerCustomFormatters(manager, applicationContext);
        LogUtils.info((String)"ArgumentFormatterManager configured with formatters: {}", (Object[])new Object[]{manager.getFormatterNames()});
        return manager;
    }

    private void registerCustomFormatters(ArgumentFormatterManager manager, ApplicationContext applicationContext) {
        try {
            Map customFormatters = applicationContext.getBeansOfType(ArgumentFormatter.class);
            for (Map.Entry entry : customFormatters.entrySet()) {
                String beanName = (String)entry.getKey();
                ArgumentFormatter formatter = (ArgumentFormatter)entry.getValue();
                if (formatter instanceof JsonArgumentFormatter || formatter instanceof KeyValueArgumentFormatter) continue;
                String formatterName = formatter.getName();
                if (formatterName == null || formatterName.trim().isEmpty()) {
                    formatterName = beanName;
                }
                manager.registerFormatter(formatterName, formatter);
                LogUtils.info((String)"Registered custom formatter: {} -> {}", (Object[])new Object[]{formatterName, formatter.getClass().getSimpleName()});
            }
        }
        catch (Exception e) {
            LogUtils.warn((String)"Failed to register custom formatters: {}", (Object[])new Object[]{e.getMessage()});
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public ArgumentFormatConfig argumentFormatConfig(LogConfigProperties properties, ApplicationContext applicationContext) {
        LogConfigProperties effectiveConfig = this.getEffectiveConfig(applicationContext, properties);
        LogConfigProperties.ArgumentFormatConfig config = effectiveConfig.getArgumentFormat();
        ArgumentFormatType type = ArgumentFormatType.JSON;
        if (config.getType() == LogConfigProperties.ArgumentFormatType.KEY_VALUE) {
            type = ArgumentFormatType.KEY_VALUE;
        }
        return new ArgumentFormatConfig(type, config.getSeparator(), config.getKeyValueSeparator(), config.isIncludeParameterIndex());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name={"com.alibaba.fastjson2.JSON"})
    public FastjsonArgumentSerializer fastjsonArgumentSerializer(SensitiveDataMasker sensitiveDataMasker, ArgumentFormatConfig argumentFormatConfig) {
        LogUtils.info((String)"Using Fastjson-based argument serializer with format: {}", (Object[])new Object[]{argumentFormatConfig.getType()});
        return new FastjsonArgumentSerializer(sensitiveDataMasker, argumentFormatConfig);
    }

    @Bean
    @ConditionalOnMissingBean(value={ArgumentSerializer.class})
    public ArgumentSerializer argumentSerializer(FastjsonArgumentSerializer fastjsonSerializer) {
        LogUtils.info((String)"ArgumentSerializer configured with Fastjson implementation", (Object[])new Object[0]);
        return fastjsonSerializer;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix="atlas.log", name={"spel-enabled"}, havingValue="true", matchIfMissing=true)
    public SpelExpressionEvaluator spelExpressionEvaluator(ApplicationContext applicationContext, LogConfigProperties properties) {
        LogConfigProperties effectiveConfig = this.getEffectiveConfig(applicationContext, properties);
        LogConfigProperties.ConditionConfig conditionConfig = effectiveConfig.getCondition();
        SpelExpressionEvaluator evaluator = new SpelExpressionEvaluator(applicationContext, conditionConfig.isCacheEnabled(), conditionConfig.getTimeoutMs(), conditionConfig.isFailSafe());
        LogUtils.info((String)"SpEL expression evaluator configured successfully, cache enabled: {}, timeout: {}ms", (Object[])new Object[]{conditionConfig.isCacheEnabled(), conditionConfig.getTimeoutMs()});
        return evaluator;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name={"com.jayway.jsonpath.JsonPath"})
    public JsonPathValueExtractor jsonPathValueExtractor() {
        return new JsonPathValueExtractor(true);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name={"com.jayway.jsonpath.JsonPath"})
    public JsonPathCompareProcessor jsonPathCompareProcessor(JsonPathValueExtractor jsonPathValueExtractor) {
        return new JsonPathCompareProcessor(jsonPathValueExtractor);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnMissingClass(value={"com.jayway.jsonpath.JsonPath"})
    public JsonPathCompareProcessor jsonPathCompareProcessorFallback() {
        return new JsonPathCompareProcessor(null);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name={"org.aspectj.lang.annotation.Aspect"})
    public AtlasLogAspect atlasLogAspect(SpelExpressionEvaluator spelExpressionEvaluator, ArgumentSerializer argumentSerializer, JsonPathCompareProcessor jsonPathCompareProcessor, ArgumentFormatterManager argumentFormatterManager) {
        LogUtils.info((String)"Atlas Log aspect configured successfully", (Object[])new Object[0]);
        return new AtlasLogAspect(spelExpressionEvaluator, argumentSerializer, jsonPathCompareProcessor, argumentFormatterManager);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name={"org.springframework.core.task.TaskDecorator"})
    public TaskDecorator traceIdTaskDecorator() {
        LogUtils.info((String)"Atlas Log TraceId task decorator configured successfully", (Object[])new Object[0]);
        return new TraceIdTaskDecorator();
    }

    @Configuration
    @ConditionalOnProperty(prefix="atlas.log", name={"spel-enabled"}, havingValue="false")
    public static class NoSpelConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public SpelExpressionEvaluator spelExpressionEvaluator() {
            LogUtils.warn((String)"SpEL expression is disabled, using empty implementation", (Object[])new Object[0]);
            return new SpelExpressionEvaluator(null, false, 0L, true);
        }
    }

    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnClass(name={"javax.servlet.Filter"})
    public static class WebConfiguration
    implements WebMvcConfigurer {
        private final LogConfigProperties properties;
        private final ApplicationContext applicationContext;

        public WebConfiguration(LogConfigProperties properties, ApplicationContext applicationContext) {
            this.properties = properties;
            this.applicationContext = applicationContext;
        }

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnProperty(prefix="atlas.LogUtils.trace-id", name={"enabled"}, havingValue="true", matchIfMissing=true)
        public TraceIdInterceptor traceIdInterceptor() {
            LogConfigProperties effectiveConfig = this.getEffectiveConfig();
            return new TraceIdInterceptor(effectiveConfig.getTraceId().getHeaderName());
        }

        @Bean
        @ConditionalOnMissingBean(name={"atlasLogFilterRegistration"})
        public FilterRegistrationBean<LoggingFilter> atlasLogFilterRegistration(ArgumentFormatConfig argumentFormatConfig) {
            LogConfigProperties effectiveConfig = this.getEffectiveConfig();
            FilterRegistrationBean registration = new FilterRegistrationBean();
            registration.setFilter((Filter)new LoggingFilter(effectiveConfig, argumentFormatConfig));
            registration.addUrlPatterns(new String[]{"/*"});
            registration.setOrder(Integer.MIN_VALUE);
            registration.setName("atlasLoggingFilter");
            LogUtils.info((String)"Atlas Log filter configured successfully with argument format: {}", (Object[])new Object[]{argumentFormatConfig.getType()});
            return registration;
        }

        public void addInterceptors(InterceptorRegistry registry) {
            LogConfigProperties effectiveConfig = this.getEffectiveConfig();
            if (effectiveConfig.getTraceId().isEnabled()) {
                registry.addInterceptor((HandlerInterceptor)this.traceIdInterceptor()).addPathPatterns(new String[]{"/**"}).order(-2147483647);
                LogUtils.info((String)"TraceId interceptor configured successfully, header name: {}", (Object[])new Object[]{effectiveConfig.getTraceId().getHeaderName()});
            }
        }

        private LogConfigProperties getEffectiveConfig() {
            try {
                LogConfigProperties mergedConfig = (LogConfigProperties)this.applicationContext.getBean("atlasLogMergedConfig", LogConfigProperties.class);
                LogUtils.debug((String)"Successfully retrieved merged configuration: atlasLogMergedConfig", (Object[])new Object[0]);
                if (mergedConfig.getHttpLog() != null) {
                    LogUtils.debug((String)"Merged config HTTP Log urlFormat: '{}'", (Object[])new Object[]{mergedConfig.getHttpLog().getUrlFormat()});
                }
                return mergedConfig;
            }
            catch (Exception e) {
                LogUtils.warn((String)"Failed to retrieve merged configuration 'atlasLogMergedConfig', falling back to properties: {}", (Object[])new Object[]{e.getMessage()});
                if (this.properties.getHttpLog() != null) {
                    LogUtils.debug((String)"Fallback properties HTTP Log urlFormat: '{}'", (Object[])new Object[]{this.properties.getHttpLog().getUrlFormat()});
                }
                return this.properties;
            }
        }
    }
}

