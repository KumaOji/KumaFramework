package com.kuma.boot.job.xxl.trace;

import com.kuma.boot.common.utils.log.LogUtils;
import com.xxl.job.core.handler.IJobHandler;
import java.lang.reflect.Modifier;
import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class XxlJobBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {
   private BeanFactory beanFactory;

   public XxlJobBeanPostProcessor() {
   }

   public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
      return bean instanceof IJobHandler ? this.wrap(bean) : bean;
   }

   private Object wrap(Object bean) {
      boolean classFinal = Modifier.isFinal(bean.getClass().getModifiers());
      boolean cglibProxy = !classFinal;
      IJobHandler job = (IJobHandler)bean;

      try {
         return this.createProxy(bean, cglibProxy, new XxlJobMethodInterceptor(job, this.beanFactory));
      } catch (AopConfigException ex) {
         if (cglibProxy) {
            LogUtils.warn("Exception occurred while trying to create a proxy, falling back to JDK proxy", new Object[]{ex});
            return this.createProxy(bean, false, new XxlJobMethodInterceptor(job, this.beanFactory));
         } else {
            throw ex;
         }
      }
   }

   Object createProxy(Object bean, boolean cglibProxy, Advice advice) {
      ProxyFactoryBean factory = new ProxyFactoryBean();
      factory.setProxyTargetClass(cglibProxy);
      factory.addAdvice(advice);
      factory.setTarget(bean);
      return factory.getObject();
   }

   public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
      this.beanFactory = beanFactory;
   }
}
