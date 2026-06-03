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

package com.kuma.boot.dingtalk.model;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.annatations.DingerImageText;
import com.kuma.boot.dingtalk.annatations.DingerLink;
import com.kuma.boot.dingtalk.annatations.DingerMarkdown;
import com.kuma.boot.dingtalk.annatations.DingerText;
import com.kuma.boot.dingtalk.entity.DingerMethod;
import com.kuma.boot.dingtalk.enums.MessageMainType;
import com.kuma.boot.dingtalk.enums.MessageSubType;
import com.kuma.boot.dingtalk.exception.DingerException;

import java.lang.reflect.Method;
import java.util.List;

import static com.kuma.boot.dingtalk.constant.DingerConstant.SPOT_SEPERATOR;
import static com.kuma.boot.dingtalk.enums.ExceptionEnum.IMAGETEXT_METHOD_PARAM_EXCEPTION;
import static com.kuma.boot.dingtalk.enums.ExceptionEnum.LINK_METHOD_PARAM_EXCEPTION;
import static com.kuma.boot.dingtalk.utils.DingerUtils.methodParamsGenericType;
import static com.kuma.boot.dingtalk.utils.DingerUtils.methodParamsType;

/**
 * dinger定义-注解方式
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:22:09
 */
public class AnnotationDingerDefinitionResolver extends AbstractDingerDefinitionResolver<List<Class<?>>> {

    @Override
    public void resolver(List<Class<?>> dingerClasses) {
        for (Class<?> dingerClass : dingerClasses) {
            // dinger 层钉钉机器人配置
            DingerConfig dingerConfiguration = dingerConfiguration(dingerClass);

            String namespace = dingerClass.getName();
            Method[] methods = dingerClass.getMethods();
            for (Method method : methods) {
                String dingerName = namespace + SPOT_SEPERATOR + method.getName();
                String dingerDefinitionKey = MessageMainType.ANNOTATION + SPOT_SEPERATOR;

                Object source;
                MessageSubType messageSubType;
                int[] paramTypes = null;
                if (method.isAnnotationPresent(DingerText.class)) {
                    source = method.getAnnotation(DingerText.class);
                    messageSubType = MessageSubType.TEXT;
                } else if (method.isAnnotationPresent(DingerMarkdown.class)) {
                    source = method.getAnnotation(DingerMarkdown.class);
                    messageSubType = MessageSubType.MARKDOWN;
                } else if (method.isAnnotationPresent(DingerImageText.class)) {
                    paramTypes = methodParamsGenericType(method, DingerImageText.clazz);
                    if (paramTypes.length != 1) {
                        throw new DingerException(IMAGETEXT_METHOD_PARAM_EXCEPTION, dingerName);
                    }
                    source = method.getAnnotation(DingerImageText.class);
                    messageSubType = MessageSubType.IMAGETEXT;
                } else if (method.isAnnotationPresent(DingerLink.class)) {
                    paramTypes = methodParamsType(method, DingerLink.clazz);
                    if (paramTypes.length != 1) {
                        throw new DingerException(LINK_METHOD_PARAM_EXCEPTION, dingerName);
                    }
                    source = method.getAnnotation(DingerLink.class);
                    messageSubType = MessageSubType.LINK;
                } else {
                    LogUtils.debug(
                            "register annotation dingerDefinition and skip method={}(possible use"
                                    + " xml definition).",
                            dingerName);
                    continue;
                }

                registerDingerDefinition(
                        dingerName,
                        source,
                        dingerDefinitionKey + messageSubType,
                        dingerConfiguration,
                        new DingerMethod(dingerName, parameterNameDiscoverer.getParameterNames(method), paramTypes));
            }
        }
    }
}
