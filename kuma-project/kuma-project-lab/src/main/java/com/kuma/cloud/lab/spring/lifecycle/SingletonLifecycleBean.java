package com.kuma.cloud.lab.spring.lifecycle;

import com.kuma.cloud.lab.spring.support.SpringLabEventHistory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/**
 * 单例 Bean 的生命周期演示。
 */
@Component
public class SingletonLifecycleBean {

    private final SpringLabEventHistory eventHistory;

    public SingletonLifecycleBean(SpringLabEventHistory eventHistory) {
        this.eventHistory = eventHistory;
    }

    @PostConstruct
    void onCreate() {
        eventHistory.recordLifecycle("SingletonLifecycleBean @PostConstruct");
    }

    @PreDestroy
    void onDestroy() {
        eventHistory.recordLifecycle("SingletonLifecycleBean @PreDestroy");
    }

}
