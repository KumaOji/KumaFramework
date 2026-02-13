/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  org.jspecify.annotations.Nullable
 */
package com.kuma.boot.common.utils.system;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class SystemUtils {
    private static final String OS_NAME_LINUX = "LINUX";
    public static final @Nullable String USER_HOME = SystemUtils.getSystemProperty("user.home");
    public static final @Nullable String USER_DIR = SystemUtils.getSystemProperty("user.dir");
    public static final @Nullable String USER_NAME = SystemUtils.getSystemProperty("user.name");
    public static final @Nullable String OS_NAME = SystemUtils.getSystemProperty("os.name");

    private SystemUtils() {
    }

    public static String getLineSeparator() {
        return SystemUtils.getProperty("line.separator");
    }

    public static String getProperty(String key) {
        return System.getProperty(key);
    }

    private static @Nullable String getSystemProperty(String property) {
        try {
            return System.getProperty(property);
        }
        catch (SecurityException ex) {
            return null;
        }
    }

    public static boolean isLinux() {
        return StrUtil.isNotBlank((CharSequence)OS_NAME) && OS_NAME_LINUX.equalsIgnoreCase(OS_NAME);
    }

    public static boolean isLocalDev() {
        return !SystemUtils.isLinux();
    }

    public static @Nullable String getProp(String key) {
        return System.getProperty(key);
    }

    public static String getProp(String key, String defValue) {
        return System.getProperty(key, defValue);
    }

    public static int getPropToInt(String key, int defaultValue) {
        return ObjectUtils.toInt(SystemUtils.getProp(key), defaultValue);
    }

    public static boolean getPropToBool(String key, boolean defaultValue) {
        return Objects.requireNonNull(ObjectUtils.toBoolean(SystemUtils.getProp(key), defaultValue));
    }

    public static @Nullable String getPropOrEnv(String key) {
        String value = System.getProperty(key);
        return value == null ? System.getenv(key) : value;
    }
}

