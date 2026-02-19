/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.aop.scope.ScopedProxyUtils
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionOverrideException
 *  org.springframework.beans.factory.support.BeanDefinitionReaderUtils
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.boot.context.properties.bind.Binder
 *  org.springframework.core.NativeDetector
 *  org.springframework.core.env.Environment
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package com.kuma.boot.web.httpexchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionOverrideException;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.NativeDetector;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public final class HttpExchangeUtil {
    private static final Logger log = LoggerFactory.getLogger(HttpExchangeUtil.class);
    private static final boolean SPRING_CLOUD_CONTEXT_PRESENT = ClassUtils.isPresent((String)"org.springframework.cloud.context.scope.refresh.RefreshScope", null);

    private HttpExchangeUtil() {
    }

    public static void registerHttpExchangeBean(DefaultListableBeanFactory beanFactory, Environment environment, Class<?> clz) {
        Assert.isTrue((boolean)Util.isHttpExchangeInterface(clz), () -> String.valueOf(clz) + " is not a HttpExchange client");
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(clz, () -> new ExchangeClientCreator((BeanFactory)beanFactory, clz).create()).getBeanDefinition();
        beanDefinition.setLazyInit(true);
        beanDefinition.setPrimary(true);
        beanDefinition.setResourceDescription("registered by httpexchange-spring-boot-starter");
        String className = clz.getName();
        try {
            if (HttpExchangeUtil.getRefresh(environment).isEnabled() && SPRING_CLOUD_CONTEXT_PRESENT && !HttpExchangeUtil.isAotProcessing() && !NativeDetector.inNativeImage()) {
                beanDefinition.setScope("refresh");
                BeanDefinitionHolder scopedProxy = ScopedProxyUtils.createScopedProxy((BeanDefinitionHolder)new BeanDefinitionHolder((BeanDefinition)beanDefinition, className), (BeanDefinitionRegistry)beanFactory, (boolean)false);
                BeanDefinitionReaderUtils.registerBeanDefinition((BeanDefinitionHolder)scopedProxy, (BeanDefinitionRegistry)beanFactory);
            } else {
                BeanDefinitionReaderUtils.registerBeanDefinition((BeanDefinitionHolder)new BeanDefinitionHolder((BeanDefinition)beanDefinition, className), (BeanDefinitionRegistry)beanFactory);
            }
        }
        catch (BeanDefinitionOverrideException ignore) {
            log.warn("Remove @HttpExchanges client '{}' from 'clients' property; it's already in base packages", (Object)className);
        }
    }

    private static boolean isAotProcessing() {
        return Boolean.getBoolean("spring.aot.processing");
    }

    private static HttpExchangeProperties.Refresh getRefresh(Environment environment) {
        return (HttpExchangeProperties.Refresh)Binder.get((Environment)environment).bind("http-exchange.refresh", HttpExchangeProperties.Refresh.class).orElseGet(HttpExchangeProperties.Refresh::new);
    }
}

