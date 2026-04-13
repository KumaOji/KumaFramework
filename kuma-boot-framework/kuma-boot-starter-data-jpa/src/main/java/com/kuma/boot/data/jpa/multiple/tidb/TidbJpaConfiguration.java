package com.kuma.boot.data.jpa.multiple.tidb;

import com.kuma.boot.data.datasource.multiple.tidb.TidbDataSourceConfiguration;
import jakarta.persistence.EntityManager;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.hibernate.autoconfigure.HibernateProperties;
import org.springframework.boot.hibernate.autoconfigure.HibernateSettings;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@AutoConfiguration(
   after = {TidbDataSourceConfiguration.class}
)
@ConditionalOnBean(
   value = {DataSource.class},
   name = {"tidbDruidDataSource"}
)
@ConditionalOnProperty(
   name = {"kuma.boot.data.datasource.multiple.tidb.enabled"},
   havingValue = "true"
)
@EnableJpaRepositories(
   entityManagerFactoryRef = "tidbEntityManagerFactory",
   transactionManagerRef = "tidbTransactionManager",
   basePackages = {"com.kuma.cloud.*.biz.tidb.repository"}
)
public class TidbJpaConfiguration {
   private final DataSource dataSource;
   private final JpaProperties jpaProperties;
   private final HibernateProperties hibernateProperties;

   public TidbJpaConfiguration(@Autowired @Qualifier("tidbDruidDataSource") DataSource dataSource, JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
      this.dataSource = dataSource;
      this.jpaProperties = jpaProperties;
      this.hibernateProperties = hibernateProperties;
   }

   @Bean(
      name = {"tidbEntityManager"}
   )
   public EntityManager tidbEntityManager(EntityManagerFactoryBuilder builder) {
      return this.tidbEntityManagerFactory(builder).getObject().createEntityManager();
   }

   @Bean(
      name = {"tidbEntityManagerFactory"}
   )
   public LocalContainerEntityManagerFactoryBean tidbEntityManagerFactory(EntityManagerFactoryBuilder builder) {
      LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
      em.setDataSource(this.dataSource);
      em.setPersistenceUnitName("tidbPersistenceUnit");
      em.setPackagesToScan(new String[]{"com.kuma.cloud.*.biz.tidb.entity"});
      em.setJpaPropertyMap(this.getHibernateProperties());
      HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
      vendorAdapter.setGenerateDdl(true);
      vendorAdapter.setShowSql(true);
      em.setJpaVendorAdapter(vendorAdapter);
      return em;
   }

   @Bean(
      name = {"tidbTransactionManager"}
   )
   public PlatformTransactionManager tidbTransactionManager(EntityManagerFactoryBuilder builder) {
      return new JpaTransactionManager(this.tidbEntityManagerFactory(builder).getObject());
   }

   private Map<String, Object> getHibernateProperties() {
      return this.hibernateProperties.determineHibernateProperties(this.jpaProperties.getProperties(), new HibernateSettings());
   }
}
