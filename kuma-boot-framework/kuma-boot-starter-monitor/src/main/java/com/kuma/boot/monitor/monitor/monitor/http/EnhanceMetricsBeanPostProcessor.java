package com.kuma.boot.monitor.monitor.monitor.http;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.monitor.monitor.utils.Metrics;
import com.kuma.boot.monitor.monitor.monitor.utils.MetricsMethodInterceptor;
import com.kuma.boot.monitor.monitor.monitor.utils.Proxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@ConditionalOnProperty(
   name = {"actuator.http.enhance.enable"},
   havingValue = "true",
   matchIfMissing = true
)
public class EnhanceMetricsBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
   static ApplicationContext applicationContext;

   public EnhanceMetricsBeanPostProcessor() {
   }

   public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
      Class<?> clazz = bean.getClass();
      if (!AnnotatedElementUtils.hasAnnotation(clazz, Controller.class) && !AnnotatedElementUtils.hasAnnotation(clazz, RequestMapping.class)) {
         return bean;
      } else {
         String var10000 = Metrics.getApplicationName(applicationContext);
         String key = "http_" + var10000 + "_" + clazz.getSimpleName();
         LogUtils.info("\u589e\u52a0http\u9ed8\u8ba4\u76d1\u63a7:{}", new Object[]{key});
         return Proxy.getProxy(bean, new MetricsMethodInterceptor(key));
      }
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      EnhanceMetricsBeanPostProcessor.applicationContext = applicationContext;
   }
}
