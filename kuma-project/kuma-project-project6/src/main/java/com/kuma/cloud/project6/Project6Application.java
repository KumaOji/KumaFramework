package com.kuma.cloud.project6;

import com.kuma.boot.core.startup.StartupSpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Project6 —— kuma-cloud-rpc 使用示例
 *
 * <p>演示在同一进程内启动 RPC 服务端并通过 RPC 客户端发起调用。
 * 真实场景中服务端与客户端通常运行在不同进程/机器，此处合并仅为方便演示。
 *
 * <p>启动依赖（可选）：
 * <ul>
 *   <li>kuma-cloud-rpc-nameserver（port 7103）—— 不启动时服务注册不可用，但直连调用仍正常</li>
 * </ul>
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.project6"})
public class Project6Application {

    public static void main(String[] args) {
        new StartupSpringApplication(Project6Application.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-project6")
                .run(args);
    }
}
