package com.kuma.cloud.reference;

import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ReferenceApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ReferenceApplication.class);
        // Alibaba 2025.1 uses Config Data imports; load bootstrap.yml as a regular config file.
        application.setDefaultProperties(Map.of("spring.config.name", "bootstrap"));
        application.run(args);
    }
}
