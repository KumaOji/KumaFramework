/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.dingtalk.annatations.DingerClose;
import com.kuma.boot.dingtalk.entity.DingerResponse;
import com.kuma.boot.dingtalk.entity.MsgType;
import com.kuma.boot.dingtalk.enums.DingerResponseCodeEnum;
import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.enums.IgnoreMethod;
import com.kuma.boot.dingtalk.session.SessionConfiguration;
import java.lang.reflect.Method;
import java.util.Map;

public class DingerHandleProxy
extends DingerInvocationHandler {
    public DingerHandleProxy(SessionConfiguration sessionConfiguration) {
        this.dingerRobot = sessionConfiguration.getDingerRobot();
        this.dingtalkProperties = sessionConfiguration.getDingerProperties();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> dingerClass = method.getDeclaringClass();
        String methodName = method.getName();
        if (ignoreMethodMap.containsKey(methodName)) {
            return ((IgnoreMethod)((Object)ignoreMethodMap.get(methodName))).execute(this, args);
        }
        if (dingerClass.isAnnotationPresent(DingerClose.class)) {
            return null;
        }
        if (method.isAnnotationPresent(DingerClose.class)) {
            return null;
        }
        String dingerClassName = dingerClass.getName();
        String keyName = dingerClassName + "." + methodName;
        try {
            DingerResponse dingerResponse;
            DingerType useDinger = this.dingerType(method);
            DingerDefinition dingerDefinition = this.dingerDefinition(useDinger, dingerClassName, keyName);
            if (dingerDefinition == null) {
                dingerResponse = DingerResponse.failed(DingerResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED, String.format("method %s does not support dinger %s.", new Object[]{keyName, useDinger}));
            } else {
                Map<String, Object> params = this.paramsHandler(method, dingerDefinition, args);
                MsgType message = this.transfer(dingerDefinition, params);
                dingerResponse = this.dingerRobot.send(message);
            }
            Object object = this.resultHandler(method.getReturnType(), dingerResponse);
            return object;
        }
        finally {
            DingerHelper.clearDinger();
        }
    }
}

