package com.kuma.cloud.console;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(ConsoleProperties.class)
public class ConsoleApplication {
    public static void main(String[] args) {
        var context = SpringApplication.run(ConsoleApplication.class, args);
        if (context.getEnvironment().getProperty("console.desktop-managed", Boolean.class, false)) {
            Thread.ofVirtual().name("console-desktop-lifecycle").start(() -> DesktopShutdown.await(System.in, context::close));
        }
    }
}
