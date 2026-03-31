package com.kuma.boot.monitor.monitor.monitor.thread;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
   name = {"actuator.bean.definition.remove.enable"},
   havingValue = "true"
)
public class RemoveBeanDefinitionRegistryProcessor implements BeanDefinitionRegistryPostProcessor {
   private final String remove = "jvmThreadMetrics";

   public RemoveBeanDefinitionRegistryProcessor() {
   }

   public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
      if (registry.containsBeanDefinition("jvmThreadMetrics")) {
         registry.removeBeanDefinition("jvmThreadMetrics");
         LogUtils.info("Removed bean definition: {}", new Object[]{"jvmThreadMetrics"});
      }

   }

   public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
   }
}
