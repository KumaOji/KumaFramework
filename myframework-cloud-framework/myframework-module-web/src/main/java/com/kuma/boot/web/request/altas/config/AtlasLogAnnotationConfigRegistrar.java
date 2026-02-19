/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.context.annotation.ImportBeanDefinitionRegistrar
 *  org.springframework.core.type.AnnotationMetadata
 */
package com.kuma.boot.web.request.altas.config;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class AtlasLogAnnotationConfigRegistrar
implements ImportBeanDefinitionRegistrar {
    private static final Logger logger = LoggerFactory.getLogger(AtlasLogAnnotationConfigRegistrar.class);
    private static final String ENABLE_ATLAS_LOG_ANNOTATION = "io.github.nemoob.atlas.LogUtils.annotation.EnableAtlasLog";
    private static final String ANNOTATION_CONFIG_PROCESSOR_BEAN_NAME = "atlasLogAnnotationConfigProcessor";
    private static final String CONFIG_MERGER_BEAN_NAME = "atlasLogConfigMerger";

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!importingClassMetadata.hasAnnotation(ENABLE_ATLAS_LOG_ANNOTATION)) {
            logger.debug("@EnableAtlasLog annotation not found, skipping registration");
            return;
        }
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(ENABLE_ATLAS_LOG_ANNOTATION);
        if (attributes == null) {
            logger.warn("@EnableAtlasLog annotation attributes is null, using default configuration");
            attributes = this.getDefaultAttributes();
        }
        logger.info("Registering Atlas Log annotation configuration with attributes: {}", (Object)attributes);
        this.registerAnnotationConfigProcessor(registry, attributes);
        this.registerConfigMerger(registry);
        logger.info("Atlas Log annotation configuration registration completed successfully");
    }

    private void registerAnnotationConfigProcessor(BeanDefinitionRegistry registry, Map<String, Object> attributes) {
        if (registry.containsBeanDefinition(ANNOTATION_CONFIG_PROCESSOR_BEAN_NAME)) {
            logger.debug("AnnotationConfigProcessor already registered, skipping");
            return;
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(AtlasLogAnnotationConfigBeanPostProcessor.class);
        builder.addConstructorArgValue(attributes);
        registry.registerBeanDefinition(ANNOTATION_CONFIG_PROCESSOR_BEAN_NAME, (BeanDefinition)builder.getBeanDefinition());
        logger.debug("Registered AtlasLogAnnotationConfigProcessor bean: {}", (Object)ANNOTATION_CONFIG_PROCESSOR_BEAN_NAME);
    }

    private void registerConfigMerger(BeanDefinitionRegistry registry) {
        if (registry.containsBeanDefinition(CONFIG_MERGER_BEAN_NAME)) {
            logger.debug("ConfigMerger already registered, skipping");
            return;
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ConfigMerger.class);
        registry.registerBeanDefinition(CONFIG_MERGER_BEAN_NAME, (BeanDefinition)builder.getBeanDefinition());
        logger.debug("Registered ConfigMerger bean: {}", (Object)CONFIG_MERGER_BEAN_NAME);
    }

    private Map<String, Object> getDefaultAttributes() {
        HashMap<String, Object> defaults = new HashMap<String, Object>();
        defaults.put("enabled", true);
        defaults.put("defaultLevel", "INFO");
        defaults.put("dateFormat", "yyyy-MM-dd HH:mm:ss.SSS");
        defaults.put("prettyPrint", false);
        defaults.put("maxMessageLength", 2000);
        defaults.put("spelEnabled", true);
        defaults.put("conditionEnabled", true);
        defaults.put("enabledTags", new String[]{"business", "security", "api"});
        defaults.put("enabledGroups", new String[]{"default", "business"});
        defaults.put("exclusions", new String[]{"*.toString", "*.hashCode", "*.equals"});
        return defaults;
    }
}

