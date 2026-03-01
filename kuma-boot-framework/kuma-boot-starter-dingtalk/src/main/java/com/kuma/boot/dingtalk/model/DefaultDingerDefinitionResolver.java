/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.context.EnvironmentAware
 *  org.springframework.core.env.Environment
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.enums.ExceptionEnum;
import com.kuma.boot.dingtalk.exception.DingerException;
import com.kuma.boot.dingtalk.listeners.DingerListenersProperty;
import com.kuma.boot.dingtalk.utils.DingerUtils;
import java.io.IOException;
import java.util.List;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class DefaultDingerDefinitionResolver
extends DingerListenersProperty
implements EnvironmentAware {
    private final DingerDefinitionResolver xmlDingerDefinitionResolver = new XmlDingerDefinitionResolver();
    private final DingerDefinitionResolver annotaDingerDefinitionResolver = new AnnotationDingerDefinitionResolver();
    private Environment environment;

    protected void resolver(List<Class<?>> dingerClasses) {
        this.registerDefaultDingerConfig(this.environment);
        this.dingerXmlResolver();
        this.annotaDingerDefinitionResolver.resolver(dingerClasses);
    }

    protected void dingerXmlResolver() {
        Resource[] resources;
        String dingerLocationsProp = "kuma.boot.dingtalk.dinger-locations";
        String dingerLocations = this.environment.getProperty(dingerLocationsProp);
        if (dingerLocations == null) {
            LogUtils.debug((String)"dinger xml is not configured.", (Object[])new Object[0]);
            return;
        }
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            resources = resolver.getResources(dingerLocations);
        }
        catch (IOException e) {
            throw new DingerException(ExceptionEnum.RESOURCE_CONFIG_EXCEPTION, dingerLocations);
        }
        if (resources.length == 0) {
            LogUtils.warn((String)"dinger xml is empty under {}.", (Object[])new Object[]{dingerLocations});
            return;
        }
        this.xmlDingerDefinitionResolver.resolver(resources);
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    private void registerDefaultDingerConfig(Environment environment) {
        if (environment == null) {
            LogUtils.warn((String)"environment is null.", (Object[])new Object[0]);
            return;
        }
        for (DingerType dingerType : enabledDingerTypes) {
            String dingers = "kuma.boot.dingtalk.dingers." + dingerType.name().toLowerCase() + ".";
            String tokenIdProp = dingers + "token-id";
            String secretProp = dingers + "secret";
            String decryptProp = dingers + "decrypt";
            String decryptKeyProp = dingers + "decryptKey";
            String asyncExecuteProp = dingers + "async";
            if (DingerUtils.isEmpty(tokenIdProp)) {
                LogUtils.debug((String)"dinger={} is not open.", (Object[])new Object[]{dingerType});
                continue;
            }
            String tokenId = environment.getProperty(tokenIdProp);
            String secret = environment.getProperty(secretProp);
            boolean decrypt = this.getProperty(environment, decryptProp);
            boolean async = this.getProperty(environment, asyncExecuteProp);
            DingerConfig defaultDingerConfig = DingerConfig.instance(tokenId);
            defaultDingerConfig.setDingerType(dingerType);
            defaultDingerConfig.setSecret(secret);
            if (decrypt) {
                defaultDingerConfig.setDecryptKey(environment.getProperty(decryptKeyProp));
            }
            defaultDingerConfig.setAsyncExecute(async);
            defaultDingerConfig.check();
            defaultDingerConfigs.put(dingerType, defaultDingerConfig);
        }
    }

    private boolean getProperty(Environment environment, String prop) {
        if (environment.getProperty(prop) != null) {
            return (Boolean)environment.getProperty(prop, Boolean.TYPE);
        }
        return false;
    }
}

