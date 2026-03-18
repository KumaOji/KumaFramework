package com.kuma.cloud.project20;

import com.kuma.boot.core.startup.StartupSpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Project20 —— kuma-ai-agent gRPC 客户端示例
 *
 * <p>演示通过 gRPC 调用 kuma-ai-agent Python 服务，涵盖：
 * <ul>
 *   <li>阻塞式对话：{@code POST /agent/chat}</li>
 *   <li>流式对话（SSE）：{@code GET /agent/chat/stream}</li>
 *   <li>会话管理：{@code GET/DELETE /agent/threads}</li>
 *   <li>配置查询：{@code GET /agent/config}</li>
 *   <li>健康检查：{@code GET /agent/health}</li>
 * </ul>
 *
 * <p>启动依赖：
 * <pre>
 *   # 先启动 Python Agent gRPC 服务（默认 127.0.0.1:50051）
 *   uv run python app/grpc_server.py
 * </pre>
 *
 * <p>然后运行本应用：
 * <pre>
 *   ./gradlew :kuma-project:kuma-project-project20:bootRun
 * </pre>
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.project20"})
public class Project20Application {

    public static void main(String[] args) {
        new StartupSpringApplication(Project20Application.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-project20")
                .run(args);
    }
}
