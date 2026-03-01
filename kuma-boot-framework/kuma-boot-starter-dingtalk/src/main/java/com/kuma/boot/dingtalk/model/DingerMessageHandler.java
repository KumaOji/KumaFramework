/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.annatations.Dinger;
import com.kuma.boot.dingtalk.autoconfigure.properties.DingtalkProperties;
import com.kuma.boot.dingtalk.entity.DingerResponse;
import com.kuma.boot.dingtalk.entity.MsgType;
import com.kuma.boot.dingtalk.entity.MultiDingerConfig;
import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.enums.MultiDingerConfigContainer;
import com.kuma.boot.dingtalk.multi.MultiDingerProperty;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class DingerMessageHandler
extends MultiDingerProperty
implements ParamHandler,
MessageTransfer,
ResultHandler<DingerResponse> {
    protected DingerRobot dingerRobot;
    protected DingtalkProperties dingtalkProperties;

    @Override
    public Map<String, Object> paramsHandler(Method method, DingerDefinition dingerDefinition, Object[] values) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        int valueLength = values.length;
        if (valueLength == 0) {
            return params;
        }
        String[] keys = dingerDefinition.methodParams();
        int[] genericIndex = dingerDefinition.genericIndex();
        if (genericIndex.length > 0) {
            for (int i : genericIndex) {
                params.put(keys[i], values[i]);
            }
            return params;
        }
        int keyLength = keys.length;
        if (keyLength == valueLength) {
            for (int i = 0; i < valueLength; ++i) {
                params.put(keys[i], values[i]);
            }
            return params;
        }
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < Objects.requireNonNull(parameters).length; ++i) {
            Parameter parameter = parameters[i];
            String paramName = parameter.getName();
            com.kuma.boot.dingtalk.annatations.Parameter[] panno = (com.kuma.boot.dingtalk.annatations.Parameter[])parameter.getDeclaredAnnotationsByType(com.kuma.boot.dingtalk.annatations.Parameter.class);
            if (panno != null && panno.length > 0) {
                paramName = panno[0].value();
            }
            params.put(paramName, values[i]);
        }
        return params;
    }

    @Override
    public MsgType transfer(DingerDefinition dingerDefinition, Map<String, Object> params) {
        MsgType message = this.copyProperties(dingerDefinition.message());
        if (message != null) {
            message.transfer(params);
        }
        return message;
    }

    @Override
    public Object resultHandler(Class<?> resultType, DingerResponse dingerResponse) {
        String name = resultType.getName();
        if (String.class.getName().equals(name)) {
            return Optional.ofNullable(dingerResponse).map(e -> e.getData()).orElse(null);
        }
        if (DingerResponse.class.getName().equals(name)) {
            return dingerResponse;
        }
        return null;
    }

    private MsgType copyProperties(MsgType src) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(src);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            return (MsgType) in.readObject();
        }
        catch (Exception e) {
            LogUtils.debug((String)"copy properties error:", (Object[])new Object[]{e});
            return null;
        }
    }

    DingerType dingerType(Method method) {
        Class<?> dingerClass = method.getDeclaringClass();
        if (dingerClass.isAnnotationPresent(Dinger.class)) {
            return dingerClass.getAnnotation(Dinger.class).value();
        }
        return this.dingtalkProperties.getDefaultDinger();
    }

    DingerDefinition dingerDefinition(DingerType useDinger, String dingerClassName, String keyName) {
        DingerDefinition dingerDefinition;
        DingerConfig localDinger = DingerHelper.getLocalDinger();
        if (localDinger == null) {
            String dingerName = String.valueOf((Object)useDinger) + "." + (String)keyName;
            dingerDefinition = AbstractDingerDefinitionResolver.Container.INSTANCE.get(dingerName);
            if (dingerDefinition == null) {
                return null;
            }
            DingerConfig dingerMethodDefaultDingerConfig = dingerDefinition.dingerConfig();
            if (DingerMessageHandler.multiDinger()) {
                MultiDingerConfig multiDingerConfig = MultiDingerConfigContainer.INSTANCE.get(useDinger, dingerClassName);
                DingerConfig dingerConfig = null;
                if (multiDingerConfig != null) {
                    dingerConfig = multiDingerConfig.getAlgorithmHandler().dingerConfig(multiDingerConfig.getDingerConfigs(), dingerMethodDefaultDingerConfig);
                }
                if (dingerConfig == null) {
                    dingerConfig = dingerMethodDefaultDingerConfig;
                }
                DingerHelper.assignDinger(dingerConfig);
            } else {
                DingerHelper.assignDinger(dingerMethodDefaultDingerConfig);
            }
        } else {
            DingerType dingerType = localDinger.getDingerType();
            if (dingerType == null) {
                dingerType = useDinger;
            }
            if ((dingerDefinition = AbstractDingerDefinitionResolver.Container.INSTANCE.get((String)(keyName = String.valueOf((Object)dingerType) + "." + (String)keyName))) == null) {
                return null;
            }
        }
        return dingerDefinition;
    }
}

