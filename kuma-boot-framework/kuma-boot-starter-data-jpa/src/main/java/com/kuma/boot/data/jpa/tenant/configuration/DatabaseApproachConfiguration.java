package com.kuma.boot.data.jpa.tenant.configuration;

import com.kuma.boot.data.jpa.tenant.annotation.ConditionalOnDatabaseApproach;
import com.kuma.boot.data.jpa.tenant.datasource.MultiTenantDataSourceFactory;
import com.kuma.boot.data.jpa.tenant.hibernate.DatabaseMultiTenantConnectionProvider;
import com.kuma.boot.data.jpa.tenant.properties.MultiTenantProperties;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.hibernate.autoconfigure.HibernateProperties;
import org.springframework.boot.hibernate.autoconfigure.HibernateSettings;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration(
   proxyBeanMethods = false
)
@ConditionalOnDatabaseApproach
@EnableTransactionManagement
@EntityScan(
   basePackages = {"cn.herodotus.engine.data.tenant.entity"}
)
@EnableJpaRepositories(
   basePackages = {"cn.herodotus.engine.data.tenant.repository"}
)
public class DatabaseApproachConfiguration {
   private static final Logger log = LoggerFactory.getLogger(DatabaseApproachConfiguration.class);

   public DatabaseApproachConfiguration() {
   }

   @PostConstruct
   public void postConstruct() {
      log.debug("[ttc] |- SDK [Database Approach] Auto Configure.");
   }

   @Bean
   public MultiTenantConnectionProvider multiTenantConnectionProvider(DataSource dataSource) {
      DatabaseMultiTenantConnectionProvider herodotusTenantConnectionProvider = new DatabaseMultiTenantConnectionProvider(dataSource);
      log.debug("[ttc] |- Bean [Multi Tenant Connection Provider] Auto Configure.");
      return herodotusTenantConnectionProvider;
   }

   @Primary
   @Bean
   public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, HibernateProperties hibernateProperties, JpaVendorAdapter jpaVendorAdapter, JpaProperties jpaProperties, MultiTenantProperties multiTenantProperties, MultiTenantConnectionProvider multiTenantConnectionProvider, CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
      Objects.requireNonNull(hibernateProperties);
      Supplier<String> defaultDdlMode = hibernateProperties::getDdlAuto;
      Map<String, Object> properties = hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), (new HibernateSettings()).ddlAuto(defaultDdlMode));
      properties.put("hibernate.multi_tenant_connection_provider", multiTenantConnectionProvider);
      properties.put("hibernate.tenant_identifier_resolver", currentTenantIdentifierResolver);
      LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
      entityManagerFactory.setDataSource(dataSource);
      entityManagerFactory.setPackagesToScan(multiTenantProperties.getPackageToScan());
      entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter);
      entityManagerFactory.setJpaPropertyMap(properties);
      return entityManagerFactory;
   }

   @Primary
   @Bean
   @ConditionalOnClass({LocalContainerEntityManagerFactoryBean.class})
   public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
      return new JpaTransactionManager((EntityManagerFactory)Objects.requireNonNull(entityManagerFactory.getObject()));
   }

   @Bean
   @ConditionalOnClass({LocalContainerEntityManagerFactoryBean.class})
   public MultiTenantDataSourceFactory multiTenantDataSourceFactory() {
      MultiTenantDataSourceFactory multiTenantDataSourceFactory = new MultiTenantDataSourceFactory();
      log.debug("[ttc] |- Bean [Multi Tenant DataSource Factory] Auto Configure.");
      return multiTenantDataSourceFactory;
   }
}
