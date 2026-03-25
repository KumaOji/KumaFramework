package com.kuma.boot.sms.common.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sms.common.executor.DefaultSendAsyncThreadPoolExecutor;
import com.kuma.boot.sms.common.executor.SendAsyncThreadPoolExecutor;
import com.kuma.boot.sms.common.handler.SendHandler;
import com.kuma.boot.sms.common.loadbalancer.ILoadBalancer;
import com.kuma.boot.sms.common.loadbalancer.RandomSmsLoadBalancer;
import com.kuma.boot.sms.common.loadbalancer.RoundRobinSmsLoadBalancer;
import com.kuma.boot.sms.common.loadbalancer.SmsSenderLoadBalancer;
import com.kuma.boot.sms.common.loadbalancer.WeightRandomSmsLoadBalancer;
import com.kuma.boot.sms.common.loadbalancer.WeightRoundRobinSmsLoadBalancer;
import com.kuma.boot.sms.common.model.NoticeData;
import com.kuma.boot.sms.common.properties.SmsAsyncProperties;
import com.kuma.boot.sms.common.properties.SmsProperties;
import com.kuma.boot.sms.common.service.NoticeService;
import com.kuma.boot.sms.common.service.impl.DefaultNoticeService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@AutoConfiguration
@ConditionalOnProperty(
   prefix = "kuma.boot.sms",
   name = {"enabled"},
   havingValue = "true"
)
@EnableConfigurationProperties({SmsProperties.class, SmsAsyncProperties.class})
public class SmsAutoConfiguration implements InitializingBean {
   public void afterPropertiesSet() throws Exception {
      LogUtils.started(SmsAutoConfiguration.class, "kuma-boot-starter-sms-common", new String[0]);
   }

   @Bean
   @ConditionalOnMissingBean
   public NoticeService noticeService(SmsProperties properties, SmsAsyncProperties asyncProperties, ILoadBalancer smsSenderLoadbalancer, ObjectProvider executorProvider) {
      return new DefaultNoticeService(properties, asyncProperties, smsSenderLoadbalancer, (SendAsyncThreadPoolExecutor)executorProvider.getIfUnique());
   }

   @Bean
   @ConditionalOnMissingBean
   @ConditionalOnProperty(
      prefix = "kuma.boot.sms.async",
      name = {"enable"},
      havingValue = "true"
   )
   public SendAsyncThreadPoolExecutor sendAsyncThreadPoolExecutor(SmsAsyncProperties properties) {
      return new DefaultSendAsyncThreadPoolExecutor(properties);
   }

   @Bean
   @ConditionalOnMissingBean
   public SmsSenderLoadBalancer smsSenderLoadBalancer(SmsProperties properties) {
      String type = properties.getLoadBalancerType();
      if (type == null) {
         return new RandomSmsLoadBalancer();
      } else {
         type = type.trim();
         if ("RoundRobin".equalsIgnoreCase(type)) {
            return new RoundRobinSmsLoadBalancer();
         } else if ("WeightRandom".equalsIgnoreCase(type)) {
            return new WeightRandomSmsLoadBalancer();
         } else {
            return (SmsSenderLoadBalancer)("WeightRoundRobin".equalsIgnoreCase(type) ? new WeightRoundRobinSmsLoadBalancer() : new RandomSmsLoadBalancer());
         }
      }
   }

   @Bean
   @ConditionalOnMissingBean
   public RestTemplate restTemplate() {
      return new RestTemplate();
   }
}
