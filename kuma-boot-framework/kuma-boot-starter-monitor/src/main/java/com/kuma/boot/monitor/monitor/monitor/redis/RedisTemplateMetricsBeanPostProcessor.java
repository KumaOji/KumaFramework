package com.kuma.boot.monitor.monitor.monitor.redis;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.monitor.monitor.utils.Metrics;
import com.kuma.boot.monitor.monitor.monitor.utils.MetricsMethodInterceptor;
import com.kuma.boot.monitor.monitor.monitor.utils.Proxy;
import java.util.Objects;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
   name = {"actuator.redis.template.enhance.enable"},
   havingValue = "true",
   matchIfMissing = true
)
public class RedisTemplateMetricsBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
   static ApplicationContext applicationContext;

   public RedisTemplateMetricsBeanPostProcessor() {
   }

   public Object postProcessBeforeInitialization(Object bean, String beanName) {
      return bean;
   }

   public Object postProcessAfterInitialization(Object bean, String beanName) {
      if (bean instanceof RedisTemplate redisTemplate) {
         RedisConnectionFactory redisConnectionFactory = redisTemplate.getConnectionFactory();
         String key = "redis_metrics_" + Metrics.getApplicationName(applicationContext);
         LogUtils.info("\u589e\u52a0redisTemplate\u9ed8\u8ba4\u76d1\u63a7:{}", new Object[]{key});
         RedisConnectionFactory proxy = (RedisConnectionFactory)Proxy.getProxy(redisConnectionFactory, new MethodInterceptor() {
            {
               Objects.requireNonNull(RedisTemplateMetricsBeanPostProcessor.this);
            }

            public Object invoke(MethodInvocation invocation) throws Throwable {
               Object result = invocation.proceed();
               return "getConnection".equals(invocation.getMethod().getName()) ? Proxy.getProxy(result, new MetricsMethodInterceptor("redis_metrics_" + Metrics.getApplicationName(RedisTemplateMetricsBeanPostProcessor.applicationContext))) : result;
            }
         });
         redisTemplate.setConnectionFactory(proxy);
      }

      return bean;
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      RedisTemplateMetricsBeanPostProcessor.applicationContext = applicationContext;
   }
}
