/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.context.EnvironmentAware
 *  org.springframework.context.ResourceLoaderAware
 *  org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
 *  org.springframework.context.annotation.ImportBeanDefinitionRegistrar
 *  org.springframework.core.annotation.AnnotationAttributes
 *  org.springframework.core.env.Environment
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.web.support.ext;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

@Component
public class MultiBizProxyRegister
implements ImportBeanDefinitionRegistrar,
BeanClassLoaderAware,
BeanFactoryAware,
EnvironmentAware,
ResourceLoaderAware {
    private BeanFactory beanFactory;
    private Environment envirnoment;
    private ClassLoader classLoader;
    private ResourceLoader resourceLoader;

    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        Set<String> scanPackages = this.getScanPackages(annotationMetadata);
        ClassPathScanningCandidateComponentProvider spiScanner = this.getScanner();
        for (String scanPackage : scanPackages) {
            Set beanDefinitions = spiScanner.findCandidateComponents(scanPackage);
            try {
                for (BeanDefinition beanDefinition : beanDefinitions) {
                    AnnotationMetadata annotationMetadataItem = null;
                    if (beanDefinition instanceof AnnotatedBeanDefinition) {
                        annotationMetadataItem = ((AnnotatedBeanDefinition)beanDefinition).getMetadata();
                    }
                    ExtensionBeanInterceptor interceptor = (ExtensionBeanInterceptor)this.beanFactory.getBean(ExtensionBeanInterceptor.class);
                    Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                    interceptor.setServiceInterface(clazz);
                    Object spiProxyObject = interceptor.getObject();
                    BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(spiProxyObject.getClass());
                    beanDefinitionBuilder.addConstructorArgValue((Object)Proxy.getInvocationHandler(spiProxyObject));
                    AbstractBeanDefinition realBeanDefinition = beanDefinitionBuilder.getBeanDefinition();
                    realBeanDefinition.setPrimary(true);
                    StringBuilder sb = new StringBuilder().append(clazz.getSimpleName()).append("#Proxy");
                    registry.registerBeanDefinition(sb.toString(), (BeanDefinition)realBeanDefinition);
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ClassPathScanningCandidateComponentProvider getScanner() {
        return null;
    }

    private Set<String> getScanPackages(AnnotationMetadata annotationMetadata) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap((Map)annotationMetadata.getAnnotationAttributes(RouterBaseScan.class.getName()));
        HashSet<String> spiScanPackages = null;
        assert (annotationAttributes != null);
        String[] paths = annotationAttributes.getStringArray("path");
        if (paths.length > 0) {
            spiScanPackages = new HashSet<String>();
            spiScanPackages.addAll(Arrays.asList(paths));
        }
        return spiScanPackages;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void setEnvironment(Environment environment) {
        this.envirnoment = environment;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}

