package com.kuma.boot.core.runtime.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 将Spring Cloud Commons及Spring Boot Cache等基础设施bean标记为ROLE_INFRASTRUCTURE，
 * 避免BeanPostProcessorChecker在BPP注册阶段对这些无需AOP代理的bean发出WARN日志。
 *
 * 根本原因：JCache BPP（无序，第3阶段注册）实例化时依赖 CacheProperties，
 * binding过程触发 ConfigurationPropertiesBindingPostProcessor 查找 BindHandlerAdvisor，
 * 进而过早实例化 CommonsConfigAutoConfiguration 等Spring Cloud bean，
 * 此时BPP注册尚未完成，BeanPostProcessorChecker发出警告。
 */
@Component
public class InfrastructureRoleMarkingBeanFactoryPostProcessor implements BeanFactoryPostProcessor, PriorityOrdered {

    private static final Set<String> INFRASTRUCTURE_BEAN_NAMES = Set.of(
            "org.springframework.cloud.commons.config.CommonsConfigAutoConfiguration",
            "org.springframework.cloud.client.loadbalancer.LoadBalancerDefaultMappingsProviderAutoConfiguration",
            "loadBalancerClientsDefaultsMappingsProvider",
            "defaultsBindHandlerAdvisor",
            "spring.cache-org.springframework.boot.cache.autoconfigure.CacheProperties"
    );

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanName : INFRASTRUCTURE_BEAN_NAMES) {
            if (beanFactory.containsBeanDefinition(beanName)) {
                beanFactory.getBeanDefinition(beanName).setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            }
        }
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }
}
