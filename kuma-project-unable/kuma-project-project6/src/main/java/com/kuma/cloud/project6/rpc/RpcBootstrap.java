package com.kuma.cloud.project6.rpc;

import com.kuma.cloud.project6.api.HelloService;
import com.kuma.cloud.project6.service.HelloServiceImpl;
import com.kuma.cloud.rpc.client.core.ClientBs;
import com.kuma.cloud.rpc.server.core.ServiceBs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * RpcBootstrap —— RPC 服务端启动 + 客户端代理初始化
 *
 * <p>实现 {@link ApplicationRunner}，在 Spring 上下文就绪后执行：
 * <ol>
 *   <li>用 {@link ServiceBs} 注册并暴露 RPC 服务（Netty 监听 {@code rpc.server.port}）</li>
 *   <li>等待 Netty 绑定完成（异步启动）</li>
 *   <li>用 {@link ClientBs} 创建服务代理，供 Controller 直接注入使用</li>
 * </ol>
 *
 * <p>名称服务器（{@code rpc.nameserver.address}）可选；未启动时服务注册失败，
 * 但直连 RPC 调用（通过 {@code rpc.client.server-address} 指定地址）仍可正常使用。
 */
@Component
public class RpcBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RpcBootstrap.class);

    /** RPC 服务接口唯一标识，客户端与服务端必须一致 */
    static final String SERVICE_ID = "hello";

    @Value("${rpc.server.port:9527}")
    private int serverPort;

    @Value("${rpc.nameserver.address:}")
    private String nameserverAddress;

    @Value("${rpc.client.server-address:127.0.0.1:9527}")
    private String clientServerAddress;

    @Value("${rpc.client.timeout:5000}")
    private long clientTimeout;

    /** RPC 客户端代理，在服务端启动后初始化 */
    private volatile HelloService helloService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        startRpcServer();
        // Netty 异步绑定，稍作等待确保端口就绪
        Thread.sleep(500);
        initRpcClient();
    }

    /**
     * 启动 RPC 服务端。
     *
     * <p>{@code registerCenter} 指向名称服务器（可选）；若未启动则服务注册失败，
     * 但 Netty 服务端依然绑定成功，客户端可通过直连地址调用。
     */
    private void startRpcServer() {
        log.info("[RPC] Starting server on port {} ...", serverPort);
        ServiceBs serviceBs = (ServiceBs) ServiceBs.getInstance()
                .port(serverPort)
                .register(SERVICE_ID, new HelloServiceImpl());
        if (nameserverAddress != null && !nameserverAddress.isBlank()) {
            serviceBs.registerCenter(nameserverAddress);
        }
        serviceBs.expose();
        log.info("[RPC] Server exposed on port {}.", serverPort);
    }

    /**
     * 初始化 RPC 客户端代理（直连模式）。
     *
     * <p>直连无需名称服务器；{@code subscribe(false)} 关闭自动发现，
     * 直接通过 {@code addresses} 指定服务端地址。
     */
    private void initRpcClient() {
        log.info("[RPC] Connecting client to {} ...", clientServerAddress);
        this.helloService = ClientBs.<HelloService>newInstance()
                .serviceId(SERVICE_ID)
                .serviceInterface(HelloService.class)
                .addresses(clientServerAddress)
                .timeout(clientTimeout)
                .subscribe(false)
                .reference();
        log.info("[RPC] Client connected to {}.", clientServerAddress);
    }

    /**
     * 获取 RPC 客户端代理。
     *
     * @return HelloService 代理实例，未初始化完成时返回 {@code null}
     */
    public HelloService helloService() {
        return helloService;
    }
}
