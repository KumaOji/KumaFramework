/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.listeners;

import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.model.DingerConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DingerListenersProperty {
    protected static List<Class<?>> dingerClasses = new ArrayList();
    protected static Map<DingerType, DingerConfig> defaultDingerConfigs = new HashMap<DingerType, DingerConfig>();
    protected static final List<DingerType> enabledDingerTypes = Arrays.stream(DingerType.values()).filter(e -> e.isEnabled()).toList();

    protected static List<Class<?>> dingerClasses() {
        return dingerClasses;
    }

    protected static void emptyDingerClasses() {
        if (dingerClasses != null && !dingerClasses.isEmpty()) {
            dingerClasses.clear();
        }
    }

    protected static void clear() {
        dingerClasses.clear();
        defaultDingerConfigs.clear();
    }
}

