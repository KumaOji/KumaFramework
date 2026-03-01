/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.dingtalk.entity.MsgType;
import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.enums.MessageMainType;
import com.kuma.boot.dingtalk.enums.MessageSubType;

public interface DingerDefinition {
    public String dingerName();

    public void setDingerName(String var1);

    public Class<? extends DingerDefinitionGenerator> dingerDefinitionGenerator();

    public void setDingerDefinitionGenerator(Class<? extends DingerDefinitionGenerator> var1);

    public MsgType message();

    public void setMessage(MsgType var1);

    public DingerConfig dingerConfig();

    public void setDingerConfig(DingerConfig var1);

    public DingerType dingerType();

    public void setDingerType(DingerType var1);

    public MessageMainType messageMainType();

    public void setMessageMainType(MessageMainType var1);

    public MessageSubType messageSubType();

    public void setMessageSubType(MessageSubType var1);

    public String[] methodParams();

    public void setMethodParams(String[] var1);

    public int[] genericIndex();

    public void setGenericIndex(int[] var1);
}

