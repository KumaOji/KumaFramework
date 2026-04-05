package com.kuma.boot.monitor.monitor.monitor.dubbo;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.monitor.monitor.utils.Metrics;
import com.kuma.boot.monitor.monitor.monitor.utils.MetricsMethodInterceptor;
import com.kuma.boot.monitor.monitor.monitor.utils.Proxy;
import java.util.Objects;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.apache.dubbo.config.spring.ServiceBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class DubboMetricsBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
   static ApplicationContext applicationContext;

   public DubboMetricsBeanPostProcessor() {
   }

   public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
      return bean;
   }

   public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {
      if (bean instanceof ServiceBean serviceBean) {
         Object ref = serviceBean.getRef();
         String var10000 = Metrics.getApplicationName(applicationContext);
         String key = "p_" + var10000 + "_" + ref.getClass().getSimpleName();
         LogUtils.info("\u589e\u52a0dubbo service\u9ed8\u8ba4\u76d1\u63a7:{}", new Object[]{key});
         serviceBean.setRef(Proxy.getProxy(ref, new MetricsMethodInterceptor(key)));
         return bean;
      } else if (bean instanceof ReferenceBean) {
         ReferenceBean referenceBean = (ReferenceBean)Proxy.getProxy(bean, new MethodInterceptor() {
            {
               Objects.requireNonNull(DubboMetricsBeanPostProcessor.this);
            }

            public Object invoke(MethodInvocation invocation) throws Throwable {
               Object result = invocation.proceed();
               if ("getObject".equals(invocation.getMethod().getName())) {
                  String name = ((ReferenceBean)bean).getInterfaceClass().getSimpleName();
                  String var10000 = Metrics.getApplicationName(DubboMetricsBeanPostProcessor.applicationContext);
                  String key = "c_" + var10000 + "_" + name;
                  LogUtils.info("\u589e\u52a0dubbo reference\u9ed8\u8ba4\u76d1\u63a7:{}", new Object[]{key});
                  return Proxy.getProxy(result, new MetricsMethodInterceptor(key));
               } else {
                  return result;
               }
            }
         });
         return referenceBean;
      } else {
         return bean;
      }
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      DubboMetricsBeanPostProcessor.applicationContext = applicationContext;
   }
}
