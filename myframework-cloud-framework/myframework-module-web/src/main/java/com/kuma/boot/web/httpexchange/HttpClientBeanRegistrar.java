/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
 *  org.springframework.core.env.Environment
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.core.type.ClassMetadata
 *  org.springframework.core.type.classreading.MetadataReader
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.service.annotation.HttpExchange
 */
package com.kuma.boot.web.httpexchange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.service.annotation.HttpExchange;

class HttpClientBeanRegistrar {
    private static final Logger log = LoggerFactory.getLogger(HttpClientBeanRegistrar.class);
    private final ClassPathScanningCandidateComponentProvider scanner = HttpClientBeanRegistrar.getScanner();
    private final BeanDefinitionRegistry registry;
    private final Environment environment;
    private static final HashMap<BeanDefinitionRegistry, Map<Class<?>, List<BeanDefinition>>> beanDefinitionMap = new HashMap();

    public HttpClientBeanRegistrar(BeanDefinitionRegistry registry, Environment environment) {
        this.registry = registry;
        this.environment = environment;
    }

    public void register(String ... basePackages) {
        Set<String> packages = Set.copyOf(Arrays.asList(basePackages));
        this.registerBeans4BasePackages(packages);
    }

    public void register(Class<?> ... clients) {
        for (Class<?> client : clients) {
            this.registerHttpClientBean(this.registry, client);
        }
    }

    private void registerHttpClientBean(BeanDefinitionRegistry registry, Class<?> clz) {
        if (!clz.isInterface()) {
            throw new IllegalArgumentException(clz.getName() + " is not an interface");
        }
        if (!Util.isHttpExchangeInterface(clz)) {
            return;
        }
        if (!(registry instanceof DefaultListableBeanFactory)) {
            throw new IllegalArgumentException("BeanDefinitionRegistry is not a DefaultListableBeanFactory");
        }
        DefaultListableBeanFactory bf = (DefaultListableBeanFactory)registry;
        HttpClientBeanRegistrar.addBeanDefinitionCache(bf);
        if (HttpClientBeanRegistrar.hasManualRegistered(registry, clz)) {
            if (log.isDebugEnabled()) {
                log.debug("HTTP client bean '{}' is already registered, skip auto registration", (Object)clz.getName());
            }
            return;
        }
        HttpExchangeUtil.registerHttpExchangeBean(bf, this.environment, clz);
    }

    private static void addBeanDefinitionCache(DefaultListableBeanFactory bf) {
        if (beanDefinitionMap.containsKey(bf)) {
            return;
        }
        for (String beanDefinitionName : bf.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = bf.getBeanDefinition(beanDefinitionName);
            Class<?> clz = Util.getBeanDefinitionClass(beanDefinition);
            if (clz == null) continue;
            beanDefinitionMap.computeIfAbsent((BeanDefinitionRegistry)bf, k -> new HashMap()).computeIfAbsent(clz, k -> new ArrayList()).add(beanDefinition);
        }
    }

    private static boolean hasManualRegistered(BeanDefinitionRegistry registry, Class<?> clz) {
        return !beanDefinitionMap.getOrDefault(registry, Map.of()).getOrDefault(clz, List.of()).isEmpty();
    }

    private static ClassPathScanningCandidateComponentProvider getScanner() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false){

            protected boolean isCandidateComponent(AnnotatedBeanDefinition abd) {
                return true;
            }
        };
        provider.addIncludeFilter((mr, mrf) -> HttpClientBeanRegistrar.isHttpExchange(mr));
        return provider;
    }

    private static boolean isHttpExchange(MetadataReader mr) {
        ClassMetadata cm = mr.getClassMetadata();
        AnnotationMetadata am = mr.getAnnotationMetadata();
        return cm.isInterface() && cm.isIndependent() && !cm.isAnnotation() && (am.hasAnnotatedMethods(HttpExchange.class.getName()) || am.hasAnnotatedMethods(RequestMapping.class.getName()));
    }

    private void registerBeans4BasePackages(Collection<String> basePackages) {
        for (String pkg : basePackages) {
            Set beanDefinitions = this.scanner.findCandidateComponents(pkg);
            for (BeanDefinition bd : beanDefinitions) {
                Class<?> clz = Util.getBeanDefinitionClass(bd);
                if (clz == null) continue;
                this.registerHttpClientBean(this.registry, clz);
            }
        }
    }

    static void clearBeanDefinitionCache(BeanDefinitionRegistry registry) {
        beanDefinitionMap.remove(registry);
    }
}

