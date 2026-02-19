/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.boot.CommandLineRunner
 *  org.springframework.boot.SpringBootVersion
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.event.ApplicationReadyEvent
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.annotation.Bean
 *  org.springframework.core.env.Environment
 *  org.springframework.web.service.annotation.HttpExchange
 */
package com.kuma.boot.web.httpexchange;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.service.annotation.HttpExchange;

@AutoConfiguration
@ConditionalOnClass(value={HttpExchange.class})
@ConditionalOnProperty(prefix="http-exchange", name={"enabled"}, matchIfMissing=true)
@EnableConfigurationProperties(value={HttpExchangeProperties.class})
public class HttpExchangeAutoConfiguration
implements DisposableBean,
InitializingBean,
ApplicationListener<ApplicationReadyEvent> {
    public void afterPropertiesSet() {
        HttpExchangeAutoConfiguration.checkVersion();
    }

    public void onApplicationEvent(ApplicationReadyEvent event) {
        ConfigurableListableBeanFactory bf = event.getApplicationContext().getBeanFactory();
        if (bf instanceof BeanDefinitionRegistry) {
            BeanDefinitionRegistry bdr = (BeanDefinitionRegistry)bf;
            HttpClientBeanRegistrar.clearBeanDefinitionCache(bdr);
        }
    }

    @Bean
    static HttpClientBeanDefinitionRegistry httpClientBeanDefinitionRegistry(Environment environment) {
        return new HttpClientBeanDefinitionRegistry(environment);
    }

    @Bean
    @ConditionalOnMissingBean
    public BeanParamArgumentResolver beanParamArgumentResolver(HttpExchangeProperties properties) {
        return new BeanParamArgumentResolver(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix="http-exchange", name={"warn-unused-config-enabled"}, matchIfMissing=true)
    public CommandLineRunner httpExchangeStarterUnusedConfigChecker(HttpExchangeProperties properties) {
        return args -> Checker.checkUnusedConfig(properties);
    }

    public void destroy() {
        Cache.clear();
        HttpClientBeanDefinitionRegistry.scanInfo.clear();
    }

    @Bean
    static HttpExchangeBeanFactoryInitializationAotProcessor httpExchangeStarterHttpExchangeBeanFactoryInitializationAotProcessor() {
        return new HttpExchangeBeanFactoryInitializationAotProcessor();
    }

    private static void checkVersion() {
        String requiredVersion;
        String version = SpringBootVersion.getVersion();
        if (version.compareTo(requiredVersion = "4.0.0") < 0) {
            throw new SpringBootVersionIncompatibleException(version, requiredVersion);
        }
    }
}

