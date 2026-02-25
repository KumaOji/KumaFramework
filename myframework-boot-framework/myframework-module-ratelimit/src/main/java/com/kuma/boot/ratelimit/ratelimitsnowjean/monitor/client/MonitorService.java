/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitsnowjean.monitor.client;

import com.kuma.boot.ratelimit.ratelimitsnowjean.monitor.entity.MonitorBean;
import java.util.List;

public interface MonitorService {
    public List<MonitorBean> getAndDelete();

    public void save(MonitorBean var1);
}

