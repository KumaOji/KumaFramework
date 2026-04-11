package com.kuma.boot.eventbus.disruptor.tmp5.starter;

import com.lmax.disruptor.dsl.Disruptor;
import com.kuma.boot.eventbus.disruptor.tmp5.service.DisruptorService;
import com.kuma.boot.eventbus.disruptor.tmp5.service.PrintThreadPoolService;
import com.kuma.boot.eventbus.disruptor.tmp5.service.ThreadPoolExecutorService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(
   proxyBeanMethods = false
)
@ConditionalOnClass({Disruptor.class})
public class DisruptorServiceConfiguration {
   public DisruptorServiceConfiguration() {
   }

   @Bean(
      destroyMethod = "destroy"
   )
   public DisruptorService disruptorService() {
      return new DisruptorService();
   }

   @Bean
   public PrintThreadPoolService printThreadPoolService() {
      return new PrintThreadPoolService();
   }

   @Bean
   public ThreadPoolExecutorService threadPoolExecutorService() {
      return new ThreadPoolExecutorService();
   }
}
