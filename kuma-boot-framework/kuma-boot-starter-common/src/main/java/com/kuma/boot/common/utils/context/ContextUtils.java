/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.utils.context;

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
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
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
import org.jspecify.annotations.Nullable;

/**
 * 上下文工具类
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 17:37:14
 */
public class ContextUtils extends SpringUtil {

    public static final String APPLICATION_NAME = "spring.application.name";

    public static final String DEFAULT_SERVICE_ID = "application";

    /**
     * mainClass
     */
    public static Class<?> mainClass;

    /**
     * applicationContext
     */
    public static ConfigurableApplicationContext applicationContext;

    /**
     * setApplicationContext
     * @param applicationContext applicationContext
     * @since 2021-09-02 17:37:28
     */
    public static void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        if (Objects.nonNull(applicationContext)) {
            ContextUtils.applicationContext = applicationContext;
        }
    }

    public static String getServiceId() {
        return applicationContext
                .getEnvironment()
                .getProperty(APPLICATION_NAME, DEFAULT_SERVICE_ID);
    }

    /**
     * getApplicationContext
     *
     * @return {@link ConfigurableApplicationContext }
     * @since 2021-09-02 17:37:32
     */
    public static ConfigurableApplicationContext getApplicationContext() {
        if (applicationContext != null) {
            return applicationContext;
        }
        ApplicationContext ctx = SpringUtil.getApplicationContext();
        return ctx instanceof ConfigurableApplicationContext ? (ConfigurableApplicationContext) ctx : null;
    }

    /**
     * 获取bean
     * @param type 类型
     * @param required 是否必须
     * @return T
     * @since 2021-09-02 17:37:46
     */
    public static <T> T getBean(Class<T> type, boolean required) {
        try {
            if (type != null && applicationContext != null) {
                try {
                    if (required) {
                        return applicationContext.getBean(type);
                    } else {
                        if (!applicationContext.getBeansOfType(type).isEmpty()) {
                            return applicationContext.getBean(type);
                        }
                    }
                } catch (NoSuchBeanDefinitionException e) {
                    return null;
                }
            }
        } catch (BeansException e) {
            LogUtils.error(e);
        }

        return null;
    }

    /**
     * 通过接口类型(clazz)获取所以Bean.
     * @param clazz
     * @param <T>
     * @return key(实现类的beanName)->value(实现类)
     */
    public static <T> Map<String, T> getBeansByType(Class<T> clazz) {
        return getApplicationContext().getBeansOfType(clazz);
    }

    public static <T> T getBean(Class<T> type, String name, boolean required) {
        try {
            if (type != null && applicationContext != null) {
                if (required) {
                    return applicationContext.getBean(name, type);
                } else {
                    if (!applicationContext.getBeansOfType(type).isEmpty()) {
                        return applicationContext.getBean(name, type);
                    }
                }
            }
        } catch (BeansException e) {
            LogUtils.error(e);
        }
        return null;
    }

    /**
     * 通过name获取 Bean.
     * @param name
     * @return
     */
    // public static Object getBean(String name) {
    // return getApplicationContext().getBean(name);
    // }

    /**
     * 通过class获取Bean.
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    /**
     * 获取bean
     * @param type 类型
     * @param required 是否必须
     * @return {@link Object }
     * @since 2021-09-02 17:37:57
     */
    public static Object getBean(String type, boolean required) {
        try {
            if (type != null && applicationContext != null) {
                if (required) {
                    return applicationContext.getBean(type);
                } else {
                    if (applicationContext.containsBean(type)) {
                        return applicationContext.getBean(type);
                    }
                }
            }
        } catch (BeansException e) {
            LogUtils.error(e);
        }
        return null;
    }

    /***
     * 获取指定类型的全部实现类
     * @param type
     * @param <T>
     * @return
     */
    public static <T> List<T> getBeans(Class<T> type) {
        Map<String, T> map = getApplicationContext().getBeansOfType(type);
        if (MapUtil.isEmpty(map)) {
            return null;
        }
        List<T> beanList = new ArrayList<>(map.size());
        beanList.addAll(map.values());
        return beanList;
    }

    /**
     * 获取bean定义信息
     * @return {@link String }
     * @since 2021-09-02 17:38:08
     */
    public static String getBeanDefinitionText() {
        StringBuilder sb = new StringBuilder();
        try {
            String[] beans = applicationContext.getBeanDefinitionNames();
            Arrays.sort(beans);
            sb = new StringBuilder();
            for (String bean : beans) {
                sb.append(bean).append(" -> ").append(applicationContext.getBean(bean).getClass());
            }
        } catch (BeansException e) {
            LogUtils.error(e);
        }
        return sb.toString();
    }

    /**
     * 获取所有被注解的
     * @param anno anno
     * @return {@link Map }
     * @since 2021-09-02 17:38:14
     */
    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> anno) {
        Map<String, Object> map;
        try {
            // 获取注解的 bean
            map = applicationContext.getBeansWithAnnotation(anno);
        } catch (Exception e) {
            map = null;
        }
        return map;
    }

    public static List<Object> getBeansWithAnnotationList(Class<? extends Annotation> anno) {
        Map<String, Object> beansWithAnnotation = getBeansWithAnnotation(anno);
        if (null != beansWithAnnotation) {
            return beansWithAnnotation.values().stream().toList();
        }
        return new ArrayList<>();
    }

    /**
     * 获取 bean 的类型
     * @param clazz clazz
     * @return {@link List }
     * @since 2021-09-02 17:38:22
     */
    public static <T> List<T> getBeansOfTypeWithList(Class<T> clazz) {
        // 声明一个结果
        Map<String, T> map;
        try {
            // 获取类型
            map = applicationContext.getBeansOfType(clazz);
        } catch (Exception e) {
            map = null;
        }
        // 返回 bean 的类型
        return map == null ? null : new ArrayList<>(map.values());
    }

    /**
     * 注册bean
     * @param name name
     * @param clazz clazz
     * @param args args
     * @since 2021-09-02 17:38:27
     */
    public static void registerBean(String name, Class<?> clazz, Object... args) {
        checkRegisterBean(applicationContext, name, clazz);
        BeanDefinitionBuilder beanDefinitionBuilder =
                BeanDefinitionBuilder.genericBeanDefinition(clazz);
        for (Object arg : args) {
            beanDefinitionBuilder.addConstructorArgValue(arg);
        }
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        BeanDefinitionRegistry beanFactory =
                (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        beanFactory.registerBeanDefinition(name, beanDefinition);
    }

    /**
     * 注册bean
     * @param name name
     * @param clazz clazz
     * @param beanDefinitionBuilder beanDefinitionBuilder
     * @since 2021-09-02 17:38:33
     */
    public static void registerBean(
            String name, Class<?> clazz, BeanDefinitionBuilder beanDefinitionBuilder) {
        checkRegisterBean(applicationContext, name, clazz);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        BeanDefinitionRegistry beanFactory =
                (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        beanFactory.registerBeanDefinition(name, beanDefinition);
    }

    public static void registerSingletonBean(String name, Object obj) {
        DefaultListableBeanFactory defaultListableBeanFactory =
                (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        defaultListableBeanFactory.registerSingleton(name, obj);
    }

    public static <T> void registerSingletonBeanSupplier(
            String name, T t, @Nullable Supplier<T> instanceSupplier) {
        DefaultListableBeanFactory defaultListableBeanFactory =
                (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

        GenericBeanDefinition definition = new GenericBeanDefinition();
        definition.setBeanClass(t.getClass());
        definition.setInstanceSupplier(instanceSupplier);

        defaultListableBeanFactory.registerBeanDefinition(name, definition);
        applicationContext.refresh();
    }

    public static void destroySingletonBean(String name) {
        DefaultListableBeanFactory defaultListableBeanFactory =
                (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        defaultListableBeanFactory.destroySingleton(name);
    }

    /**
     * 取消注册bean
     * @param name name
     * @since 2021-09-02 17:38:37
     */
    public static void unRegisterBean(String name) {
        BeanDefinitionRegistry beanFactory =
                (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        beanFactory.removeBeanDefinition(name);
    }

    /**
     * 检查已注册的bean
     * @param applicationContext applicationContext
     * @param name name
     * @param clazz clazz
     * @since 2021-09-02 17:38:43
     */
    public static void checkRegisterBean(
            ApplicationContext applicationContext, String name, Class<?> clazz) {
        if (applicationContext.containsBean(name)) {
            Object bean = applicationContext.getBean(name);
            if (!bean.getClass().isAssignableFrom(clazz)) {
                throw new BaseException("BeanName 重复注册" + name);
            }
        }
    }

    /**
     * 得到类型转换器
     * @return {@link ConversionService }
     * @since 2022-07-12 13:30:54
     */
    public static ConversionService getTypeConverter() {
        return DefaultConversionService.getSharedInstance();
    }

    /**
     * 获取指定类的在指定包下所有子类实例
     * @param superClass 父类类型
     * @param basePackage 扫描的包
     * @return 获取指定类的所有子类实例
     */
    public static <T> List<T> getAllSubclassInstances(Class<T> superClass, String basePackage) {
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(superClass));
        Set<BeanDefinition> components = provider.findCandidateComponents(basePackage);
        List<T> result = new ArrayList<>();
        try {
            for (BeanDefinition component : components) {
                result.add(superClass.cast(getBean(Class.forName(component.getBeanClassName()))));
            }
        } catch (Exception error) {
            // log.error("SpringBeanUtil getAllSubclassInstances error:", error);
        }
        return result;
    }

    public static <T> List<T> getBeansByOrder(Class<T> type) {
        List<T> order = getBeans(type);
        AnnotationAwareOrderComparator.sort(order);
        return order;
    }

    public static <T> List<T> tryGetBeansByOrder(Class<T> type) {
        try {
            return getBeansByOrder(type);
        } catch (Exception e) {
            return new ArrayList<T>();
        }
    }
}
