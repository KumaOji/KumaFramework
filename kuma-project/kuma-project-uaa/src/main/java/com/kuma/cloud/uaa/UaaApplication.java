package com.kuma.cloud.uaa;

import com.kuma.boot.core.startup.StartupSpringApplication;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import java.util.Map;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Kuma 体系统一认证授权中心（UAA）启动类。
 *
 * <p>对外提供 OAuth2.1 / OIDC 协议端点，为 blog 等业务应用统一签发 RS256 JWT。
 *
 * @author kuma
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class UaaApplication extends SpringBootServletInitializer {

    @Override
    protected @NonNull SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(UaaApplication.class)
                .properties("spring.config.name=application,bootstrap");
    }

    public static void main(String[] args) {
        var application = new StartupSpringApplication(UaaApplication.class)
                .setKmcBanner()
                .setKmcApplicationProperty("kuma-cloud-uaa");
        application.setDefaultProperties(Map.of("spring.config.name", "application,bootstrap"));
        application.run(args);
    }
}
