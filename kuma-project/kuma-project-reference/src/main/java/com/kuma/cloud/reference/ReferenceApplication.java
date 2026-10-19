package com.kuma.cloud.reference;

import com.kuma.boot.core.startup.StartupSpringApplication;
import com.kuma.boot.data.migration.env.DataMigrationEnvironmentPostProcessor;
import java.util.Map;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(excludeName = {
        "org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration",
        "org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration",
        "org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration",
        "org.springframework.boot.security.autoconfigure.actuate.web.servlet.ManagementWebSecurityAutoConfiguration"
})
@ConfigurationPropertiesScan
public class ReferenceApplication {
    public static void main(String[] args) {
        var application = new StartupSpringApplication(ReferenceApplication.class)
                .setKmcBanner()
                .setKmcApplicationProperty("kuma-reference");
        // The project explicitly selects Flyway; register the early listener directly
        // because this template does not use META-INF/spring listener discovery.
        application.addListeners(new DataMigrationEnvironmentPostProcessor());
        // Alibaba 2025.1 uses Config Data imports; load bootstrap.yml as a regular config file.
        application.setDefaultProperties(Map.of("spring.config.name", "bootstrap"));
        application.run(args);
    }
}
