package com.kuma.boot.arthas;

import com.kuma.boot.arthas.autoconfigure.properties.ArthasProperties;
import com.kuma.boot.common.utils.log.LogUtils;
import com.taobao.arthas.agent.attach.ArthasAgent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 应用启动后，将 Arthas 通过 arthas-agent-attach 嵌入到当前 JVM。
 *
 * <p>仅在 {@code kuma.boot.arthas.enabled=true} 时由 {@code ArthasAutoConfiguration} 装配。</p>
 */
public class ArthasAgentBootstrap implements InitializingBean {

    private final ArthasProperties properties;

    public ArthasAgentBootstrap(ArthasProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
        try {
            ArthasAgent.attach(buildConfigMap());
            LogUtils.info(
                    "Arthas attached. ip={}, telnetPort={}, httpPort={}, tunnelServer={}",
                    properties.getIp(), properties.getTelnetPort(), properties.getHttpPort(),
                    StringUtils.hasText(properties.getTunnelServer()) ? properties.getTunnelServer() : "<none>");
        } catch (Throwable t) {
            // 诊断组件不应影响业务启动，attach 失败仅告警
            LogUtils.warn("Arthas attach failed, skip. cause={}", t.getMessage());
        }
    }

    private Map<String, String> buildConfigMap() {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("arthas.ip", properties.getIp());
        configMap.put("arthas.telnetPort", String.valueOf(properties.getTelnetPort()));
        configMap.put("arthas.httpPort", String.valueOf(properties.getHttpPort()));
        putIfText(configMap, "arthas.appName", properties.getAppName());
        putIfText(configMap, "arthas.tunnelServer", properties.getTunnelServer());
        putIfText(configMap, "arthas.agentId", properties.getAgentId());
        putIfText(configMap, "arthas.username", properties.getUsername());
        putIfText(configMap, "arthas.password", properties.getPassword());
        return configMap;
    }

    private static void putIfText(Map<String, String> configMap, String key, String value) {
        if (StringUtils.hasText(value)) {
            configMap.put(key, value);
        }
    }
}
