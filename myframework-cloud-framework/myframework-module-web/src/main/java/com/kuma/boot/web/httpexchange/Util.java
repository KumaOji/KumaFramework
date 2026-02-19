/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 *  org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.boot.context.properties.bind.Binder
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.core.env.Environment
 *  org.springframework.core.type.MethodMetadata
 *  org.springframework.util.AntPathMatcher
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.service.annotation.HttpExchange
 */
package com.kuma.boot.web.httpexchange;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.service.annotation.HttpExchange;

final class Util {
    private static final AntPathMatcher matcher = new AntPathMatcher(".");

    private Util() {
    }

    public static Optional<HttpExchangeProperties.Channel> findMatchedConfig(Class<?> clz, HttpExchangeProperties properties) {
        Optional<HttpExchangeProperties.Channel> found = properties.getChannels().stream().filter(it -> it.getClasses().stream().anyMatch(ch -> ch == clz)).findFirst();
        if (found.isPresent()) {
            return found;
        }
        return properties.getChannels().stream().filter(it -> Util.match(clz, it)).findFirst();
    }

    public static boolean nameMatch(String name, Set<Class<?>> classes) {
        return classes.stream().anyMatch(clz -> Util.match(name, clz));
    }

    private static boolean match(Class<?> clz, HttpExchangeProperties.Channel client) {
        if (client.getClasses().stream().anyMatch(ch -> ch == clz)) {
            return true;
        }
        return client.getClients().stream().anyMatch(name -> Util.match(name, clz));
    }

    private static boolean match(String name, Class<?> clz) {
        return Util.isMatched(name, clz) || Stream.of(clz.getInterfaces()).anyMatch(it -> Util.isMatched(name, it));
    }

    private static boolean isMatched(String name, Class<?> clz) {
        String nameToUse = name.replace("-", "");
        return nameToUse.equalsIgnoreCase(clz.getSimpleName()) || nameToUse.equalsIgnoreCase(clz.getName()) || nameToUse.equalsIgnoreCase(clz.getCanonicalName()) || matcher.match(name, clz.getCanonicalName()) || matcher.match(name, clz.getSimpleName());
    }

    public static HttpExchangeProperties getProperties(Environment environment) {
        HttpExchangeProperties properties = (HttpExchangeProperties)Binder.get((Environment)environment).bind("http-exchange", HttpExchangeProperties.class).orElseGet(HttpExchangeProperties::new);
        properties.afterPropertiesSet();
        return properties;
    }

    public static boolean isHttpExchangeInterface(Class<?> clz) {
        return clz.isInterface() && (Util.hasAnnotation(clz, HttpExchange.class) || Util.hasAnnotation(clz, RequestMapping.class));
    }

    public static boolean hasAnnotation(Class<?> clz, Class<? extends Annotation> annotationType) {
        Method[] methods;
        if (AnnotationUtils.findAnnotation(clz, annotationType) != null) {
            return true;
        }
        for (Method method : methods = ReflectionUtils.getAllDeclaredMethods(clz)) {
            if (AnnotationUtils.findAnnotation((Method)method, annotationType) == null) continue;
            return true;
        }
        return false;
    }

    public static @Nullable Class<?> getBeanDefinitionClass(BeanDefinition beanDefinition) {
        AnnotatedBeanDefinition abd;
        MethodMetadata metadata;
        if (beanDefinition instanceof AnnotatedBeanDefinition && (metadata = (abd = (AnnotatedBeanDefinition)beanDefinition).getFactoryMethodMetadata()) != null) {
            return Util.forName(metadata.getReturnTypeName());
        }
        ResolvableType rt = beanDefinition.getResolvableType();
        if (ResolvableType.NONE.equalsType(rt)) {
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName == null) {
                return null;
            }
            return Util.forName(beanClassName);
        }
        return rt.resolve();
    }

    public static @Nullable Class<?> forName(String beanClassName) {
        try {
            return Class.forName(beanClassName);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }
}

