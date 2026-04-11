package com.kuma.boot.sensitive.sensitivemvc;

import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean({FastJsonHttpMessageConverter.class})
@ConditionalOnClass({FastJsonConfig.class, FastJsonHttpMessageConverter.class})
public class FastJsonAutoConfiguration {
   public FastJsonAutoConfiguration() {
   }

   @Bean
   public FastJsonBeanPostProcessor fastJsonBeanPostProcessor() {
      return new FastJsonBeanPostProcessor();
   }
}
