package com.kuma.boot.arthas.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * Arthas 诊断配置。
 *
 * <p>当 {@code kuma.boot.arthas.enabled=true} 时，应用启动后会通过 arthas-agent-attach
 * 将 Arthas 嵌入到当前 JVM，无需在生产机器上单独下载/启动 arthas-boot。</p>
 */
@RefreshScope
@ConfigurationProperties(prefix = ArthasProperties.PREFIX)
public class ArthasProperties {

    public static final String PREFIX = "kuma.boot.arthas";

    /** 是否启用嵌入式 Arthas，默认关闭（诊断工具应显式开启）。 */
    private boolean enabled = false;

    /** 应用名称，用于在 Arthas Tunnel Server 中区分不同应用。 */
    private String appName;

    /** Arthas Tunnel Server 地址，例如 ws://127.0.0.1:7777/ws，用于远程统一管理。 */
    private String tunnelServer;

    /** 注册到 Tunnel Server 的 agentId，留空则自动生成。 */
    private String agentId;

    /** 绑定 IP，默认 127.0.0.1，仅本机可连接；如需远程连接可设为 0.0.0.0。 */
    private String ip = "127.0.0.1";

    /** telnet 端口，默认 3658。 */
    private int telnetPort = 3658;

    /** http 端口，默认 8563。 */
    private int httpPort = 8563;

    /** Web Console / telnet 认证用户名（可选）。 */
    private String username;

    /** Web Console / telnet 认证密码（可选）。 */
    private String password;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTunnelServer() {
        return tunnelServer;
    }

    public void setTunnelServer(String tunnelServer) {
        this.tunnelServer = tunnelServer;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getTelnetPort() {
        return telnetPort;
    }

    public void setTelnetPort(int telnetPort) {
        this.telnetPort = telnetPort;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
