package com.kuma.boot.tracer.autoconfigure;

import com.p6spy.engine.spy.P6ModuleManager;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.p6spy.autoconfigure.properties.P6spyProperties;
import java.lang.reflect.Field;
import javax.sql.DataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.util.ReflectionUtils;

@AutoConfiguration
public class SleuthP6spyAutoConfiguration implements BeanPostProcessor, Ordered, ApplicationContextAware {
   boolean isLoad = false;
   private P6spyProperties p6spyProperties;
   private ApplicationContext applicationContext;

   public SleuthP6spyAutoConfiguration() {
   }

   public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
      try {
         if (bean instanceof DataSource && !this.isLoad) {
            this.p6spyProperties = this.getP6spyProperties();
            Field[] fields = P6spyProperties.class.getDeclaredFields();

            for(Field field : fields) {
               ReflectionUtils.makeAccessible(field);
               Object val = field.get(this.p6spyProperties);
               String name = field.getName();
               if (val instanceof String) {
                  String valStr = (String)val;
                  String propertyKey = String.format("%s.%s", "p6spy.config", name);
                  if ("modulelist".equals(name)) {
                     valStr = valStr + ",brave.p6spy.TracingP6Factory";
                  }

                  System.setProperty(propertyKey, valStr);
               }
            }

            P6ModuleManager.getInstance().reload();
            this.isLoad = true;
         }
      } catch (IllegalAccessException e) {
         LogUtils.error(e);
      }

      return bean;
   }

   public P6spyProperties getP6spyProperties() {
      if (this.p6spyProperties == null) {
         this.p6spyProperties = (P6spyProperties)this.applicationContext.getBean(P6spyProperties.class);
      }

      return this.p6spyProperties;
   }

   public int getOrder() {
      return 30;
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
   }
}
