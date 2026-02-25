/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitsnowjean.monitor.service;

import com.kuma.boot.ratelimit.ratelimitsnowjean.monitor.entity.MonitorBean;
import java.util.List;

public interface MonitorService {
    public static final String PRE = "$PRE$";
    public static final String AFTER = "$AFTER$";
    public static final String DATE = "$DATA$";

    public static String getMonitorPreKey(MonitorBean m) {
        return PRE + m.getApp() + m.getId() + DATE + m.getDateTime();
    }

    public static String getMonitorAfterKey(MonitorBean m) {
        return AFTER + m.getApp() + m.getId() + DATE + m.getDateTime();
    }

    public static String getMonitorPreKeys(String app, String id) {
        return PRE + app + id + "*";
    }

    public static String getMonitorAfterKeys(String app, String id) {
        return AFTER + app + id + "*";
    }

    public void save(List<MonitorBean> var1);

    public List<MonitorBean> getAll(String var1, String var2);

    public boolean clean(String var1, String var2);
}

