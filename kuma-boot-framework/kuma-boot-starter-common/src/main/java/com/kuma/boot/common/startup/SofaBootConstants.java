package com.kuma.boot.common.startup;

public class SofaBootConstants {

    public static final String APP_NAME_KEY = "spring.application.name";

    public static final String STARTUP_LOG_EXTRA_INFO = "startupLogExtraInfo";

    public static final String SOFA_DEFAULT_PROPERTY_SOURCE = "sofaConfigurationProperties";

    public static final String SOFA_EXCLUDE_AUTO_CONFIGURATION_PROPERTY_SOURCE =
            "sofaExcludeAutoConfigurationProperties";

    public static final String SOFA_BOOTSTRAP = "sofaBootstrap";

    public static final String SPRING_CLOUD_BOOTSTRAP = "bootstrap";

    public static final String SOFA_HIGH_PRIORITY_CONFIG = "sofaHighPriorityConfig";

    public static final String SOFA_BOOT_SPACE_NAME = "sofa-boot";

    public static final String SOFA_BOOT_VERSION = "sofa-boot.version";

    public static final String SOFA_BOOT_FORMATTED_VERSION = "sofa-boot.formatted-version";

    public static final String SOFA_BOOT_SCENES_FILE_DIR = "sofa-boot/scenes";

    public static final String ENDPOINTS_WEB_EXPOSURE_INCLUDE_CONFIG =
            "management.endpoints.web.exposure.include";

    public static final String SOFA_DEFAULT_ENDPOINTS_WEB_EXPOSURE_VALUE =
            "info,health,readiness,startup,beans,components,rpc";

    public static final int CPU_CORE = Runtime.getRuntime().availableProcessors();
}
