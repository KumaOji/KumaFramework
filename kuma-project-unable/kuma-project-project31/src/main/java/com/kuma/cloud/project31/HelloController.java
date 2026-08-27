package com.kuma.cloud.project31;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HelloController
 * <p>
 * 对应 Gradle 中的 SayHelloTask：
 *   Gradle Task  → ./gradlew hello          → 控制台输出 "Hello, World!"
 *   HTTP 接口    → GET /hello               → 返回       "Hello, World!"
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
