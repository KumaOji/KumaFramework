package com.kuma.ai.python;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.kuma.boot.core.startup.StartupSpringApplication;
import com.kuma.boot.web.annotation.KumaBootApplication;
import com.kuma.cloud.bootstrap.annotation.KumaCloudApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@KumaBootApplication
@KumaCloudApplication
@ComponentScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.python"})
@EnableAutoConfiguration(exclude = DruidDataSourceAutoConfigure.class)
@ConfigurationPropertiesScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.python"})
public class PythonApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(PythonApplication.class);
    }

    public static void main(String[] args) {
        new StartupSpringApplication(PythonApplication.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-python")
                .setKmcAllowBeanDefinitionOverriding(true)
                .run(args);
    }
}
