/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
 *  org.springframework.core.env.Environment
 *  org.springframework.util.ObjectUtils
 */
package com.kuma.boot.web.httpexchange;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;

class HttpClientBeanDefinitionRegistry
implements BeanDefinitionRegistryPostProcessor {
    static final ScanInfo scanInfo = new ScanInfo();
    private final Environment environment;

    HttpClientBeanDefinitionRegistry(Environment environment) {
        this.environment = environment;
    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        boolean enabled = (Boolean)this.environment.getProperty("http-exchange.enabled", Boolean.class, (Object)true);
        if (!enabled) {
            return;
        }
        this.registerBeans(new HttpClientBeanRegistrar(registry, this.environment));
    }

    void registerBeans(HttpClientBeanRegistrar registrar) {
        HttpExchangeProperties properties = Util.getProperties(this.environment);
        HttpClientBeanDefinitionRegistry.scanInfo.basePackages.addAll(properties.getBasePackages());
        if (!ObjectUtils.isEmpty(HttpClientBeanDefinitionRegistry.scanInfo.basePackages)) {
            registrar.register((String[])HttpClientBeanDefinitionRegistry.scanInfo.basePackages.toArray(String[]::new));
        }
        HttpClientBeanDefinitionRegistry.scanInfo.clients.addAll(properties.getClients());
        if (!ObjectUtils.isEmpty(HttpClientBeanDefinitionRegistry.scanInfo.clients)) {
            registrar.register((Class[])HttpClientBeanDefinitionRegistry.scanInfo.clients.toArray(Class[]::new));
        }
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}

