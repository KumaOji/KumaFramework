/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.dingtalk.entity.MsgType;
import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.enums.MessageMainType;
import com.kuma.boot.dingtalk.enums.MessageSubType;

public class DefaultDingerDefinition
implements DingerDefinition {
    private String dingerName;
    private Class<? extends DingerDefinitionGenerator> dingerDefinitionGenerator;
    private MsgType message;
    private DingerConfig dingerConfig;
    private DingerType dingerType;
    private MessageMainType messageMainType;
    private MessageSubType messageSubType;
    private String[] methodParams;
    private int[] genericIndex;

    @Override
    public String dingerName() {
        return this.dingerName;
    }

    @Override
    public void setDingerName(String dingerName) {
        this.dingerName = dingerName;
    }

    @Override
    public Class<? extends DingerDefinitionGenerator> dingerDefinitionGenerator() {
        return this.dingerDefinitionGenerator;
    }

    @Override
    public void setDingerDefinitionGenerator(Class<? extends DingerDefinitionGenerator> dingerDefinitionGenerator) {
        this.dingerDefinitionGenerator = dingerDefinitionGenerator;
    }

    @Override
    public MsgType message() {
        return this.message;
    }

    @Override
    public void setMessage(MsgType message) {
        this.message = message;
    }

    @Override
    public DingerConfig dingerConfig() {
        return this.dingerConfig;
    }

    @Override
    public void setDingerConfig(DingerConfig dingerConfig) {
        this.dingerConfig = dingerConfig;
    }

    @Override
    public DingerType dingerType() {
        return this.dingerType;
    }

    @Override
    public void setDingerType(DingerType dingerType) {
        this.dingerType = dingerType;
    }

    @Override
    public MessageMainType messageMainType() {
        return this.messageMainType;
    }

    @Override
    public void setMessageMainType(MessageMainType messageMainType) {
        this.messageMainType = messageMainType;
    }

    @Override
    public MessageSubType messageSubType() {
        return this.messageSubType;
    }

    @Override
    public void setMessageSubType(MessageSubType messageSubType) {
        this.messageSubType = messageSubType;
    }

    @Override
    public String[] methodParams() {
        return this.methodParams;
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
        return this.genericIndex;
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

