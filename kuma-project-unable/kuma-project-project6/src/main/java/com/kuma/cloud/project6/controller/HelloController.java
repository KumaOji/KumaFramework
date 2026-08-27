package com.kuma.cloud.project6.controller;

import com.kuma.cloud.project6.api.HelloService;
import com.kuma.cloud.project6.rpc.RpcBootstrap;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * HelloController —— 通过 HTTP 触发 RPC 调用
 *
 * <p>示例请求：
 * <pre>
 *   GET /rpc/hello?name=Alice
 *   GET /rpc/add?a=3&amp;b=5
 * </pre>
 */
@RestController
@RequestMapping("/rpc")
@RequiredArgsConstructor
public class HelloController {

    /** RPC 引导类，持有服务端启动后创建的客户端代理 */
    private final RpcBootstrap rpcBootstrap;

    /**
     * 调用 RPC hello 方法。
     *
     * @param name 姓名（默认 "World"）
     * @return RPC 调用结果
     */
    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
        HelloService service = rpcBootstrap.helloService();
        if (service == null) {
            return "RPC client not ready, please try again later.";
        }
        return service.hello(name);
    }

    /**
     * 调用 RPC add 方法（演示带基本类型参数的 RPC）。
     *
     * @param a 加数
     * @param b 被加数
     * @return 求和结果
     */
    @GetMapping("/add")
    public String add(@RequestParam int a, @RequestParam int b) {
        HelloService service = rpcBootstrap.helloService();
        if (service == null) {
            return "RPC client not ready, please try again later.";
        }
        return a + " + " + b + " = " + service.add(a, b);
    }
}
