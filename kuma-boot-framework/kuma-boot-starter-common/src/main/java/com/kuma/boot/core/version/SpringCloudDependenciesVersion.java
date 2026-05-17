package com.kuma.boot.core.version;

public final class SpringCloudDependenciesVersion {

    private SpringCloudDependenciesVersion() {}

    public static String getVersion() {
        try {
            Class.forName("org.springframework.cloud.bootstrap.BootstrapConfiguration");
        } catch (ClassNotFoundException e) {
            return "NOT IN SPRING CLOUD DEPENDENCIES";
        }
        return "2025.1.0";
    }
}
