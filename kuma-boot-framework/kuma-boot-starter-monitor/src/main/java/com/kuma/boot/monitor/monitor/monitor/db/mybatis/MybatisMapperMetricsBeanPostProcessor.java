package com.kuma.boot.monitor.monitor.monitor.db.mybatis;

import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
   name = {"actuator.db.mybatis.enhance.enable"},
   havingValue = "true",
   matchIfMissing = true
)
public class MybatisMapperMetricsBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
   static ApplicationContext applicationContext;

   public MybatisMapperMetricsBeanPostProcessor() {
   }

   public Object postProcessBeforeInitialization(Object bean, String beanName) {
      return bean;
   }

   public Object postProcessAfterInitialization(Object bean, String beanName) {
      return bean instanceof MapperFactoryBean ? new MetricMapperFactoryBean((MapperFactoryBean)bean, applicationContext) : bean;
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      MybatisMapperMetricsBeanPostProcessor.applicationContext = applicationContext;
   }
}
