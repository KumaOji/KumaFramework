/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.utils.system;

import com.kuma.boot.common.constant.SystemConstants;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import java.util.Objects;
import cn.hutool.core.util.StrUtil;
import org.jspecify.annotations.Nullable;

/**
 * 系统工具类
 *
 * @author kuma
 * @version 2022.09
 * @since 2023-01-03 11:29:04
 */
public final class SystemUtils {

    /**
     * 系统跑龙套
     * @return
     * @since 2023-01-03 11:29:04
     */
    private SystemUtils() {}

    /**
     * 获取换行符号
     * @return {@link String }
     * @since 2023-01-03 11:29:04
     */
    public static String getLineSeparator() {
        return getProperty(SystemConstants.LINE_SEPARATOR);
    }

    /**
     * 获取属性信息
     * @param key 标识
     * @return {@link String }
     * @since 2023-01-03 11:29:04
     */
    public static String getProperty(final String key) {
        return System.getProperty(key);
    }

    /** linux操作系统名称 代码部署于 linux 上，工作默认为 mac 和 Windows */
    private static final String OS_NAME_LINUX = "LINUX";

    /** 用户家里 获取 user home */
    @Nullable public static final String USER_HOME = getSystemProperty("user.home");

    /** 用户dir 获取用户地址 */
    @Nullable public static final String USER_DIR = getSystemProperty("user.dir");

    /** 用户名 获取用户名 */
    @Nullable public static final String USER_NAME = getSystemProperty("user.name");

    /** 操作系统名称 os 名 */
    @Nullable public static final String OS_NAME = getSystemProperty("os.name");

    /**
     * Gets a System property, defaulting to {@code null} if the property cannot be read.
     *
     * <p>
     * If a {@code SecurityException} is caught, the return value is {@code null} and a
     * message is written to {@code System.err}.
     * @param property the system property name
     * @return {@link String }
     * @since 2023-01-03 11:29:04
     */
    @Nullable
    private static String getSystemProperty(final String property) {
        try {
            return System.getProperty(property);
        } catch (final SecurityException ex) {
            return null;
        }
    }

    /**
     * 判断是否为本地开发环境
     * @return boolean
     * @since 2023-01-03 11:29:04
     */
    public static boolean isLinux() {
        return StrUtil.isNotBlank(OS_NAME) && OS_NAME_LINUX.equalsIgnoreCase(OS_NAME);
    }

    /**
     * 代码部署于 linux 上，工作默认为 mac 和 Windows
     * @return boolean
     * @since 2023-01-03 11:29:04
     */
    public static boolean isLocalDev() {
        return !isLinux();
    }

    /**
     * 读取 System Property
     * @param key key
     * @return {@link String }
     * @since 2023-01-03 11:29:04
     */
    @Nullable
    public static String getProp(String key) {
        return System.getProperty(key);
    }

    /**
     * 读取 System Property
     * @param key key
     * @param defValue 默认值
     * @return {@link String }
     * @since 2023-01-03 11:29:04
     */
    public static String getProp(String key, String defValue) {
        return System.getProperty(key, defValue);
    }

    /**
     * 读取 System Property
     * @param key key
     * @param defaultValue defaultValue
     * @return int
     * @since 2023-01-03 11:29:04
     */
    public static int getPropToInt(String key, int defaultValue) {
        return ObjectUtils.toInt(getProp(key), defaultValue);
    }

    /**
     * 读取 System Property
     * @param key key
     * @param defaultValue defaultValue
     * @return boolean
     * @since 2023-01-03 11:29:04
     */
    public static boolean getPropToBool(String key, boolean defaultValue) {
        return Objects.requireNonNull(ObjectUtils.toBoolean(getProp(key), defaultValue));
    }

    /**
     * 读取 System Property 或者 Env
     * @param key key
     * @return {@link String }
     * @since 2023-01-03 11:29:04
     */
    @Nullable
    public static String getPropOrEnv(String key) {
        String value = System.getProperty(key);
        return value == null ? System.getenv(key) : value;
    }
}
