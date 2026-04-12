package com.kuma.boot.webagg.aop;

import com.kuma.boot.ddd.model.types.MarkerCheck;
import com.kuma.boot.webagg.controller.BusinessController;
import com.kuma.boot.webagg.controller.InnerController;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RecordValidationAutoProxyCreator extends AbstractAdvisorAutoProxyCreator implements Ordered {

   private final RecordValidationInterceptor interceptor = new RecordValidationInterceptor();
   private final RecordValidationAdvisor advisor = new RecordValidationAdvisor(this.interceptor);

   public RecordValidationAutoProxyCreator() {
      // 设置极高的优先级，确保比默认的 AnnotationAwareAspectJAutoProxyCreator (默认 Order=0) 先执行
      // Ordered.HIGHEST_PRECEDENCE = Integer.MIN_VALUE
      //this.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
   }

   @Override
   protected List<Advisor> findEligibleAdvisors( Class<?> beanClass, String beanName ) {
      // 快速过滤：只处理继承自 BusinessController 或 InnerController 的类
      if (BusinessController.class.isAssignableFrom(beanClass) ||
              InnerController.class.isAssignableFrom(beanClass)) {

         List<Advisor> advisors = new ArrayList<>();
         advisors.add(this.advisor);
         return advisors;
      }

      return super.findEligibleAdvisors(beanClass, beanName);
   }

   /**
    * 核心重写：处理已经存在的代理对象
    */
   @Override
   protected Object wrapIfNecessary( Object bean, String beanName, Object cacheKey ) {
      // 1. 获取目标类
      Class<?> beanClass = AopUtils.getTargetClass(bean);

      // 2. 判断是否需要我们的逻辑
      if (!BusinessController.class.isAssignableFrom(beanClass) &&
              !InnerController.class.isAssignableFrom(beanClass)) {
         return bean;
      }

      // 3. 【关键】检查是否已经是代理对象 (由其他 Creator 先执行了)
      // 如果我们的 Order 设置得不够小，可能会发生这种情况
      if (AopUtils.isAopProxy(bean)) {
         return mergeAdvisors((Advised) bean);
      }

      // 4. 正常流程：调用父类创建代理
      return super.wrapIfNecessary(bean, beanName, cacheKey);
   }

   /**
    * 合并逻辑：将现有的拦截器链与我们的拦截器合并
    */
   private Object mergeAdvisors( Advised advisedBean ) {
      try {
         // 获取原始目标对象
         Object target = advisedBean.getTargetSource().getTarget();

         // 获取现有的所有拦截器/Advisor
         Advisor[] existingAdvisors = advisedBean.getAdvisors();

         // 创建新的 ProxyFactory
         ProxyFactory proxyFactory = new ProxyFactory(target);
         proxyFactory.copyFrom(this);

         // 复制原有的配置 (接口、代理目标类等)
         proxyFactory.setInterfaces(advisedBean.getProxiedInterfaces());
         proxyFactory.setProxyTargetClass(advisedBean.isProxyTargetClass());
         // 标记为已过滤
         proxyFactory.setPreFiltered(true);

         // 【核心步骤】先添加我们的 Advisor (确保高优先级，最先执行)
//			if (this.advisor == null) {
//				this.advisor = new RecordValidationAdvisor(this.interceptor);
//			}
         // 插在链表头部
         proxyFactory.addAdvisor(0, this.advisor);

         // 再添加原有的所有 Advisor
         for (Advisor advisor : existingAdvisors) {
            proxyFactory.addAdvisor(advisor);
         }

         // 生成新的代理对象
         return proxyFactory.getProxy(ClassUtils.getDefaultClassLoader());

      } catch (Exception e) {
         throw new AopConfigException("Failed to merge advisors for bean: " + advisedBean, e);
      }
   }

   @Override
   public int getOrder() {
      return Ordered.HIGHEST_PRECEDENCE + 10;
   }
}
