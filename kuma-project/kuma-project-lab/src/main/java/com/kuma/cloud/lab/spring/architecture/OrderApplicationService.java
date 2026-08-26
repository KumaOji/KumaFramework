package com.kuma.cloud.lab.spring.architecture;

import com.kuma.cloud.lab.spring.domain.event.LabOrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 业务层：编排 Repository 并发布领域事件，对应 Spring 架构中的 Service 层。
 */
@Service
@RequiredArgsConstructor
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public String createOrder(String orderId, String product, double amount) {
        orderRepository.save(orderId, "CREATED");
        eventPublisher.publishEvent(new LabOrderCreatedEvent(orderId, product, amount));
        return orderRepository.findStatus(orderId);
    }

}
