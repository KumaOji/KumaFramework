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
      logger.info("应用程序上下文 ： [{}]", "开始初始化");
      ZlfDisruptorSpringUtils.applicationContext = applicationContext;
      logger.info("应用程序上下文 ： [{}]", "初始化完成");
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
