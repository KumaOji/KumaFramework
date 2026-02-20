package com.kuma.boot.core.runtime.bean;

import com.google.common.collect.Maps;
import com.kuma.boot.common.utils.log.LogUtils;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

//TimeCostPriorityOrderedBeanPostProcessor是一个高优先级的后置处理器，所以会优先执行，
// 我们可以用它来监控容器中所有bean执行BeanPostProcessor的before方法的执行耗时，
// 具体实现也很简单，就是在这个后置处理器的before方法里面先记录下每个bean的执行的初始时间，
// 然后在after方法里面计算结束时间，中间的差值就是每个bean执行所有BeanPostProcessor的耗时
//
/**
 * TimeCostPriorityOrderedBeanPostProcessor
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@Component
public class TimeCostPriorityOrderedBeanPostProcessor implements BeanPostProcessor,
        PriorityOrdered {

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
                LogUtils.info("==>Before方法耗时beanName:{},操作耗时:{}", beanName, cost);
            }
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
