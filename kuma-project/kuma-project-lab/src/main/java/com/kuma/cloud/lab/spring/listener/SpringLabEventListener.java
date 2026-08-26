package com.kuma.cloud.lab.spring.listener;

import com.kuma.cloud.lab.spring.domain.event.LabOrderCreatedEvent;
import com.kuma.cloud.lab.spring.domain.event.LabUserRegisteredEvent;
import com.kuma.cloud.lab.spring.support.SpringLabEventHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 演示 @EventListener 监听自定义事件与容器事件。
 */
@Component
@RequiredArgsConstructor
public class SpringLabEventListener {

    private final SpringLabEventHistory eventHistory;

    @EventListener
    public void onUserRegistered(LabUserRegisteredEvent event) {
        eventHistory.recordEvent("LabUserRegisteredEvent username=" + event.username()
                + ", source=" + event.source());
    }

    @EventListener
    public void onOrderCreated(LabOrderCreatedEvent event) {
        eventHistory.recordEvent("LabOrderCreatedEvent orderId=" + event.orderId()
                + ", product=" + event.product()
                + ", amount=" + event.amount());
    }

    @EventListener
    public void onContextRefreshed(ContextRefreshedEvent event) {
        String contextId = event.getApplicationContext().getId();
        eventHistory.recordContext("ContextRefreshedEvent contextId=" + contextId);
    }

}
