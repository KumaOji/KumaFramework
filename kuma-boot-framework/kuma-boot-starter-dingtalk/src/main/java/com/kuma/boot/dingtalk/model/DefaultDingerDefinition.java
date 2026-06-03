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

import com.kuma.boot.dingtalk.entity.MsgType;
import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.enums.MessageMainType;
import com.kuma.boot.dingtalk.enums.MessageSubType;

/**
 * DefaultDingerDefinition
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:22:24
 */
public class DefaultDingerDefinition implements DingerDefinition {

    private String dingerName;
    private Class<? extends DingerDefinitionGenerator> dingerDefinitionGenerator;
    private MsgType message;
    private DingerConfig dingerConfig;
    private DingerType dingerType;
    private MessageMainType messageMainType;
    private MessageSubType messageSubType;
    /** dinger方法的参数信息 */
    private String[] methodParams;
    /** dinger方法的泛型信息 */
    private int[] genericIndex;

    @Override
    public String dingerName() {
        return dingerName;
    }

    @Override
    public void setDingerName(String dingerName) {
        this.dingerName = dingerName;
    }

    @Override
    public Class<? extends DingerDefinitionGenerator> dingerDefinitionGenerator() {
        return dingerDefinitionGenerator;
    }

    @Override
    public void setDingerDefinitionGenerator(Class<? extends DingerDefinitionGenerator> dingerDefinitionGenerator) {
        this.dingerDefinitionGenerator = dingerDefinitionGenerator;
    }

    @Override
    public MsgType message() {
        return message;
    }

    @Override
    public void setMessage(MsgType message) {
        this.message = message;
    }

    @Override
    public DingerConfig dingerConfig() {
        return dingerConfig;
    }

    @Override
    public void setDingerConfig(DingerConfig dingerConfig) {
        this.dingerConfig = dingerConfig;
    }

    @Override
    public DingerType dingerType() {
        return dingerType;
    }

    @Override
    public void setDingerType(DingerType dingerType) {
        this.dingerType = dingerType;
    }

    @Override
    public MessageMainType messageMainType() {
        return messageMainType;
    }

    @Override
    public void setMessageMainType(MessageMainType messageMainType) {
        this.messageMainType = messageMainType;
    }

    @Override
    public MessageSubType messageSubType() {
        return messageSubType;
    }

    @Override
    public void setMessageSubType(MessageSubType messageSubType) {
        this.messageSubType = messageSubType;
    }

    @Override
    public String[] methodParams() {
        return methodParams;
    }

    @Override
    public void setMethodParams(String[] methodParams) {
        if (methodParams == null) {
            this.methodParams = new String[0];
            return;
        }
        this.methodParams = methodParams;
    }

    @Override
    public int[] genericIndex() {
        return genericIndex;
    }

    @Override
    public void setGenericIndex(int[] genericIndex) {
        if (genericIndex == null) {
            this.genericIndex = new int[0];
            return;
        }
        this.genericIndex = genericIndex;
    }
}
