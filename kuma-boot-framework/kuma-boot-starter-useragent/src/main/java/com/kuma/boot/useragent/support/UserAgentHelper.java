package com.kuma.boot.useragent.support;

import com.kuma.boot.useragent.domain.UserAgent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpHeaders;

public class UserAgentHelper implements ApplicationContextAware {
   private ApplicationContext applicationContext;

   public UserAgentHelper() {
   }

   public UserAgent convert(HttpHeaders headers) {
      for(UserAgentConverter converter : this.applicationContext.getBeanProvider(UserAgentConverter.class)) {
         UserAgent userAgent = converter.convert(headers);
         if (userAgent != null) {
            return userAgent;
         }
      }

      throw new NoSuchBeanDefinitionException(UserAgentConverter.class);
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
   }
}
