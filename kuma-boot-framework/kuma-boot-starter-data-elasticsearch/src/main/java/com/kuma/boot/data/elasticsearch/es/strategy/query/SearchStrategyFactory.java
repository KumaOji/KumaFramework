package com.kuma.boot.data.elasticsearch.es.strategy.query;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SearchStrategyFactory implements InitializingBean, ApplicationContextAware {
   private final Map<String, SearchStrategy> factory = new HashMap<>();
   private ApplicationContext appContext;

   public SearchStrategyFactory() {
   }

   public SearchStrategy handleStrategy(String model) {
      return (SearchStrategy)this.factory.get(model);
   }

   public void afterPropertiesSet() throws Exception {
      this.appContext.getBeansOfType(SearchStrategy.class).values().forEach((handler) -> this.factory.put(handler.getModel(), handler));
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.appContext = applicationContext;
   }
}
