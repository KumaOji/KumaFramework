/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.github.xiaoymin.knife4j.annotations.ApiSupport
 *  com.kuma.boot.common.utils.log.LogUtils
 *  io.swagger.v3.oas.annotations.tags.Tag
 *  io.swagger.v3.oas.models.OpenAPI
 *  org.apache.commons.lang3.ArrayUtils
 *  org.springdoc.core.customizers.GlobalOpenApiCustomizer
 *  org.springdoc.core.properties.SpringDocConfigProperties
 *  org.springdoc.core.properties.SpringDocConfigProperties$GroupConfig
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
 *  org.springframework.core.type.filter.AnnotationTypeFilter
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.util.CollectionUtils
 *  org.springframework.web.bind.annotation.RestController
 */
package com.kuma.boot.springdoc.knife4j.spring.extension;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.springdoc.knife4j.spring.configuration.Knife4jProperties;
import com.kuma.boot.springdoc.knife4j.spring.configuration.Knife4jSetting;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

public class Knife4jOpenApiCustomizer
implements GlobalOpenApiCustomizer {
    final Knife4jProperties knife4jProperties;
    final SpringDocConfigProperties properties;

    public Knife4jOpenApiCustomizer(Knife4jProperties knife4jProperties, SpringDocConfigProperties properties) {
        this.knife4jProperties = knife4jProperties;
        this.properties = properties;
    }

    public void customise(OpenAPI openApi) {
        LogUtils.debug((String)"Knife4j OpenApiCustomizer", (Object[])new Object[0]);
        if (this.knife4jProperties.isEnable()) {
            Knife4jSetting setting = this.knife4jProperties.getSetting();
            OpenApiExtensionResolver openApiExtensionResolver = new OpenApiExtensionResolver(setting, this.knife4jProperties.getDocuments());
            openApiExtensionResolver.start();
            HashMap<String, Object> objectMap = new HashMap<String, Object>();
            objectMap.put("x-setting", setting);
            objectMap.put("x-markdownFiles", openApiExtensionResolver.getMarkdownFiles());
            openApi.addExtension("x-openapi", objectMap);
            this.addOrderExtension(openApi);
        }
    }

    private void addOrderExtension(OpenAPI openApi) {
        if (CollectionUtils.isEmpty((Collection)this.properties.getGroupConfigs())) {
            return;
        }
        Set packagesToScan = this.properties.getGroupConfigs().stream().map(SpringDocConfigProperties.GroupConfig::getPackagesToScan).filter(toScan -> !CollectionUtils.isEmpty((Collection)toScan)).flatMap(Collection::stream).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(packagesToScan)) {
            return;
        }
        Set<Class> classes = packagesToScan.stream().map(packageToScan -> this.scanPackageByAnnotation((String)packageToScan, (Class<? extends Annotation>)RestController.class)).flatMap(Collection::stream).filter(clazz -> clazz.isAnnotationPresent(ApiSupport.class)).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(classes)) {
            HashMap tagOrderMap = new HashMap();
            classes.forEach(clazz -> {
                Tag tag = this.getTag((Class<?>)clazz);
                if (Objects.nonNull(tag)) {
                    ApiSupport apiSupport = clazz.getAnnotation(ApiSupport.class);
                    tagOrderMap.putIfAbsent(tag.name(), apiSupport.order());
                }
            });
            if (openApi.getTags() != null) {
                openApi.getTags().forEach(tag -> {
                    if (tagOrderMap.containsKey(tag.getName())) {
                        tag.addExtension("x-order", tagOrderMap.get(tag.getName()));
                    }
                });
            }
        }
    }

    private Tag getTag(Class<?> clazz) {
        Object[] interfaces;
        Tag tag = clazz.getAnnotation(Tag.class);
        if (Objects.isNull(tag) && ArrayUtils.isNotEmpty((Object[])(interfaces = clazz.getInterfaces()))) {
            for (Object interfaceClazz : interfaces) {
                Tag anno = ((Class)interfaceClazz).getAnnotation(Tag.class);
                if (!Objects.nonNull(anno)) continue;
                tag = anno;
                break;
            }
        }
        return tag;
    }

    private Set<Class<?>> scanPackageByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter((TypeFilter)new AnnotationTypeFilter(annotationClass));
        HashSet classes = new HashSet();
        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(packageName)) {
            try {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                classes.add(clazz);
            }
            catch (ClassNotFoundException classNotFoundException) {}
        }
        return classes;
    }
}

