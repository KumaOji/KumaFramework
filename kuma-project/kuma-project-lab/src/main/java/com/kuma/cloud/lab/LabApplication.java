package com.kuma.cloud.lab;

import com.kuma.boot.core.startup.StartupSpringApplication;
import com.kuma.boot.web.annotation.KumaBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@KumaBootApplication
@ComponentScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.lab"})
@ConfigurationPropertiesScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.lab"})
public class LabApplication {

    public static void main(String[] args) {
        new StartupSpringApplication(LabApplication.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-lab")
                .run(args);
    }
}
