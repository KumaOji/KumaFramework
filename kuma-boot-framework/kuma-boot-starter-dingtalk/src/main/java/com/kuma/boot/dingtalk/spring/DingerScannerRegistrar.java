/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.BeanNameGenerator
 *  org.springframework.context.annotation.ImportBeanDefinitionRegistrar
 *  org.springframework.core.annotation.AnnotationAttributes
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.dingtalk.spring;

import com.kuma.boot.dingtalk.annatations.DingerScan;
import com.kuma.boot.dingtalk.entity.ExceptionPairs;
import com.kuma.boot.dingtalk.enums.ExceptionEnum;
import com.kuma.boot.dingtalk.exception.DingerAnalysisException;
import com.kuma.boot.dingtalk.exception.DingerException;
import com.kuma.boot.dingtalk.model.ClassPathDingerScanner;
import com.kuma.boot.dingtalk.model.DefaultDingerDefinitionResolver;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

public class DingerScannerRegistrar
extends DefaultDingerDefinitionResolver
implements ImportBeanDefinitionRegistrar {
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap((Map)importingClassMetadata.getAnnotationAttributes(DingerScan.class.getName()));
        if (annoAttrs != null) {
            this.registerBeanDefinitions(annoAttrs, registry);
        }
    }

    void registerBeanDefinitions(AnnotationAttributes annoAttrs, BeanDefinitionRegistry registry) {
        Class generatorClass;
        Class markerInterface;
        ClassPathDingerScanner scanner = new ClassPathDingerScanner(registry);
        Class annotationClass = annoAttrs.getClass("annotationClass");
        if (!Annotation.class.equals((Object)annotationClass)) {
            scanner.setAnnotationClass(annotationClass);
        }
        if (!Class.class.equals((Object)(markerInterface = annoAttrs.getClass("markerInterface")))) {
            scanner.setMarkerInterface(markerInterface);
        }
        if (!BeanNameGenerator.class.equals((Object)(generatorClass = annoAttrs.getClass("nameGenerator")))) {
            scanner.setBeanNameGenerator((BeanNameGenerator)BeanUtils.instantiateClass((Class)generatorClass));
        }
        ArrayList<String> basePackages = new ArrayList<String>();
        basePackages.addAll(Arrays.stream(annoAttrs.getStringArray("value")).filter(StringUtils::hasText).toList());
        basePackages.addAll(Arrays.stream(annoAttrs.getStringArray("basePackages")).filter(StringUtils::hasText).toList());
        scanner.registerFilters();
        scanner.doScan(StringUtils.toStringArray(basePackages));
        try {
            this.resolver(scanner.getDingerClasses());
            dingerClasses = scanner.getDingerClasses();
        }
        catch (DingerException ex) {
            throw new DingerAnalysisException(ex.getPairs(), ex.getMessage());
        }
        catch (Exception ex) {
            throw new DingerException(ex, (ExceptionPairs)ExceptionEnum.UNKNOWN);
        }
    }
}

