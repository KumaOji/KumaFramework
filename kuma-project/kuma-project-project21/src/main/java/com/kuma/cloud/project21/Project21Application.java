package com.kuma.cloud.project21;

import com.kuma.boot.core.startup.StartupSpringApplication;
import com.kuma.boot.web.annotation.KumaBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Project21 —— kuma-boot-starter-office Doc 生成示例
 *
 * <p>演示通过 Aspose.Words 生成 Word 文档并提供 HTTP 下载：
 * <ul>
 *   <li>根据请求参数动态生成 docx：{@code GET /doc/generate?title=xxx}</li>
 *   <li>HTML 片段转 docx 下载：{@code POST /doc/html2doc}</li>
 * </ul>
 *
 * <p>启动：
 * <pre>
 *   ./gradlew :kuma-project:kuma-project-project21:bootRun
 * </pre>
 */
@KumaBootApplication
@ComponentScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.project21"})
public class Project21Application {

    public static void main(String[] args) {
        new StartupSpringApplication(Project21Application.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-project21")
                .run(args);
    }
}
