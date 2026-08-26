package com.kuma.cloud.lab.spring.ioc;

/**
 * 消息渲染策略，演示接口 + 多实现 + @Primary 注入。
 */
public interface MessageRenderer {

    String type();

    String render(String message);

}
