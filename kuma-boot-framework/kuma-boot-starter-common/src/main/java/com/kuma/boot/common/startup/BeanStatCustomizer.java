package com.kuma.boot.common.startup;

public interface BeanStatCustomizer {

    BeanStat customize(String beanName, Object bean, BeanStat beanStat);
}
