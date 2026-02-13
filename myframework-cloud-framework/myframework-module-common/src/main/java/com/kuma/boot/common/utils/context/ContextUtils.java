/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.map.MapUtil
 *  cn.hutool.extra.spring.SpringUtil
 *  org.jspecify.annotations.Nullable
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.beans.factory.support.GenericBeanDefinition
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ConfigurableApplicationContext
 *  org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
 *  org.springframework.core.annotation.AnnotationAwareOrderComparator
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.support.DefaultConversionService
 *  org.springframework.core.type.filter.AssignableTypeFilter
 *  org.springframework.core.type.filter.TypeFilter
 */
package com.kuma.boot.common.utils.context;

import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

public class ContextUtils
extends SpringUtil {
    public static final String APPLICATION_NAME = "spring.application.name";
    public static final String DEFAULT_SERVICE_ID = "application";
    public static Class<?> mainClass;
    public static ConfigurableApplicationContext applicationContext;

    public static void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        if (Objects.nonNull(applicationContext)) {
            ContextUtils.applicationContext = applicationContext;
        }
    }

    public static String getServiceId() {
        return applicationContext.getEnvironment().getProperty(APPLICATION_NAME, DEFAULT_SERVICE_ID);
    }

    public static ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> type, boolean required) {
        block6: {
            try {
                if (type == null || applicationContext == null) break block6;
                try {
                    if (required) {
                        return (T)applicationContext.getBean(type);
                    }
                    if (!applicationContext.getBeansOfType(type).isEmpty()) {
                        return (T)applicationContext.getBean(type);
                    }
                }
                catch (NoSuchBeanDefinitionException e) {
                    return null;
                }
            }
            catch (BeansException e) {
                LogUtils.error(e);
            }
        }
        return null;
    }

    public static <T> Map<String, T> getBeansByType(Class<T> clazz) {
        return ContextUtils.getApplicationContext().getBeansOfType(clazz);
    }

    public static <T> T getBean(Class<T> type, String name, boolean required) {
        try {
            if (type != null && applicationContext != null) {
                if (required) {
                    return (T)applicationContext.getBean(name, type);
                }
                if (!applicationContext.getBeansOfType(type).isEmpty()) {
                    return (T)applicationContext.getBean(name, type);
                }
            }
        }
        catch (BeansException e) {
            LogUtils.error(e);
        }
        return null;
    }

    public static <T> T getBean(Class<T> clazz) {
        return (T)ContextUtils.getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return (T)ContextUtils.getApplicationContext().getBean(name, clazz);
    }

    public static Object getBean(String type, boolean required) {
        try {
            if (type != null && applicationContext != null) {
                if (required) {
                    return applicationContext.getBean(type);
                }
                if (applicationContext.containsBean(type)) {
                    return applicationContext.getBean(type);
                }
            }
        }
        catch (BeansException e) {
            LogUtils.error(e);
        }
        return null;
    }

    public static <T> List<T> getBeans(Class<T> type) {
        Map map = ContextUtils.getApplicationContext().getBeansOfType(type);
        if (MapUtil.isEmpty((Map)map)) {
            return null;
        }
        ArrayList beanList = new ArrayList(map.size());
        beanList.addAll(map.values());
        return beanList;
    }

    public static String getBeanDefinitionText() {
        StringBuilder sb = new StringBuilder();
        try {
            Object[] beans = applicationContext.getBeanDefinitionNames();
            Arrays.sort(beans);
            sb = new StringBuilder();
            for (Object bean : beans) {
                sb.append((String)bean).append(" -> ").append(applicationContext.getBean((String)bean).getClass());
            }
        }
        catch (BeansException e) {
            LogUtils.error(e);
        }
        return sb.toString();
    }

    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> anno) {
        Map map;
        try {
            map = applicationContext.getBeansWithAnnotation(anno);
        }
        catch (Exception e) {
            map = null;
        }
        return map;
    }

    public static List<Object> getBeansWithAnnotationList(Class<? extends Annotation> anno) {
        Map<String, Object> beansWithAnnotation = ContextUtils.getBeansWithAnnotation(anno);
        if (null != beansWithAnnotation) {
            return beansWithAnnotation.values().stream().toList();
        }
        return new ArrayList<Object>();
    }

    public static <T> List<T> getBeansOfTypeWithList(Class<T> clazz) {
        Map map;
        try {
            map = applicationContext.getBeansOfType(clazz);
        }
        catch (Exception e) {
            map = null;
        }
        return map == null ? null : new ArrayList(map.values());
    }

    public static void registerBean(String name, Class<?> clazz, Object ... args) {
        ContextUtils.checkRegisterBean((ApplicationContext)applicationContext, name, clazz);
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        for (Object arg : args) {
            beanDefinitionBuilder.addConstructorArgValue(arg);
        }
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry)applicationContext.getBeanFactory();
        beanFactory.registerBeanDefinition(name, (BeanDefinition)beanDefinition);
    }

    public static void registerBean(String name, Class<?> clazz, BeanDefinitionBuilder beanDefinitionBuilder) {
        ContextUtils.checkRegisterBean((ApplicationContext)applicationContext, name, clazz);
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry)applicationContext.getBeanFactory();
        beanFactory.registerBeanDefinition(name, (BeanDefinition)beanDefinition);
    }

    public static void registerSingletonBean(String name, Object obj) {
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory)applicationContext.getAutowireCapableBeanFactory();
        defaultListableBeanFactory.registerSingleton(name, obj);
    }

    public static <T> void registerSingletonBeanSupplier(String name, T t, @Nullable Supplier<T> instanceSupplier) {
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory)applicationContext.getAutowireCapableBeanFactory();
        GenericBeanDefinition definition = new GenericBeanDefinition();
        definition.setBeanClass(t.getClass());
        definition.setInstanceSupplier(instanceSupplier);
        defaultListableBeanFactory.registerBeanDefinition(name, (BeanDefinition)definition);
        applicationContext.refresh();
    }

    public static void destroySingletonBean(String name) {
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory)applicationContext.getAutowireCapableBeanFactory();
        defaultListableBeanFactory.destroySingleton(name);
    }

    public static void unRegisterBean(String name) {
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry)applicationContext.getBeanFactory();
        beanFactory.removeBeanDefinition(name);
    }

    public static void checkRegisterBean(ApplicationContext applicationContext, String name, Class<?> clazz) {
        Object bean;
        if (applicationContext.containsBean(name) && !(bean = applicationContext.getBean(name)).getClass().isAssignableFrom(clazz)) {
            throw new BaseException("BeanName \u91cd\u590d\u6ce8\u518c" + name);
        }
    }

    public static ConversionService getTypeConverter() {
        return DefaultConversionService.getSharedInstance();
    }

    public static <T> List<T> getAllSubclassInstances(Class<T> superClass, String basePackage) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter((TypeFilter)new AssignableTypeFilter(superClass));
        Set components = provider.findCandidateComponents(basePackage);
        ArrayList<T> result = new ArrayList<T>();
        try {
            for (BeanDefinition component : components) {
                result.add(superClass.cast(ContextUtils.getBean(Class.forName(component.getBeanClassName()))));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return result;
    }

    public static <T> List<T> getBeansByOrder(Class<T> type) {
        List<T> order = ContextUtils.getBeans(type);
        AnnotationAwareOrderComparator.sort(order);
        return order;
    }

    public static <T> List<T> tryGetBeansByOrder(Class<T> type) {
        try {
            return ContextUtils.getBeansByOrder(type);
        }
        catch (Exception e) {
            return new ArrayList();
        }
    }
}

