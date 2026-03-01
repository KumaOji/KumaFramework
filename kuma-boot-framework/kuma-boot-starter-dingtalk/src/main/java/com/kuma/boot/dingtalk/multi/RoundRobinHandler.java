/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.dingtalk.multi;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.model.DingerConfig;

import java.util.List;

public class RoundRobinHandler
implements AlgorithmHandler {
    private volatile int index = 0;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DingerConfig handler(List<DingerConfig> dingerConfigs, DingerConfig defaultDingerConfig) {
        int size = dingerConfigs.size();
        int idx = this.index++;
        RoundRobinHandler roundRobinHandler = this;
        synchronized (roundRobinHandler) {
            this.index = this.index >= size ? 0 : this.index;
            LogUtils.debug((String)"#{}# \u5f53\u524d\u4f7f\u7528\u7b2c{}\u4e2a\u673a\u5668\u4eba", (Object[])new Object[]{this.algorithmId(), idx});
        }
        return dingerConfigs.get(idx);
    }
}

