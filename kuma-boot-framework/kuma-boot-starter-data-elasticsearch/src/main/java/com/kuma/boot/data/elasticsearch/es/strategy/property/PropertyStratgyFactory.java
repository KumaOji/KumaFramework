package com.kuma.boot.data.elasticsearch.es.strategy.property;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class PropertyStratgyFactory implements InitializingBean, ApplicationContextAware {
   private final Map<String, PropertyStratgy> factory = new HashMap();
   private ApplicationContext appContext;

   public PropertyStratgyFactory() {
   }

   public PropertyStratgy handleStrategy(String model) {
      return (PropertyStratgy)this.factory.get(model);
   }

   public void afterPropertiesSet() throws Exception {
      this.appContext.getBeansOfType(PropertyStratgy.class).values().forEach((handler) -> this.factory.put(handler.getType(), handler));
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.appContext = applicationContext;
   }
}
