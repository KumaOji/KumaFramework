package com.kuma.boot.data.jpa.tenant.configuration;

import com.kuma.boot.data.jpa.tenant.properties.MultiTenantProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableConfigurationProperties({MultiTenantProperties.class})
@Import({DiscriminatorApproachConfiguration.class, SchemaApproachConfiguration.class, DatabaseApproachConfiguration.class})
public class DataTenantConfiguration {
   private static final Logger log = LoggerFactory.getLogger(SchemaApproachConfiguration.class);

   public DataTenantConfiguration() {
   }

   @PostConstruct
   public void postConstruct() {
      log.debug("[ttc] |- SDK [Data Tenant] Auto Configure.");
   }
}
