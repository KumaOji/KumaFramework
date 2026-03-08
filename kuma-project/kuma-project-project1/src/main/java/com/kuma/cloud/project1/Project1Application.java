package com.kuma.cloud.project1;

import com.kuma.boot.core.startup.StartupSpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.project1"})
public class Project1Application {

    public static void main(String[] args) {
        new StartupSpringApplication(Project1Application.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-project1")
                .run(args);
    }
}
