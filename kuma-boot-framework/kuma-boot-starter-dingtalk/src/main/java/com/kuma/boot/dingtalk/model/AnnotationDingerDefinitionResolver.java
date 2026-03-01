/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.annatations.DingerImageText;
import com.kuma.boot.dingtalk.annatations.DingerLink;
import com.kuma.boot.dingtalk.annatations.DingerMarkdown;
import com.kuma.boot.dingtalk.annatations.DingerText;
import com.kuma.boot.dingtalk.entity.DingerMethod;
import com.kuma.boot.dingtalk.enums.ExceptionEnum;
import com.kuma.boot.dingtalk.enums.MessageMainType;
import com.kuma.boot.dingtalk.enums.MessageSubType;
import com.kuma.boot.dingtalk.exception.DingerException;
import com.kuma.boot.dingtalk.utils.DingerUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class AnnotationDingerDefinitionResolver
extends AbstractDingerDefinitionResolver<List<Class<?>>> {
    @Override
    public void resolver(List<Class<?>> dingerClasses) {
        for (Class<?> dingerClass : dingerClasses) {
            Method[] methods;
            DingerConfig dingerConfiguration = this.dingerConfiguration(dingerClass);
            String namespace = dingerClass.getName();
            for (Method method : methods = dingerClass.getMethods()) {
                MessageSubType messageSubType;
                Annotation source;
                String dingerName = namespace + "." + method.getName();
                String dingerDefinitionKey = String.valueOf((Object)MessageMainType.ANNOTATION) + ".";
                int[] paramTypes = null;
                if (method.isAnnotationPresent(DingerText.class)) {
                    source = method.getAnnotation(DingerText.class);
                    messageSubType = MessageSubType.TEXT;
                } else if (method.isAnnotationPresent(DingerMarkdown.class)) {
                    source = method.getAnnotation(DingerMarkdown.class);
                    messageSubType = MessageSubType.MARKDOWN;
                } else if (method.isAnnotationPresent(DingerImageText.class)) {
                    paramTypes = DingerUtils.methodParamsGenericType(method, DingerImageText.clazz);
                    if (paramTypes.length != 1) {
                        throw new DingerException(ExceptionEnum.IMAGETEXT_METHOD_PARAM_EXCEPTION, dingerName);
                    }
                    source = method.getAnnotation(DingerImageText.class);
                    messageSubType = MessageSubType.IMAGETEXT;
                } else if (method.isAnnotationPresent(DingerLink.class)) {
                    paramTypes = DingerUtils.methodParamsType(method, DingerLink.clazz);
                    if (paramTypes.length != 1) {
                        throw new DingerException(ExceptionEnum.LINK_METHOD_PARAM_EXCEPTION, dingerName);
                    }
                    source = method.getAnnotation(DingerLink.class);
                    messageSubType = MessageSubType.LINK;
                } else {
                    LogUtils.debug((String)"register annotation dingerDefinition and skip method={}(possible use xml definition).", (Object[])new Object[]{dingerName});
                    continue;
                }
                this.registerDingerDefinition(dingerName, source, dingerDefinitionKey + String.valueOf((Object)messageSubType), dingerConfiguration, new DingerMethod(dingerName, this.parameterNameDiscoverer.getParameterNames(method), paramTypes));
            }
        }
    }
}

