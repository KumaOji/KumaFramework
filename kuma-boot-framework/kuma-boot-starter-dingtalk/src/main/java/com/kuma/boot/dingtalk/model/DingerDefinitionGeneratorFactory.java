/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.enums.ExceptionEnum;
import com.kuma.boot.dingtalk.exception.DingerException;

import java.util.HashMap;
import java.util.Map;

public class DingerDefinitionGeneratorFactory {
    static final Map<String, DingerDefinitionGenerator> dingTalkDefinitionGeneratorMap = new HashMap<String, DingerDefinitionGenerator>();

    public static DingerDefinitionGenerator get(String key) {
        DingerDefinitionGenerator dingTalkDefinitionGenerator = dingTalkDefinitionGeneratorMap.get(key);
        if (dingTalkDefinitionGenerator == null) {
            LogUtils.debug((String)"key={}, dingTalkDefinitionGeneratorMap={}.", (Object[])new Object[]{key, dingTalkDefinitionGeneratorMap.keySet()});
            throw new DingerException(ExceptionEnum.DINGERDEFINITION_ERROR, key);
        }
        return dingTalkDefinitionGenerator;
    }
}

