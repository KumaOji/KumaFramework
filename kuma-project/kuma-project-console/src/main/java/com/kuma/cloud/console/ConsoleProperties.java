package com.kuma.cloud.console;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("console")
public record ConsoleProperties(boolean openBrowser, String wslDistribution, String wslUser,
                                List<Project> projects) {
    public ConsoleProperties {
        projects = projects == null ? List.of() : List.copyOf(projects);
    }

    public record Project(String name, String url) {}
}
