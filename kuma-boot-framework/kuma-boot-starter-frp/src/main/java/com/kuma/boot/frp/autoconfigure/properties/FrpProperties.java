package com.kuma.boot.frp.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * FRP 客户端配置属性
 *
 * <p>示例：
 * <pre>
 * kuma:
 *   boot:
 *     frp:
 *       enabled: true
 *       server-addr: your-server.com
 *       server-port: 7000
 *       auth:
 *         token: your-token
 *       proxies:
 *         - name: ssh
 *           type: tcp
 *           local-port: 22
 *           remote-port: 6022
 * </pre>
 *
 * @author kuma
 */
@Data
@ConfigurationProperties(prefix = FrpProperties.PREFIX)
public class FrpProperties {

    public static final String PREFIX = "kuma.boot.frp";

    /** 是否启用 FRP 客户端 */
    private boolean enabled = false;

    /** frps 服务端地址 */
    private String serverAddr = "127.0.0.1";

    /** frps 服务端端口 */
    private int serverPort = 7000;

    /**
     * 自定义 frpc 可执行文件路径。
     * 为空时优先使用工具目录下已有文件，其次从 classpath 内置资源释放。
     */
    private String frpcPath;

    /**
     * frpc 工具释放目录（内置 frpc.exe 首次运行时解压到此处）。
     * 默认 ${user.home}/.kuma/frp
     */
    private String toolDir;

    /** 进程崩溃后是否自动重启 */
    private boolean autoRestart = true;

    /** 自动重启前等待时间（毫秒） */
    private long restartDelayMs = 3000;

    /**
     * 最大重启次数，0 表示不限制。
     * 超过后停止重启，需手动干预。
     */
    private int maxRestartAttempts = 5;

    /** token 认证密钥 */
    private String token = "";

    /** 代理列表 */
    private List<Proxy> proxies = new ArrayList<>();

    // ── 嵌套配置类 ─────────────────────────────────────────────────────────

    @Data
    public static class Proxy {
        /** 代理名称（唯一标识） */
        private String name;
        /** 代理类型：tcp / udp / http / https / stcp / xtcp */
        private String type = "tcp";
        /** 本地服务 IP，默认 127.0.0.1 */
        private String localIp = "127.0.0.1";
        /** 本地服务端口 */
        private int localPort;
        /** frps 映射的远端端口（tcp/udp 类型使用） */
        private int remotePort;
        /** HTTP/HTTPS 类型：自定义域名，逗号分隔 */
        private String customDomains;
        /** HTTP/HTTPS 类型：子域名 */
        private String subdomain;
        /** HTTP 类型：路径前缀 */
        private String locations;
    }
}
