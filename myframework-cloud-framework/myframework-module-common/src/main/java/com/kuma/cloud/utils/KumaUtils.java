package com.kuma.cloud.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.experimental.UtilityClass;

/**
 * 简单工具类示例：演示 Hutool + Lombok 的 Gradle 依赖用法。
 * 依赖来自 gradle/libs.versions.toml（projectLibs），在 build.gradle 中通过 libs.xxx 引用。
 */
@UtilityClass
public class KumaUtils {

    /**
     * 字符串为空或 blank 时返回默认值（Hutool StrUtil）
     */
    public static String defaultIfBlank(String str, String defaultValue) {
        return StrUtil.blankToDefault(str, defaultValue);
    }

    /**
     * 对字符串做 MD5（Hutool DigestUtil）
     */
    public static String md5Hex(String input) {
        return DigestUtil.md5Hex(input);
    }
}
