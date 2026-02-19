/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.github.xiaoymin.knife4j.core.util.StrUtil
 *  org.springframework.core.env.Environment
 */
package com.kuma.boot.springdoc.knife4j.spring.util;

import com.github.xiaoymin.knife4j.core.util.StrUtil;
import java.util.Objects;
import org.springframework.core.env.Environment;

public class EnvironmentUtils {
    public static String resolveContextPath(Environment environment) {
        String contextPath = "";
        String v1BasePath = environment.getProperty("server.context-path");
        String basePath = environment.getProperty("server.servlet.context-path");
        if (StrUtil.isNotBlank((CharSequence)v1BasePath) && !"/".equals(v1BasePath)) {
            contextPath = v1BasePath;
        } else if (StrUtil.isNotBlank((CharSequence)basePath) && !"/".equals(basePath)) {
            contextPath = basePath;
        }
        return contextPath;
    }

    public static String resolveString(Environment environment, String key, String defaultValue) {
        String envValue;
        if (environment != null && StrUtil.isNotBlank((CharSequence)(envValue = environment.getProperty(key)))) {
            return envValue;
        }
        return defaultValue;
    }

    public static Integer resolveInt(Environment environment, String key, Integer defaultValue) {
        if (environment != null) {
            return Integer.parseInt(Objects.toString(environment.getProperty(key, String.valueOf(defaultValue)), String.valueOf(defaultValue)));
        }
        return defaultValue;
    }

    public static Boolean resolveBool(Environment environment, String key, Boolean defaultValue) {
        if (environment != null) {
            return Boolean.valueOf(Objects.toString(environment.getProperty(key, defaultValue.toString()), defaultValue.toString()));
        }
        return defaultValue;
    }
}

