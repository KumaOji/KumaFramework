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

import com.kuma.boot.dingtalk.annatations.DingerClose;
import com.kuma.boot.dingtalk.entity.DingerResponse;
import com.kuma.boot.dingtalk.entity.MsgType;
import com.kuma.boot.dingtalk.enums.DingerResponseCodeEnum;
import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.session.SessionConfiguration;

import java.lang.reflect.Method;
import java.util.Map;

import static com.kuma.boot.dingtalk.constant.DingerConstant.SPOT_SEPERATOR;

/**
 * Dinger Handle Proxy
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:22:51
 */
public class DingerHandleProxy extends DingerInvocationHandler {

    public DingerHandleProxy(SessionConfiguration sessionConfiguration) {
        this.dingerRobot = sessionConfiguration.getDingerRobot();
        this.dingtalkProperties = sessionConfiguration.getDingerProperties();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> dingerClass = method.getDeclaringClass();

        final String methodName = method.getName();

        if (ignoreMethodMap.containsKey(methodName)) {
            return ignoreMethodMap.get(methodName).execute(this, args);
        }

        if (dingerClass.isAnnotationPresent(DingerClose.class)) {
            return null;
        }

        if (method.isAnnotationPresent(DingerClose.class)) {
            return null;
        }

        final String dingerClassName = dingerClass.getName();
        String keyName = dingerClassName + SPOT_SEPERATOR + methodName;
        try {
            DingerType useDinger = dingerType(method);
            DingerDefinition dingerDefinition = dingerDefinition(useDinger, dingerClassName, keyName);

            DingerResponse dingerResponse;

            if (dingerDefinition == null) {
                dingerResponse = DingerResponse.failed(
                        DingerResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED,
                        String.format("method %s does not support dinger %s.", keyName, useDinger));
            } else {
                // method params map
                Map<String, Object> params = paramsHandler(method, dingerDefinition, args);

                MsgType message = transfer(dingerDefinition, params);

                dingerResponse = dingerRobot.send(message);
            }

            // return...
            return resultHandler(method.getReturnType(), dingerResponse);
        } finally {
            DingerHelper.clearDinger();
        }
    }
}
