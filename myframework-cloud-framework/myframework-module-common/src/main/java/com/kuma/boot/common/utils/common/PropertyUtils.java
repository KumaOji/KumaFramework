/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.model.Callable;
import com.kuma.boot.common.model.PropertyCache;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.convert.ConvertUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.File;
import java.util.Map;
import java.util.Objects;

public final class PropertyUtils {
    public static String NULL = "<?NULL?>";

    public static void eachProperty(Callable.Action3<String, String, Object> call) {
        for (String string : System.getProperties().stringPropertyNames()) {
            call.invoke("properties", string, System.getProperty(string));
        }
        for (Map.Entry entry : System.getenv().entrySet()) {
            call.invoke("env", (String)entry.getKey(), entry.getValue());
        }
    }

    public static <T> T getProperty(String key, T defaultValue) {
        String value = PropertyUtils.getProperty(key);
        if (value == null) {
            value = System.getenv(key);
        }
        if (value == null && ContextUtils.getApplicationContext() != null) {
            value = ContextUtils.getApplicationContext().getEnvironment().getProperty(key);
        }
        if (value == null) {
            return defaultValue;
        }
        return (T)ConvertUtils.convert((Object)value, defaultValue.getClass());
    }

    public static String getProperty(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = System.getenv(key);
        }
        if (value == null && ContextUtils.getApplicationContext() != null) {
            value = ContextUtils.getApplicationContext().getEnvironment().getProperty(key);
        }
        return value;
    }

    public static <T> T getEnvProperty(String key, T defaultvalue) {
        String value = System.getenv(key);
        if (value == null) {
            return defaultvalue;
        }
        return (T)ConvertUtils.convert((Object)value, defaultvalue.getClass());
    }

    public static <T> T getSystemProperty(String key, T defaultvalue) {
        String value = System.getProperty(key);
        if (value == null) {
            return defaultvalue;
        }
        return (T)ConvertUtils.convert((Object)value, defaultvalue.getClass());
    }

    public static void setDefaultInitProperty(Class<?> cls, String module, String key, String propertyValue) {
        PropertyUtils.setDefaultInitProperty(cls, module, key, propertyValue, "");
    }

    public static void setDefaultInitProperty(Class<?> cls, String module, String key, String propertyValue, String message) {
        if (StringUtils.isEmpty(PropertyUtils.getPropertyCache(key, ""))) {
            if (!StringUtils.isEmpty(propertyValue)) {
                System.setProperty(key, propertyValue);
                PropertyCache propertyCache = ContextUtils.getBean(PropertyCache.class, false);
                if (Objects.nonNull(propertyCache)) {
                    propertyCache.tryUpdateCache(key, propertyValue);
                }
                LogUtils.debug(" set default init property key: {}, value: {}, message: {}", key, propertyValue, message);
            }
        } else if (StringUtils.isEmpty(PropertyUtils.getSystemProperty(key, ""))) {
            System.setProperty(key, Objects.requireNonNull(PropertyUtils.getPropertyCache(key, "")));
        }
    }

    public static void setProperty(String key, String propertyValue, String message) {
        System.setProperty(key, propertyValue);
        LogUtils.debug(" set default init property key: {}, value: {}, message: {}", key, propertyValue, message);
    }

    public static <T> T getPropertyCache(String key, T defaultValue) {
        PropertyCache propertyCache = ContextUtils.getBean(PropertyCache.class, false);
        if (Objects.nonNull(propertyCache)) {
            return propertyCache.get(key, defaultValue);
        }
        return null;
    }

    public static void setDefaultProperty(String applicationName) {
        String userHome = System.getProperty("user.home");
        System.setProperty("JM.LOG.PATH", userHome + File.separator + "logs" + File.separator + applicationName);
        System.setProperty("JM.SNAPSHOT.PATH", userHome + File.separator + "logs" + File.separator + applicationName);
        System.setProperty("nacos.logging.default.config.enabled", "true");
        System.setProperty("arthas.output.dir", userHome + File.separator + "logs" + File.separator + applicationName + File.separator + "arthas-output");
        System.setProperty("arthas.logging.file.path", userHome + File.separator + "logs" + File.separator + applicationName + File.separator + "arthas");
        System.setProperty("arthas.outputPath", userHome + File.separator + "logs" + File.separator + applicationName + File.separator + "arthas-output");
        System.setProperty("rocketmq.client.logUseSlf4j", "true");
        System.setProperty("rocketmq.client.logRoot", userHome + File.separator + "logs" + File.separator + applicationName + File.separator + "rocketmqlogs");
        System.setProperty("rocketmq.client.logFileMaxIndex", "200");
        System.setProperty("rocketmq.client.logFileMaxSize", "67108864");
        System.setProperty("rocketmq.client.logLevel", "WARN");
        System.setProperty("rocketmq.log.root", userHome + File.separator + "logs" + File.separator + applicationName + File.separator + "rocketmqlogs");
        System.setProperty("csp.sentinel.log.dir", userHome + File.separator + "logs" + File.separator + applicationName + File.separator + "csp");
        System.setProperty("csp.sentinel.log.output.type", "file");
        System.setProperty("EAGLEEYE.LOG.PATH", userHome + File.separator + "logs" + File.separator + applicationName + File.separator + "eagleeye");
        System.setProperty("polaris.log.home", userHome + File.separator + "logs" + File.separator + applicationName + File.separator + "polaris" + File.separator + "logs");
        System.setProperty("portfile", userHome + File.separator + "logs" + File.separator + applicationName + File.separator + applicationName + ".port");
    }
}

