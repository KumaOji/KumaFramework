/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.cloud.stream.framework.trigger.util;

import com.kuma.cloud.stream.framework.trigger.enums.DelayTypeEnums;

public class DelayQueueTools {
    private static final String PREFIX = "{rocketmq_trigger}_";

    public static String wrapperUniqueKey(DelayTypeEnums type, String id) {
        return "{TIME_TRIGGER_" + type.name() + "}_" + id;
    }

    public static String generateKey(String executorName, Long triggerTime, String uniqueKey) {
        return PREFIX + (executorName + triggerTime + uniqueKey).hashCode();
    }
}

