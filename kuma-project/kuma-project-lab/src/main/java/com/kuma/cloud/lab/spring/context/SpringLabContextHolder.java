package com.kuma.cloud.lab.spring.context;

import com.kuma.cloud.lab.spring.support.SpringLabEventHistory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 演示 ApplicationContextAware：容器启动后将上下文引用注入到 Bean 中。
 */
@Component
public class SpringLabContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private final SpringLabEventHistory eventHistory;

    public SpringLabContextHolder(SpringLabEventHistory eventHistory) {
        this.eventHistory = eventHistory;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
        eventHistory.recordContext("ApplicationContextAware#setApplicationContext 回调完成");
    }

    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext 尚未初始化");
        }
        return applicationContext;
    }

    public static boolean containsBean(String name) {
        return getApplicationContext().containsBean(name);
    }

    public static <T> T getBean(Class<T> type) {
        return getApplicationContext().getBean(type);
    }

}
