package com.kuma.boot.webagg.aop;

import com.kuma.boot.webagg.controller.BusinessController;
import com.kuma.boot.webagg.controller.InnerController;
import java.util.ArrayList;
import java.util.List;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;

public class RecordValidationAutoProxyCreator extends AbstractAdvisorAutoProxyCreator implements Ordered {
   private final RecordValidationInterceptor interceptor = new RecordValidationInterceptor();
   private final RecordValidationAdvisor advisor;

   public RecordValidationAutoProxyCreator() {
      this.advisor = new RecordValidationAdvisor(this.interceptor);
   }

   protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
      if (!BusinessController.class.isAssignableFrom(beanClass) && !InnerController.class.isAssignableFrom(beanClass)) {
         return super.findEligibleAdvisors(beanClass, beanName);
      } else {
         List<Advisor> advisors = new ArrayList();
         advisors.add(this.advisor);
         return advisors;
      }
   }

   protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
      Class<?> beanClass = AopUtils.getTargetClass(bean);
      if (!BusinessController.class.isAssignableFrom(beanClass) && !InnerController.class.isAssignableFrom(beanClass)) {
         return bean;
      } else {
         return AopUtils.isAopProxy(bean) ? this.mergeAdvisors((Advised)bean) : super.wrapIfNecessary(bean, beanName, cacheKey);
      }
   }

   private Object mergeAdvisors(Advised advisedBean) {
      try {
         Object target = advisedBean.getTargetSource().getTarget();
         Advisor[] existingAdvisors = advisedBean.getAdvisors();
         ProxyFactory proxyFactory = new ProxyFactory(target);
         proxyFactory.copyFrom(this);
         proxyFactory.setInterfaces(advisedBean.getProxiedInterfaces());
         proxyFactory.setProxyTargetClass(advisedBean.isProxyTargetClass());
         proxyFactory.setPreFiltered(true);
         proxyFactory.addAdvisor(0, this.advisor);

         for(Advisor advisor : existingAdvisors) {
            proxyFactory.addAdvisor(advisor);
         }

         return proxyFactory.getProxy(ClassUtils.getDefaultClassLoader());
      } catch (Exception e) {
         throw new AopConfigException("Failed to merge advisors for bean: " + String.valueOf(advisedBean), e);
      }
   }

   public int getOrder() {
      return -2147483638;
   }
}
