/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.context.annotation.ImportBeanDefinitionRegistrar
 *  org.springframework.core.Ordered
 *  org.springframework.core.annotation.AnnotationAttributes
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.objenesis.instantiator.util.ClassUtils
 */
package com.kuma.boot.dingtalk.multi;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.annatations.EnableMultiDinger;
import com.kuma.boot.dingtalk.annatations.MultiDinger;
import com.kuma.boot.dingtalk.annatations.MultiHandler;
import com.kuma.boot.dingtalk.entity.ExceptionPairs;
import com.kuma.boot.dingtalk.entity.MultiDingerAlgorithmDefinition;
import com.kuma.boot.dingtalk.entity.MultiDingerConfig;
import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.enums.ExceptionEnum;
import com.kuma.boot.dingtalk.enums.MultiDingerConfigContainer;
import com.kuma.boot.dingtalk.exception.DingerException;
import com.kuma.boot.dingtalk.exception.MultiDingerRegisterException;
import com.kuma.boot.dingtalk.listeners.DingerListenersProperty;
import com.kuma.boot.dingtalk.model.DingerConfig;
import com.kuma.boot.dingtalk.utils.DingerUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

public class MultiDingerScannerRegistrar
extends DingerListenersProperty
implements ImportBeanDefinitionRegistrar,
Ordered {
    protected static final Map<String, MultiDingerAlgorithmDefinition> MULTIDINGER_ALGORITHM_DEFINITION_MAP = new HashMap<String, MultiDingerAlgorithmDefinition>();

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        try {
            this.doScanAndRegister(importingClassMetadata, registry);
        }
        catch (DingerException ex) {
            throw new MultiDingerRegisterException(ex.getPairs(), ex.getMessage());
        }
        catch (Exception ex) {
            throw new DingerException(ex, (ExceptionPairs)ExceptionEnum.UNKNOWN);
        }
        finally {
            MultiDingerScannerRegistrar.emptyDingerClasses();
        }
    }

    private void doScanAndRegister(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!importingClassMetadata.hasAnnotation(EnableMultiDinger.class.getName())) {
            LogUtils.warn((String)"import class can't find EnableMultiDinger annotation.", (Object[])new Object[0]);
            return;
        }
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap((Map)importingClassMetadata.getAnnotationAttributes(EnableMultiDinger.class.getName()));
        AnnotationAttributes[] value = annotationAttributes.getAnnotationArray("value");
        boolean aloneMulti = value.length == 0;
        LogUtils.info((String)"multi dinger register and is it global register? {}.", (Object[])new Object[]{!aloneMulti});
        if (aloneMulti) {
            List<Class<?>> dingerClasses = MultiDingerScannerRegistrar.dingerClasses();
            if (dingerClasses.isEmpty()) {
                LogUtils.warn((String)"dinger class is empty, so no need to deal with multiDinger.", (Object[])new Object[0]);
                return;
            }
            this.multiDingerHandler(registry, dingerClasses);
            if (!MultiDingerConfigContainer.INSTANCE.isEmpty()) {
                MultiDingerProperty.multiDinger = true;
            }
        } else {
            for (AnnotationAttributes attributes : value) {
                DingerType dinger = (DingerType)attributes.getEnum("dinger");
                Class handler = attributes.getClass("handler");
                LogUtils.debug((String)"enable {} global multi dinger, and multiDinger handler class={}.", (Object[])new Object[]{dinger, handler.getName()});
                DingerConfigHandler dingerConfigHandler = (DingerConfigHandler)BeanUtils.instantiateClass((Class)handler);
                this.registerHandler(registry, dinger, String.valueOf((Object)dinger) + "." + MultiDingerConfigContainer.GLOABL_KEY, dingerConfigHandler);
                MultiDingerProperty.multiDinger = true;
            }
        }
    }

    private void multiDingerHandler(BeanDefinitionRegistry registry, List<Class<?>> dingerClasses) {
        int valid = 0;
        for (Class<?> dingerClass : dingerClasses) {
            if (!dingerClass.isAnnotationPresent(MultiHandler.class)) continue;
            MultiHandler multiDinger = dingerClass.getAnnotation(MultiHandler.class);
            MultiDinger value = multiDinger.value();
            Class<? extends DingerConfigHandler> dingerConfigHandler = value.handler();
            String beanName = dingerConfigHandler.getSimpleName();
            if (dingerConfigHandler.isInterface()) {
                LogUtils.warn((String)"dingerClass={} handler className={} is interface and skip.", (Object[])new Object[]{dingerClass.getSimpleName(), beanName});
                continue;
            }
            String key = dingerClass.getName();
            DingerConfigHandler handler = (DingerConfigHandler) BeanUtils.instantiateClass(dingerConfigHandler);
            DingerType dinger = value.dinger();
            this.registerHandler(registry, dinger, String.valueOf((Object)dinger) + "." + key, handler);
            LogUtils.debug((String)"regiseter multi dinger for dingerClass={} and dingerConfigHandler={}.", (Object[])new Object[]{dingerClass.getSimpleName(), beanName});
            ++valid;
        }
        if (valid == 0) {
            LogUtils.warn((String)"enable global multi dinger but none dinger interface be decorated with @MultiHandler.", (Object[])new Object[0]);
        }
    }

    private void registerHandler(BeanDefinitionRegistry registry, DingerType dinger, String key, DingerConfigHandler dingerConfigHandler) {
        String dingerConfigHandlerClassName = dingerConfigHandler.getClass().getSimpleName();
        if (!dinger.isEnabled()) {
            throw new DingerException(ExceptionEnum.MULTIDINGER_ANNOTATTION_EXCEPTION, new Object[]{key, dinger});
        }
        Class<? extends AlgorithmHandler> algorithm = dingerConfigHandler.algorithmHandler();
        List<DingerConfig> dingerConfigs = dingerConfigHandler.dingerConfigs();
        dingerConfigs = dingerConfigs == null ? new ArrayList<>() : new ArrayList<>(dingerConfigs);
        if (algorithm == null) {
            throw new DingerException(ExceptionEnum.MULTIDINGER_ALGORITHM_EXCEPTION, dingerConfigHandlerClassName);
        }
        for (int i = 0; i < dingerConfigs.size(); ++i) {
            DingerConfig dingerConfig = (DingerConfig)dingerConfigs.get(i);
            dingerConfig.setDingerType(dinger);
            if (!DingerUtils.isEmpty(dingerConfig.getTokenId())) continue;
            throw new DingerException(ExceptionEnum.DINGER_CONFIG_HANDLER_EXCEPTION, dingerConfigHandlerClassName, i);
        }
        long injectionCnt = Arrays.stream(algorithm.getDeclaredFields()).filter(e -> e.isAnnotationPresent(Autowired.class)).count();
        AnalysisEnum mode = AnalysisEnum.REFLECT;
        if (injectionCnt == 0L) {
            AlgorithmHandler algorithmHandler = (AlgorithmHandler) BeanUtils.instantiateClass(algorithm);
            MultiDingerConfigContainer.INSTANCE.put(key, new MultiDingerConfig(algorithmHandler, dingerConfigs));
        } else {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(algorithm);
            AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
            beanDefinition.setAutowireMode(2);
            String beanName = key + "." + algorithm.getSimpleName();
            registry.registerBeanDefinition(beanName, (BeanDefinition)beanDefinition);
            MULTIDINGER_ALGORITHM_DEFINITION_MAP.put(beanName, new MultiDingerAlgorithmDefinition(key, algorithm, dingerConfigs, dingerConfigHandlerClassName));
            mode = AnalysisEnum.SPRING_CONTAINER;
        }
        LogUtils.debug((String)"key={}, algorithm={} analysis through mode {}.", (Object[])new Object[]{key, algorithm.getSimpleName(), mode});
    }

    public int getOrder() {
        return 0x7FFFFFFE;
    }

    public static enum AnalysisEnum {
        REFLECT,
        SPRING_CONTAINER;

    }
}

