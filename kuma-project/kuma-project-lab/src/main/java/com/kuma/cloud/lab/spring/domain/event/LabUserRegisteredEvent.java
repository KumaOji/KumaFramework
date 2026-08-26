package com.kuma.cloud.lab.spring.domain.event;

/**
 * 用户注册事件，演示 Spring 事件驱动模型。
 */
public record LabUserRegisteredEvent(String username, String source) {
}
