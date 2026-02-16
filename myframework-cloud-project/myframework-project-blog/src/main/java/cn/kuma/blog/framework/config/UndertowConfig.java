package cn.kuma.blog.framework.config;

import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Undertow WebSocket 配置
 * 解决 WebSocket 缓冲区池警告
 * 使用 @Lazy 延迟初始化，优化启动速度
 */
@Configuration
@Lazy
public class UndertowConfig implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {

    /**
     * 配置 Undertow WebSocket 缓冲区池
     * 解决警告：UT026010: Buffer pool was not set on WebSocketDeploymentInfo
     */
    @Override
    public void customize(UndertowServletWebServerFactory factory) {
        factory.addDeploymentInfoCustomizers(deploymentInfo -> {
            WebSocketDeploymentInfo webSocketDeploymentInfo = new WebSocketDeploymentInfo();
            DefaultByteBufferPool bufferPool = new DefaultByteBufferPool(true, 1024);
            webSocketDeploymentInfo.setBuffers(bufferPool);
            deploymentInfo.addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, webSocketDeploymentInfo);
        });
    }
}

