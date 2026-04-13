package com.kuma.boot.data.jpa.simplestjpa.config;

import com.kuma.boot.data.jpa.simplestjpa.plugin.TenantData;
import com.kuma.boot.data.jpa.simplestjpa.plugin.TenantFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class BeanAutoConfiguration {
   public BeanAutoConfiguration() {
   }

   @Bean
   @ConditionalOnMissingBean
   public TenantProperties tenantProperties() {
      return new TenantProperties();
   }

   @Bean
   @ConditionalOnMissingBean
   public TenantFactory tenantFactory() {
      return new TenantData();
   }
}
