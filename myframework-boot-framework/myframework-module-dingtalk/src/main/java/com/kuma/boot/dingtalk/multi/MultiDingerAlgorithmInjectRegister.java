/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.kuma.boot.dingtalk.multi;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.entity.ExceptionPairs;
import com.kuma.boot.dingtalk.entity.MultiDingerAlgorithmDefinition;
import com.kuma.boot.dingtalk.entity.MultiDingerConfig;
import com.kuma.boot.dingtalk.enums.ExceptionEnum;
import com.kuma.boot.dingtalk.enums.MultiDingerConfigContainer;
import com.kuma.boot.dingtalk.exception.DingerException;
import com.kuma.boot.dingtalk.exception.MultiDingerRegisterException;
import com.kuma.boot.dingtalk.model.DingerConfig;
import com.kuma.boot.dingtalk.utils.DingerUtils;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class MultiDingerAlgorithmInjectRegister
implements ApplicationContextAware,
InitializingBean {
    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (MultiDingerAlgorithmInjectRegister.applicationContext == null) {
            MultiDingerAlgorithmInjectRegister.applicationContext = applicationContext;
        } else {
            LogUtils.warn((String)"applicationContext is not null.", (Object[])new Object[0]);
        }
    }

    public void afterPropertiesSet() throws Exception {
        if (MultiDingerScannerRegistrar.MULTIDINGER_ALGORITHM_DEFINITION_MAP.isEmpty()) {
            LogUtils.info((String)"AlgorithmHandler Container is Empty.", (Object[])new Object[0]);
            return;
        }
        try {
            this.multiDingerWithInjectAttributeHandler();
        }
        catch (DingerException ex) {
            throw new MultiDingerRegisterException(ex.getPairs(), ex.getMessage());
        }
        catch (Exception ex) {
            throw new DingerException(ex, (ExceptionPairs)ExceptionEnum.UNKNOWN);
        }
    }

    private void multiDingerWithInjectAttributeHandler() {
        Set<Map.Entry<String, MultiDingerAlgorithmDefinition>> entries = MultiDingerScannerRegistrar.MULTIDINGER_ALGORITHM_DEFINITION_MAP.entrySet();
        for (Map.Entry<String, MultiDingerAlgorithmDefinition> entry : entries) {
            String beanName = entry.getKey();
            MultiDingerAlgorithmDefinition v = entry.getValue();
            Class<? extends AlgorithmHandler> algorithm = v.getAlgorithm();
            AlgorithmHandler algorithmHandler = (AlgorithmHandler)applicationContext.getBean(beanName, algorithm);
            this.algorithmFieldInjection(algorithm, algorithmHandler);
            List<DingerConfig> dingerConfigs = v.getDingerConfigs();
            MultiDingerConfigContainer.INSTANCE.put(v.getKey(), new MultiDingerConfig(algorithmHandler, dingerConfigs));
            LogUtils.info((String)"dingerClassName={} exist spring inject info and algorithmHandler class={}, dingerConfigs={}.", (Object[])new Object[]{v.getKey(), algorithm.getSimpleName(), dingerConfigs.size()});
        }
        MultiDingerScannerRegistrar.MULTIDINGER_ALGORITHM_DEFINITION_MAP.clear();
    }

    private void algorithmFieldInjection(Class<? extends AlgorithmHandler> algorithm, AlgorithmHandler algorithmHandler) {
        String algorithmSimpleName = algorithm.getSimpleName();
        for (Field declaredField : algorithm.getDeclaredFields()) {
            String[] actualBeanNames;
            int length;
            Qualifier qualifier;
            if (!declaredField.isAnnotationPresent(Autowired.class)) continue;
            String fieldBeanName = declaredField.getName();
            if (declaredField.isAnnotationPresent(Qualifier.class) && DingerUtils.isNotEmpty((qualifier = declaredField.getAnnotation(Qualifier.class)).value())) {
                fieldBeanName = qualifier.value();
            }
            if ((length = (actualBeanNames = applicationContext.getBeanNamesForType(declaredField.getType())).length) == 1) {
                fieldBeanName = actualBeanNames[0];
            } else if (length > 1) {
                String fbn = fieldBeanName;
                long count = Arrays.stream(actualBeanNames).filter(e -> Objects.equals(e, fbn)).count();
                if (count == 0L) {
                    throw new DingerException(ExceptionEnum.ALGORITHM_FIELD_INSTANCE_NOT_MATCH, algorithmSimpleName, fieldBeanName);
                }
            } else {
                throw new DingerException(ExceptionEnum.ALGORITHM_FIELD_INSTANCE_NOT_EXISTS, algorithmSimpleName, fieldBeanName);
            }
            try {
                declaredField.setAccessible(true);
                declaredField.set(algorithmHandler, applicationContext.getBean(fieldBeanName));
            }
            catch (IllegalAccessException e2) {
                throw new DingerException(ExceptionEnum.ALGORITHM_FIELD_INJECT_FAILED, algorithmSimpleName, fieldBeanName);
            }
        }
    }

    protected static void clear() {
        applicationContext = null;
    }
}

