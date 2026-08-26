package com.kuma.cloud.lab.spring.ioc;

import org.springframework.stereotype.Component;

/**
 * 组件扫描注册的 Bean 实现。
 */
@Component("plainMessageRenderer")
public class PlainMessageRenderer implements MessageRenderer {

    @Override
    public String type() {
        return "plain";
    }

    @Override
    public String render(String message) {
        return message;
    }

}
