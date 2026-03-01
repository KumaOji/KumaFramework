/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.multi;

import com.kuma.boot.dingtalk.model.DingerConfig;
import com.kuma.boot.dingtalk.utils.RandomUtils;
import java.util.List;

public class RandomHandler
implements AlgorithmHandler {
    @Override
    public DingerConfig handler(List<DingerConfig> dingerConfigs, DingerConfig defaultDingerConfig) {
        int size = dingerConfigs.size();
        int index = RandomUtils.nextInt(size);
        return dingerConfigs.get(index);
    }
}

