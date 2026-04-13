package com.kuma.boot.data.jpa.tenant.configuration;

import com.kuma.boot.data.jpa.tenant.annotation.ConditionalOnSchemaApproach;
import com.kuma.boot.data.jpa.tenant.hibernate.SchemaMultiTenantConnectionProvider;
import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(
   proxyBeanMethods = false
)
@ConditionalOnSchemaApproach
public class SchemaApproachConfiguration {
   private static final Logger log = LoggerFactory.getLogger(SchemaApproachConfiguration.class);

   public SchemaApproachConfiguration() {
   }

   @PostConstruct
   public void postConstruct() {
      log.debug("[ttc] |- SDK [Schema Approach] Auto Configure.");
   }

   @Bean
   public MultiTenantConnectionProvider multiTenantConnectionProvider(DataSource dataSource) {
      SchemaMultiTenantConnectionProvider schemaMultiTenantConnectionProvider = new SchemaMultiTenantConnectionProvider(dataSource);
      log.debug("[ttc] |- Bean [Multi Tenant Connection Provider] Auto Configure.");
      return schemaMultiTenantConnectionProvider;
   }
}
