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

    public static Map<String, Object> getUrlMap() {
        Map<String, Object> versions = new HashMap<>();
        versions.put("kmc.website.url", "NULL");
        versions.put("kmc.website.initializr.url", "NULL");
        versions.put("kmc.website.blog.url", "NULL");
        versions.put("kmc.website.m.url", "NULL");
        versions.put("kmc.website.datav.url", "NULL");
        versions.put("kmc.website.manager.url", "NULL");
        versions.put("kmc.website.merchant.url", "NULL");
        versions.put("kmc.website.open.url", "NULL");
        versions.put("kmc.website.backend.url", "NULL");
        versions.put("kmc.website.gitee.url", "NULL");
        versions.put("kmc.website.github.url", "NULL");
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
