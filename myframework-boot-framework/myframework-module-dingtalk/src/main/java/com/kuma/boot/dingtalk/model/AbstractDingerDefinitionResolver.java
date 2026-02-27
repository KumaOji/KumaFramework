/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.core.ParameterNameDiscoverer
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.annatations.AsyncExecute;
import com.kuma.boot.dingtalk.annatations.DingerConfiguration;
import com.kuma.boot.dingtalk.entity.DingerMethod;
import com.kuma.boot.dingtalk.enums.DingerDefinitionType;
import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.enums.ExceptionEnum;
import com.kuma.boot.dingtalk.exception.DingerException;
import com.kuma.boot.dingtalk.listeners.DingerListenersProperty;
import com.kuma.boot.dingtalk.utils.DingerUtils;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.ParameterNameDiscoverer;

public abstract class AbstractDingerDefinitionResolver<T>
extends DingerListenersProperty
implements DingerDefinitionResolver<T> {
    private Map<String, Class<? extends DingerDefinitionGenerator>> dingerDefinitionGeneratorMap = new HashMap<String, Class<? extends DingerDefinitionGenerator>>();
    protected ParameterNameDiscoverer parameterNameDiscoverer = new DingerParameterNameDiscoverer();

    public AbstractDingerDefinitionResolver() {
        for (DingerDefinitionType dingerDefinitionType : DingerDefinitionType.dingerDefinitionTypes) {
            this.dingerDefinitionGeneratorMap.put(String.valueOf((Object)dingerDefinitionType.dingerType()) + "." + String.valueOf((Object)dingerDefinitionType.messageMainType()) + "." + String.valueOf((Object)dingerDefinitionType.messageSubType()), dingerDefinitionType.dingerDefinitionGenerator());
        }
    }

    protected DingerConfig dingerConfiguration(Class<?> dingerClass) {
        DingerConfiguration dingerConfiguration;
        String tokenId;
        DingerConfig dingerConfig = new DingerConfig();
        if (dingerClass.isAnnotationPresent(DingerConfiguration.class) && DingerUtils.isNotEmpty(tokenId = (dingerConfiguration = dingerClass.getAnnotation(DingerConfiguration.class)).tokenId())) {
            dingerConfig.setTokenId(tokenId);
            dingerConfig.setDecryptKey(dingerConfiguration.decryptKey());
            dingerConfig.setSecret(dingerConfiguration.secret());
        }
        if (dingerClass.isAnnotationPresent(AsyncExecute.class)) {
            dingerConfig.setAsyncExecute(true);
        }
        return dingerConfig;
    }

    void registerDingerDefinition(String dingerName, Object source, String dingerDefinitionKey, DingerConfig dingerConfiguration, DingerMethod dingerMethod) {
        for (DingerType dingerType : enabledDingerTypes) {
            DingerConfig defaultDingerConfig = (DingerConfig)defaultDingerConfigs.get((Object)dingerType);
            if (dingerConfiguration == null) {
                LogUtils.debug((String)"dinger={} not open and skip the corresponding dinger registration.", (Object[])new Object[]{dingerType});
                continue;
            }
            String keyName = String.valueOf((Object)dingerType) + "." + dingerName;
            String key = String.valueOf((Object)dingerType) + "." + dingerDefinitionKey;
            Class<? extends DingerDefinitionGenerator> dingerDefinitionGeneratorClass = this.dingerDefinitionGeneratorMap.get(key);
            if (dingerDefinitionGeneratorClass == null) {
                LogUtils.debug((String)"\u5f53\u524dkey=%s\u5728DingerDefinitionType\u4e2d\u6ca1\u5b9a\u4e49", (Object[])new Object[]{key});
                continue;
            }
            DingerDefinitionGenerator dingerDefinitionGenerator = DingerDefinitionGeneratorFactory.get(dingerDefinitionGeneratorClass.getName());
            DingerDefinition dingerDefinition = dingerDefinitionGenerator.generator(new DingerDefinitionGeneratorContext<Object>(keyName, source));
            if (dingerDefinition == null) {
                LogUtils.debug((String)"keyName={} dinger[{}] format is illegal.", (Object[])new Object[]{keyName, dingerDefinitionKey});
                continue;
            }
            if (Container.INSTANCE.contains(keyName)) {
                throw new DingerException(ExceptionEnum.DINGER_REPEATED_EXCEPTION, keyName);
            }
            if (dingerMethod.check()) {
                throw new DingerException(ExceptionEnum.METHOD_DEFINITION_EXCEPTION, dingerMethod.getMethodName());
            }
            dingerDefinition.setMethodParams(dingerMethod.getMethodParams());
            dingerDefinition.setGenericIndex(dingerMethod.getParamTypes());
            dingerDefinition.dingerConfig().merge(dingerConfiguration).merge(defaultDingerConfig);
            Container.INSTANCE.put(keyName, dingerDefinition);
            LogUtils.debug((String)"dinger definition={} has been registed.", (Object[])new Object[]{keyName});
        }
    }

    protected static void clear() {
        Container.INSTANCE.container.clear();
    }

    protected static enum Container {
        INSTANCE;

        private Map<String, DingerDefinition> container = new HashMap<String, DingerDefinition>(128);

        DingerDefinition get(String key) {
            return this.container.get(key);
        }

        void put(String key, DingerDefinition dingerDefinition) {
            this.container.put(key, dingerDefinition);
        }

        boolean contains(String key) {
            return this.container.containsKey(key);
        }
    }
}

