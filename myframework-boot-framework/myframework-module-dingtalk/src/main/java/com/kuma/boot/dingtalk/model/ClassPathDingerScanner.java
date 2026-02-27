/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.GenericBeanDefinition
 *  org.springframework.context.annotation.ClassPathBeanDefinitionScanner
 *  org.springframework.core.type.filter.AnnotationTypeFilter
 *  org.springframework.core.type.filter.AssignableTypeFilter
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.util.ClassUtils
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.spring.DingerFactoryBean;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

public class ClassPathDingerScanner
extends ClassPathBeanDefinitionScanner {
    private List<Class<?>> dingerClasses = new ArrayList();
    private Class<? extends Annotation> annotationClass;
    private Class<?> markerInterface;

    public ClassPathDingerScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public void setMarkerInterface(Class<?> markerInterface) {
        this.markerInterface = markerInterface;
    }

    public void registerFilters() {
        boolean acceptAllInterfaces = true;
        if (this.annotationClass != null) {
            this.addIncludeFilter((TypeFilter)new AnnotationTypeFilter(this.annotationClass));
            acceptAllInterfaces = false;
        }
        if (this.markerInterface != null) {
            this.addIncludeFilter((TypeFilter)new AssignableTypeFilter(this, this.markerInterface){
                {
                    Objects.requireNonNull(this$0);
                    super(targetType);
                }

                protected boolean matchClassName(String className) {
                    return false;
                }
            });
            acceptAllInterfaces = false;
        }
        if (acceptAllInterfaces) {
            this.addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
        }
        this.addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }

    public Set<BeanDefinitionHolder> doScan(String ... basePackages) {
        Set beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            LogUtils.warn((String)"No Dinger was found in '{}' package. Please check your configuration.", (Object[])new Object[]{Arrays.toString(basePackages)});
        } else {
            this.processBeanDefinitions(beanDefinitions);
        }
        return beanDefinitions;
    }

    protected boolean isCandidateComponent(AnnotatedBeanDefinition annotatedBeanDefinition) {
        return annotatedBeanDefinition.getMetadata().isInterface() && annotatedBeanDefinition.getMetadata().isIndependent();
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        for (BeanDefinitionHolder beanDefinition : beanDefinitions) {
            GenericBeanDefinition definition = (GenericBeanDefinition)beanDefinition.getBeanDefinition();
            String beanClassName = definition.getBeanClassName();
            LogUtils.debug((String)"Creating DingerFactoryBean with name '{}' and '{}' dingerInterface", (Object[])new Object[]{beanDefinition.getBeanName(), beanClassName});
            try {
                this.dingerClasses.add(ClassUtils.forName((String)beanClassName, (ClassLoader)((Object)((Object)this)).getClass().getClassLoader()));
            }
            catch (ClassNotFoundException e) {
                LogUtils.warn((String)"beanClassName=[{}] not found", (Object[])new Object[]{beanClassName});
            }
            definition.setBeanClass(DingerFactoryBean.class);
            definition.getConstructorArgumentValues().addGenericArgumentValue((Object)beanClassName);
            definition.setAutowireMode(2);
        }
    }

    public List<Class<?>> getDingerClasses() {
        return this.dingerClasses;
    }
}

