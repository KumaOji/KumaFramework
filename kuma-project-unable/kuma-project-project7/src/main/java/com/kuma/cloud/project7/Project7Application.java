package com.kuma.cloud.project7;

import com.kuma.boot.core.startup.StartupSpringApplication;
import com.kuma.cloud.ccsr.client.starter.annotation.EnableCcsrClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Project7 —— kuma-cloud-ccsr 使用示例
 *
 * <p>演示通过 CcsrService 向 CCSR 分布式配置中心进行配置读写。
 *
 * <p>启动依赖（可选）：
 * <ul>
 *   <li>kuma-cloud-ccsr-server（port 8005/8006/8007）—— 不启动时请求会失败，但应用本身可正常启动</li>
 * </ul>
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.project7"})
@EnableCcsrClient(enable = true)
public class Project7Application {

    public static void main(String[] args) {
        System.setProperty("com.google.protobuf.use_unsafe_pre22_gencode", "true");

        new StartupSpringApplication(Project7Application.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-project7")
                .run(args);
    }
}
