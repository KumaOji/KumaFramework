package com.kuma.cloud.project5;

import com.kuma.boot.core.startup.StartupSpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Project5 —— kuma-cloud-job Worker 使用示例
 * <p>
 * 启动前确保 job-server (port:8082) 和 job-nameserver (port:9089) 已运行
 * </p>
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.project5"})
public class Project5Application {

    public static void main(String[] args) {
        new StartupSpringApplication(Project5Application.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-project5")
                .run(args);
    }
}
