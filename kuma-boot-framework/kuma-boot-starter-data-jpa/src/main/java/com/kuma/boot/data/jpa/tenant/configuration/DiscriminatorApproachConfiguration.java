package com.kuma.boot.data.jpa.tenant.configuration;

import com.kuma.boot.data.jpa.tenant.hibernate.HerodotusTenantIdentifierResolver;
import jakarta.annotation.PostConstruct;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(
   proxyBeanMethods = false
)
public class DiscriminatorApproachConfiguration {
   private static final Logger log = LoggerFactory.getLogger(DiscriminatorApproachConfiguration.class);

   public DiscriminatorApproachConfiguration() {
   }

   @PostConstruct
   public void postConstruct() {
      log.debug("[ttc] |- SDK [Discriminator Approach] Auto Configure.");
   }

   @Bean
   public CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {
      HerodotusTenantIdentifierResolver herodotusTenantIdentifierResolver = new HerodotusTenantIdentifierResolver();
      log.debug("[ttc] |- Bean [Current Tenant Identifier Resolver] Auto Configure.");
      return herodotusTenantIdentifierResolver;
   }
}
