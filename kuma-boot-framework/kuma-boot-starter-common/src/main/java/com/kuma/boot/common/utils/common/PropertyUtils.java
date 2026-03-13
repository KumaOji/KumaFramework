/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.utils.common;

import static com.kuma.boot.common.utils.convert.ConvertUtils.convert;

import com.kuma.boot.common.model.Callable;
import com.kuma.boot.common.model.PropertyCache;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.File;
import java.util.Map;
import java.util.Objects;

/**
 * PropertyUtil
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:51:35
 */
public final class PropertyUtils {

    /**
     * NULL
     */
    public static final String NULL = "<?NULL?>";

    /**
     * eachProperty
     * @param call call
     * @since 2021-09-02 20:51:44
     */
    public static void eachProperty(Callable.Action3<String, String, Object> call) {
        for (String key : System.getProperties().stringPropertyNames()) {
            call.invoke("properties", key, System.getProperty(key));
        }

        for (Map.Entry<String, String> kv : System.getenv().entrySet()) {
            call.invoke("env", kv.getKey(), kv.getValue());
        }
    }

    /**
     * getProperty
     * @param key key
     * @param defaultValue defaultValue
     * @param <T> T
     * @return T
     * @since 2021-09-02 20:51:48
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProperty(String key, T defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            value = System.getenv(key);
        }
        if (value == null && ContextUtils.getApplicationContext() != null) {
            value = ContextUtils.getApplicationContext().getEnvironment().getProperty(key);
        }
        if (value == null) {
            return defaultValue;
        }
        return (T) convert(value, defaultValue.getClass());
    }

    /**
     * getProperty
     * @param key key
     * @return {@link String }
     * @since 2021-09-02 20:51:57
     */
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

    /**
     * getEnvProperty
     * @param key key
     * @param defaultvalue defaultvalue
     * @param <T> T
     * @return T
     * @since 2021-09-02 20:52:00
     */
    @SuppressWarnings("unchecked")
    public static <T> T getEnvProperty(String key, T defaultvalue) {
        String value = System.getenv(key);
        if (value == null) {
            return defaultvalue;
        } else {
            return (T) convert(value, defaultvalue.getClass());
        }
    }

    /**
     * getSystemProperty
     * @param key key
     * @param defaultvalue defaultvalue
     * @param <T> T
     * @return T
     * @since 2021-09-02 20:52:08
     */
    @SuppressWarnings("unchecked")
    public static <T> T getSystemProperty(String key, T defaultvalue) {
        String value = System.getProperty(key);
        if (value == null) {
            return defaultvalue;
        } else {
            return (T) convert(value, defaultvalue.getClass());
        }
    }

    /**
     * setDefaultInitProperty
     * @param cls cls
     * @param module module
     * @param key key
     * @param propertyValue propertyValue
     * @since 2021-09-02 20:52:16
     */
    public static void setDefaultInitProperty(
            Class<?> cls, String module, String key, String propertyValue) {
        setDefaultInitProperty(cls, module, key, propertyValue, "");
    }

    /**
     * setDefaultInitProperty
     * @param cls cls
     * @param module module
     * @param key key
     * @param propertyValue propertyValue
     * @param message message
     * @since 2021-09-02 20:52:19
     */
    public static void setDefaultInitProperty(
            Class<?> cls, String module, String key, String propertyValue, String message) {
        if (StringUtils.isEmpty(PropertyUtils.getPropertyCache(key, ""))) {
            if (!StringUtils.isEmpty(propertyValue)) {
                System.setProperty(key, propertyValue);
                PropertyCache propertyCache = ContextUtils.getBean(PropertyCache.class, false);
                if (Objects.nonNull(propertyCache)) {
                    propertyCache.tryUpdateCache(key, propertyValue);
                }

                LogUtils.debug(
                        " set default init property key: {}, value: {}, message: {}",
                        key,
                        propertyValue,
                        message);
            }
        } else {
            if (StringUtils.isEmpty(getSystemProperty(key, ""))) {
                System.setProperty(
                        key, Objects.requireNonNull(PropertyUtils.getPropertyCache(key, "")));
            }
        }
    }

    public static void setProperty(String key, String propertyValue, String message) {
        System.setProperty(key, propertyValue);
        LogUtils.debug(
                " set default init property key: {}, value: {}, message: {}",
                key,
                propertyValue,
                message);
    }

    /**
     * getPropertyCache
     * @param key key
     * @param defaultValue defaultValue
     * @param <T> T
     * @return T
     * @since 2021-09-02 20:52:22
     */
    public static <T> T getPropertyCache(String key, T defaultValue) {
        PropertyCache propertyCache = ContextUtils.getBean(PropertyCache.class, false);
        if (Objects.nonNull(propertyCache)) {
            return propertyCache.get(key, defaultValue);
        }
        return null;
    }

    public static void setDefaultProperty(String applicationName) {
        String userHome = System.getProperty("user.home");

        /*
         * 设置nacos客户端日志和快照目录 设置nacos日志目录
         *
         * @see LocalConfigInfoProcessor
         */
        System.setProperty(
                "JM.LOG.PATH",
                userHome + File.separator + "logs" + File.separator + applicationName);
        System.setProperty(
                "JM.SNAPSHOT.PATH",
                userHome + File.separator + "logs" + File.separator + applicationName);
        System.setProperty("nacos.logging.default.config.enabled", "true");

        /* 设置arthas日志目录 */
        System.setProperty(
                "arthas.output.dir",
                userHome
                        + File.separator
                        + "logs"
                        + File.separator
                        + applicationName
                        + File.separator
                        + "arthas-output");
        System.setProperty(
                "arthas.logging.file.path",
                userHome
                        + File.separator
                        + "logs"
                        + File.separator
                        + applicationName
                        + File.separator
                        + "arthas");
        System.setProperty(
                "arthas.outputPath",
                userHome
                        + File.separator
                        + "logs"
                        + File.separator
                        + applicationName
                        + File.separator
                        + "arthas-output");

        /* 设置rocketmq4日志目录 org.apache.rocketmq.client.log.ClientLogger */
        System.setProperty("rocketmq.client.logUseSlf4j", "true");
        System.setProperty(
                "rocketmq.client.logRoot",
                userHome
                        + File.separator
                        + "logs"
                        + File.separator
                        + applicationName
                        + File.separator
                        + "rocketmqlogs");
        System.setProperty("rocketmq.client.logFileMaxIndex", "200");
        System.setProperty("rocketmq.client.logFileMaxSize", "67108864");
        // ERROR、WARN、INFO、DEBUG
        System.setProperty("rocketmq.client.logLevel", "WARN");
        /* 设置rocketmq5日志目录 rmq.client.logback.xml */
        System.setProperty(
                "rocketmq.log.root",
                userHome
                        + File.separator
                        + "logs"
                        + File.separator
                        + applicationName
                        + File.separator
                        + "rocketmqlogs");
        // System.setProperty("rocketmq.log.level", "WARN");

        // 设置metric.log / block.log / sentinel-record.log 等日志目录
        System.setProperty(
                "csp.sentinel.log.dir",
                userHome
                        + File.separator
                        + "logs"
                        + File.separator
                        + applicationName
                        + File.separator
                        + "csp");
        // file（输出到文件，默认）、console（输出到控制台）
        System.setProperty("csp.sentinel.log.output.type", "file");
        // 设置eagleeye-self.log目录
        System.setProperty(
                "EAGLEEYE.LOG.PATH",
                userHome
                        + File.separator
                        + "logs"
                        + File.separator
                        + applicationName
                        + File.separator
                        + "eagleeye");

        /* 设置polaris日志目录 */
        System.setProperty(
                "polaris.log.home",
                userHome
                        + File.separator
                        + "logs"
                        + File.separator
                        + applicationName
                        + File.separator
                        + "polaris"
                        + File.separator
                        + "logs");

        /*
         * @see WebServerPortFileWriter
         */
        System.setProperty(
                "portfile",
                userHome
                        + File.separator
                        + "logs"
                        + File.separator
                        + applicationName
                        + File.separator
                        + applicationName
                        + ".port");
    }
}
