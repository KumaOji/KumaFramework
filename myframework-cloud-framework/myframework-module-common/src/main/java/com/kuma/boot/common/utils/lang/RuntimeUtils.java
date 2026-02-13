/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.lang;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.number.NumberUtils;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class RuntimeUtils {
    private static volatile int pId = -1;
    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();

    public static int getPId() {
        if (pId > 0) {
            return pId;
        }
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        int index = jvmName.indexOf("@");
        if (index > 0) {
            pId = NumberUtils.toInt(jvmName.substring(0, index), -1);
            return pId;
        }
        return pId;
    }

    public static Instant getStartTime() {
        return Instant.ofEpochMilli(ManagementFactory.getRuntimeMXBean().getStartTime());
    }

    public static Duration getUpTime() {
        return Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime());
    }

    public static String getJvmArguments() {
        List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        return StringUtils.join(vmArguments, " ");
    }

    public static int getCpuNum() {
        return CPU_NUM;
    }
}

