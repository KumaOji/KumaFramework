/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.pie;

import com.kuma.boot.common.support.pie.ChannelPipeline;

public interface Channel {
    public Channel process(Object var1, Object var2);

    public ChannelPipeline pipeline();

    public static interface ChannelProcessor {
        public void doProcess(Object var1, Object var2);
    }
}

