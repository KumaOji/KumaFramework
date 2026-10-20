package com.kuma.cloud.blog;

import com.kuma.boot.core.startup.StartupSpringApplication;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import java.util.Map;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BlogApplication extends SpringBootServletInitializer {

    @Override
    protected @NonNull SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BlogApplication.class)
                .properties("spring.config.name=application,bootstrap");
    }

    public static void main(String[] args) {
        var application = new StartupSpringApplication(BlogApplication.class)
                .setKmcBanner()
                .setKmcApplicationProperty("kuma-cloud-blog");
        application.setDefaultProperties(Map.of("spring.config.name", "application,bootstrap"));
        application.run(args);
    }
}
