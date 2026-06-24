package com.kuma.cloud.blog;

import com.alibaba.druid.spring.boot4.autoconfigure.DruidDataSourceAutoConfigure;
import com.kuma.boot.core.startup.StartupSpringApplication;
import com.kuma.boot.web.annotation.KumaBootApplication;
import com.kuma.cloud.bootstrap.annotation.KumaCloudApplication;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@KumaBootApplication
@KumaCloudApplication
@ComponentScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.blog"})
@EnableAutoConfiguration
@ConfigurationPropertiesScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.blog"})
public class BlogApplication extends SpringBootServletInitializer {

    @Override
    protected @NonNull SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BlogApplication.class);
    }

    static void main(String[] args) {
        new StartupSpringApplication(BlogApplication.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-blog")
                .setKmcAllowBeanDefinitionOverriding(true)
                .run(args);
    }
}
