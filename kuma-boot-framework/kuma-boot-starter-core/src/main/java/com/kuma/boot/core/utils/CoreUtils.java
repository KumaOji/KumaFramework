package com.kuma.boot.core.utils;

import com.kuma.boot.common.support.version.KmcVersion;
import com.kuma.boot.core.version.SpringCloudAlibabaVersion;
import com.kuma.boot.core.version.SpringCloudDependenciesVersion;
import com.kuma.boot.core.version.SpringCloudVersion;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

/**
 * CoreUtils
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class CoreUtils {

    public static MapPropertySource getVersionSource( Class<?> sourceClass, Environment environment ) {
        return new MapPropertySource("kmc_boot_info", getVersionsMap(sourceClass, environment));
    }

    public static MapPropertySource getUrlSource() {
        return new MapPropertySource("kmc_website_info", getUrlMap());
    }

    /**
     * 作者/网站链接，会写入 System Properties 供 banner/kmc-banner.txt 的 ${...} 占位符展开。
     *
     * <p><b>扩展约定（banner.txt 对齐规则）：</b>
     * <pre>
     *   格式：  &lt;label padded to 43 chars&gt;::  ${property.key}
     *   示例：  Spring              ::  ${spring.version}     (标签 6 字符 + 27 空格 = 43)
     *           SC Dependencies     ::  ${...}               (标签 15 字符 + 18 空格 = 43)
     *
     *   新增步骤：
     *     1. 在此 map 中添加  map.put("kmc.xxx.yyy", "value");
     *     2. 在 kmc-banner.txt 中添加一行：
     *        ${AnsiColor.XXX}  YourLabel           ::  ${kmc.xxx.yyy}
     *        （保证 "YourLabel + 填充空格" 恰好 43 字符，:: 落在第 22 列）
     * </pre>
     */
    public static Map<String, Object> getUrlMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("kmc.author", "Kuma");
        map.put("kmc.author.github", "https://github.com/KumaOji");
        map.put("kmc.author.gitee", "https://gitee.com/kuma047");
        map.put("kmc.author.csdn", "https://blog.csdn.net/Kuma_Test");
        map.put("kmc.author.juejin", "https://juejin.cn/user/3423240582337945");
        return map;
    }

    public static String getApplicationVersion( Environment environment ) {
        return environment.getProperty("spring.application.version");
    }

    public static Map<String, Object> getVersionsMap( Class<?> sourceClass, Environment environment ) {
        String appVersion = getApplicationVersion(environment);
        String bootVersion = SpringBootVersion.getVersion();
        Map<String, Object> versions = new HashMap<>();
        versions.put("application.version", getVersionString(appVersion, false));
        versions.put("spring-boot.version", getVersionString(bootVersion, false));
        versions.put("application.formatted-version", getVersionString(appVersion, true));
        versions.put("spring-boot.formatted-version", getVersionString(bootVersion, true));

        versions.put("kmc-boot.version",
                getVersionString(KmcVersion.getVersion(), false));
        versions.put("spring.version", getVersionString(SpringVersion.getVersion(), false));
        versions.put("spring-cloud.version",
                getVersionString(SpringCloudVersion.getVersion(), false));
        versions.put("spring-cloud-dependencies.version",
                getVersionString(SpringCloudDependenciesVersion.getVersion(), false));
        versions.put("spring-cloud-alibaba.version",
                getVersionString(SpringCloudAlibabaVersion.getVersion(), false));
        return versions;
    }


    public static String getVersionString( String version, boolean format ) {
        if (version == null) {
            return "";
        }
        return format ? " (v" + version + ")" : version;


    }

}
