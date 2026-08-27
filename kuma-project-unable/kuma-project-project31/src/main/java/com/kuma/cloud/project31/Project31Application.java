package com.kuma.cloud.project31;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Project31Application
 * <p>
 * kuma-cloud-gradle-plugin 使用演示
 * <p>
 * 运行前先执行：
 *   ./gradlew :kuma-other-framework:kuma-cloud-plugin:kuma-cloud-gradle-plugin:publishToMavenLocal
 * <p>
 * 演示 Gradle Task：
 *   ./gradlew :kuma-project:kuma-project-project31:hello   → 执行 SayHelloTask，输出 "Hello, World!"
 *   ./gradlew :kuma-project:kuma-project-project31:bootJar → ServicePlugin 打包可执行 fat-jar
 */
@SpringBootApplication
public class Project31Application {

    public static void main(String[] args) {
        SpringApplication.run(Project31Application.class, args);
    }
}
