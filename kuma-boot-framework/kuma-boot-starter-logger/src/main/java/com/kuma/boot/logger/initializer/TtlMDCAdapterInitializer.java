package com.kuma.boot.logger.initializer;

import org.slf4j.TtlMDCAdapter;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.annotation.Order;

@Order(1)
public class TtlMDCAdapterInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
   public TtlMDCAdapterInitializer() {
   }

   public void initialize(ConfigurableApplicationContext applicationContext) {
      if (!(applicationContext instanceof AnnotationConfigApplicationContext)) {
         TtlMDCAdapter.getInstance();
      }

   }
}
