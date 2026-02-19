/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 *  org.springframework.aop.framework.AopProxyUtils
 *  org.springframework.aot.generate.GenerationContext
 *  org.springframework.aot.generate.MethodReference
 *  org.springframework.aot.hint.ProxyHints
 *  org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution
 *  org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
 *  org.springframework.beans.factory.aot.BeanRegistrationExcludeFilter
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.beans.factory.support.RegisteredBean
 *  org.springframework.core.env.Environment
 *  org.springframework.javapoet.MethodSpec$Builder
 *  org.springframework.util.ClassUtils
 */
package com.kuma.boot.web.httpexchange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import javax.lang.model.element.Modifier;
import org.jspecify.annotations.Nullable;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.generate.MethodReference;
import org.springframework.aot.hint.ProxyHints;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationExcludeFilter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.core.env.Environment;
import org.springframework.javapoet.MethodSpec;
import org.springframework.util.ClassUtils;

class HttpExchangeBeanFactoryInitializationAotProcessor
implements BeanRegistrationExcludeFilter,
BeanFactoryInitializationAotProcessor {
    HttpExchangeBeanFactoryInitializationAotProcessor() {
    }

    public boolean isExcludedFromAotProcessing(RegisteredBean registeredBean) {
        return Util.isHttpExchangeInterface(registeredBean.getBeanClass());
    }

    public @Nullable BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
        return (generationContext, beanFactoryInitializationCode) -> {
            Map<String, BeanDefinition> definitions = HttpExchangeBeanFactoryInitializationAotProcessor.listDefinition(beanFactory);
            if (definitions.isEmpty()) {
                return;
            }
            HttpExchangeBeanFactoryInitializationAotProcessor.registerProxies(generationContext, definitions);
            MethodReference methodReference = beanFactoryInitializationCode.getMethods().add("registerHttpExchangeClientBeanDefinitions", method -> HttpExchangeBeanFactoryInitializationAotProcessor.buildMethod(method, definitions)).toMethodReference();
            beanFactoryInitializationCode.addInitializer(methodReference);
        };
    }

    private static void registerProxies(GenerationContext generationContext, Map<String, BeanDefinition> definitions) {
        ProxyHints proxies = generationContext.getRuntimeHints().proxies();
        definitions.values().stream().map(beanDefinition -> beanDefinition.getResolvableType().resolve()).flatMap(e -> {
            if (e == null) {
                return Stream.empty();
            }
            ArrayList arr = new ArrayList();
            Collections.addAll(arr, e);
            Collections.addAll(arr, ClassUtils.getAllInterfacesForClass((Class)e));
            return arr.stream();
        }).distinct().filter(Util::isHttpExchangeInterface).map(xva$0 -> AopProxyUtils.completeJdkProxyInterfaces((Class[])new Class[]{xva$0})).forEach(arg_0 -> ((ProxyHints)proxies).registerJdkProxy(arg_0));
    }

    private static void buildMethod(MethodSpec.Builder method, Map<String, BeanDefinition> definitions) {
        method.addModifiers(new Modifier[]{Modifier.PUBLIC});
        method.addParameter(DefaultListableBeanFactory.class, "beanFactory", new Modifier[0]);
        method.addParameter(Environment.class, "environment", new Modifier[0]);
        definitions.forEach((beanName, beanDefinition) -> {
            Class clientClass = beanDefinition.getResolvableType().resolve();
            method.addStatement("$T.registerHttpExchangeBean(beanFactory, environment, $T.class)", new Object[]{HttpExchangeUtil.class, clientClass});
        });
    }

    private static Map<String, BeanDefinition> listDefinition(ConfigurableListableBeanFactory beanFactory) {
        HashMap<String, BeanDefinition> beanDefinitions = new HashMap<String, BeanDefinition>();
        for (String name : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
            Class clz = beanDefinition.getResolvableType().resolve();
            if (clz == null || !Util.isHttpExchangeInterface(clz)) continue;
            beanDefinitions.put(name, beanDefinition);
        }
        return beanDefinitions;
    }
}

