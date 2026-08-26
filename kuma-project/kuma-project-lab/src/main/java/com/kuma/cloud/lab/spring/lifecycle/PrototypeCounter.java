package com.kuma.cloud.lab.spring.lifecycle;

import com.kuma.cloud.lab.spring.support.SpringLabEventHistory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 演示 Bean 生命周期回调与 prototype 作用域。
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PrototypeCounter {

    private final SpringLabEventHistory eventHistory;
    private final int instanceId;

    public PrototypeCounter(SpringLabEventHistory eventHistory) {
        this.eventHistory = eventHistory;
        this.instanceId = System.identityHashCode(this);
    }

    @PostConstruct
    void onCreate() {
        eventHistory.recordLifecycle("PrototypeCounter@" + instanceId + " @PostConstruct");
    }

    @PreDestroy
    void onDestroy() {
        eventHistory.recordLifecycle("PrototypeCounter@" + instanceId + " @PreDestroy");
    }

    public int instanceId() {
        return instanceId;
    }

}
