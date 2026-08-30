package com.kuma.cloud.uaa;

import com.kuma.boot.security.spring.autoconfigure.OAuth2ComplianceConfiguration;
import com.kuma.boot.security.spring.autoconfigure.Oauth2ResourceAutoConfiguration;
import com.kuma.cloud.uaa.annotation.EnableUaaBootApplication;
import com.kuma.cloud.bootstrap.annotation.KumaCloudApplication;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Kuma 体系统一认证授权中心（UAA）启动类。
 *
 * <p>对外提供 OAuth2.1 / OIDC 协议端点，为 blog 等业务应用统一签发 RS256 JWT。
 *
 * @author kuma
 */
@EnableUaaBootApplication
@KumaCloudApplication
@ComponentScan(
        basePackages = {"com.kuma.boot", "com.kuma.cloud.uaa"},
        excludeFilters = {
            @ComponentScan.Filter(
                    type = FilterType.ASSIGNABLE_TYPE,
                    classes = Oauth2ResourceAutoConfiguration.class),
            @ComponentScan.Filter(
                    type = FilterType.ASSIGNABLE_TYPE,
                    classes = OAuth2ComplianceConfiguration.class)
        })
@EnableAutoConfiguration
@ConfigurationPropertiesScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.uaa"})
public class UaaApplication extends SpringBootServletInitializer {

    @Override
    protected @NonNull SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(UaaApplication.class);
    }

    static void main(String[] args) {
        new com.kuma.boot.core.startup.StartupSpringApplication(UaaApplication.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-uaa")
                .setKmcAllowBeanDefinitionOverriding(true)
                .run(args);
    }
}
