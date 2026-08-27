package com.kuma.cloud.project6.service;

import com.kuma.cloud.project6.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HelloServiceImpl —— RPC 服务实现（服务端）
 *
 * <p>由 {@link com.kuma.cloud.project6.rpc.RpcBootstrap} 通过
 * {@code ServiceBs.register("hello", new HelloServiceImpl())} 注册到 RPC 服务端。
 *
 * <p>注意：此类不需要 Spring {@code @Service} 注解，由 RPC 框架自身管理实例。
 */
public class HelloServiceImpl implements HelloService {

    private static final Logger log = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(String name) {
        log.info("[RPC Server] hello() called, name={}", name);
        return "Hello, " + name + "! (from kuma-cloud-rpc server)";
    }

    @Override
    public int add(int a, int b) {
        log.info("[RPC Server] add() called, a={}, b={}", a, b);
        return a + b;
    }
}
