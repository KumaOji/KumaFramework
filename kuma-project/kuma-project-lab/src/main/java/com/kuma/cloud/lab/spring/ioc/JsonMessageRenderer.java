package com.kuma.cloud.lab.spring.ioc;

/**
 * Java Config 注册的 @Primary 实现。
 */
public class JsonMessageRenderer implements MessageRenderer {

    @Override
    public String type() {
        return "json";
    }

    @Override
    public String render(String message) {
        return "{\"message\":\"" + message + "\"}";
    }

}
