package com.kuma.boot.core.startup;

public interface BeanStatCustomizer {

    BeanStat customize(String beanName, Object bean, BeanStat beanStat);
}
