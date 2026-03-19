package com.kuma.cloud.project8;

import com.kuma.boot.core.startup.StartupSpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 分布式事务 Demo
 * <p>
 * 演示 kuma-cloud-tx-rm 的接入：
 * 1. 启动 kuma-cloud-tx-server（事务管理者，Netty 监听 8080）
 * 2. 本应用作为事务参与者，通过 @DistributedTransactional 协调多步操作
 * </p>
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.project8", "com.kuma.cloud.tx.rm"})
public class Project8Application {

    public static void main(String[] args) {
        new StartupSpringApplication(Project8Application.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-project8")
                .run(args);
    }
}
