package com.kuma.cloud.lab.spring.domain.event;

/**
 * 订单创建事件，用于串联分层架构与事件监听。
 */
public record LabOrderCreatedEvent(String orderId, String product, double amount) {
}
