package com.kuma.cloud.console;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("console")
public record ConsoleProperties(boolean openBrowser, String wslDistribution, String wslUser,
                                List<Project> projects) {
    public ConsoleProperties {
        projects = projects == null ? List.of() : List.copyOf(projects);
    }

    public record Project(String name, String url, String managementUrl) {
        @org.springframework.boot.context.properties.bind.ConstructorBinding
        public Project {}
        public Project(String name, String url) { this(name, url, null); }
        public String actuatorUrl() { return (managementUrl == null || managementUrl.isBlank() ? url : managementUrl).replaceAll("/+$", ""); }
    }
}
