package com.kuma.cloud.reference;

import com.kuma.boot.core.startup.StartupSpringApplication;
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
        // Alibaba 2025.1 uses Config Data imports; load bootstrap.yml as a regular config file.
        application.setDefaultProperties(Map.of("spring.config.name", "bootstrap"));
        application.run(args);
    }
}
