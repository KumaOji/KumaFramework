package com.kuma.cloud.project2;

import com.kuma.boot.core.startup.StartupSpringApplication;
import com.kuma.boot.web.annotation.KumaBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@KumaBootApplication
@ComponentScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.project2"})
@ConfigurationPropertiesScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.project2"})
public class Project2Application {

    public static void main(String[] args) {
        new StartupSpringApplication(Project2Application.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-project2")
                .run(args);
    }
}
