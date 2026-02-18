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
        return new MapPropertySource("ttc_boot_info", getVersionsMap(sourceClass, environment));
    }

    public static MapPropertySource getUrlSource() {
        return new MapPropertySource("ttc_website_info", getUrlMap());
    }

    public static Map<String, Object> getUrlMap() {
        Map<String, Object> versions = new HashMap<>();
        versions.put("ttc.website.url", "https://kumacloud.top");
        versions.put("ttc.website.initializr.url", "https://start.kumacloud.top");
        versions.put("ttc.website.blog.url", "https://blog.kumacloud.top");
        versions.put("ttc.website.m.url", "https://m.kumacloud.top");
        versions.put("ttc.website.datav.url", "https://datav.kumacloud.top");
        versions.put("ttc.website.manager.url", "https://manager.kumacloud.top");
        versions.put("ttc.website.merchant.url", "https://merchant.kumacloud.top");
        versions.put("ttc.website.open.url", "https://open.kumacloud.top");
        versions.put("ttc.website.backend.url", "https://backend.kumacloud.top");
        versions.put("ttc.website.gitee.url", "https://gitee.com/dtbox/kuma-cloud-project");
        versions.put("ttc.website.github.url", "https://github.com/kuma/kuma-cloud-project");
        return versions;
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

        versions.put("ttc-boot.version",
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
