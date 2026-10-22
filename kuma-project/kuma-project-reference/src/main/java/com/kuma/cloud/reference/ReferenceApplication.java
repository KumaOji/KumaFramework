package com.kuma.cloud.reference;

import com.kuma.boot.core.startup.StartupSpringApplication;
import com.kuma.boot.web.annotation.KumaBootApplication;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@KumaBootApplication(oauth2ResourceServer = false)
@ConfigurationPropertiesScan
public class ReferenceApplication {
    public static void main(String[] args) {
        var application = new StartupSpringApplication(ReferenceApplication.class)
                .setKmcBanner()
                .setKmcApplicationProperty("kuma-reference");
        // Alibaba 2025.1 uses Config Data imports; load bootstrap.yml as a regular config file.
        application.setDefaultProperties(Map.of("spring.config.name", "bootstrap"));
        application.run(args);
    }
}
