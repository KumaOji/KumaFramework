package com.kuma.boot.core.runtime.bean;

import com.google.common.collect.Maps;
import com.kuma.boot.common.utils.log.LogUtils;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

//ZLowOrderTimeCostBeanPostProcessor ，他基本会最后执行，原因是他没有实现优先级接口，同时类名还是Z开头的，
// 我们可以用它来监控容器中所有bean执行BeanPostProcessor的after方法的执行耗时
/**
 * ZBeanPostProcessor
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@Component
public class ZBeanPostProcessor implements BeanPostProcessor {

    private Map<String, Long> costMap = Maps.newConcurrentMap();

    @Override
    public Object postProcessBeforeInitialization( Object bean, String beanName ) throws BeansException {
        costMap.put(beanName, System.currentTimeMillis());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization( Object bean, String beanName ) throws BeansException {
        if (costMap.containsKey(beanName)) {
            Long start = costMap.get(beanName);
            long cost = System.currentTimeMillis() - start;
            if (cost > 50) {
                LogUtils.info("==>After方法耗时beanName:{},操作耗时:{}", beanName, cost);
            }
        }
        return bean;
    }
}
