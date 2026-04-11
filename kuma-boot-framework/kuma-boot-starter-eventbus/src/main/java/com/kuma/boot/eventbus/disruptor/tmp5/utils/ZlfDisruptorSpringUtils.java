package com.kuma.boot.eventbus.disruptor.tmp5.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ZlfDisruptorSpringUtils implements ApplicationContextAware {
   private static final Logger logger = LoggerFactory.getLogger(ZlfDisruptorSpringUtils.class);
   private static ApplicationContext applicationContext;

   public ZlfDisruptorSpringUtils() {
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      logger.info("\u5e94\u7528\u7a0b\u5e8f\u4e0a\u4e0b\u6587 \uff1a [{}]", "\u5f00\u59cb\u521d\u59cb\u5316");
      ZlfDisruptorSpringUtils.applicationContext = applicationContext;
      logger.info("\u5e94\u7528\u7a0b\u5e8f\u4e0a\u4e0b\u6587 \uff1a [{}]", "\u521d\u59cb\u5316\u5b8c\u6210");
   }

   public static ApplicationContext getApplicationContext() {
      return applicationContext;
   }

   public static Object getBean(String name) {
      return getApplicationContext().getBean(name);
   }

   public static <T> T getBean(Class<T> clazz) {
      return (T)getApplicationContext().getBean(clazz);
   }

   public static <T> T getBean(String name, Class<T> clazz) {
      return (T)getApplicationContext().getBean(name, clazz);
   }
}
