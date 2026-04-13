package com.kuma.boot.data.jpa.multiple.doris;

import com.kuma.boot.data.datasource.multiple.doris.DorisDataSourceConfiguration;
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
import org.springframework.transaction.annotation.EnableTransactionManagement;

@AutoConfiguration(
   after = {DorisDataSourceConfiguration.class}
)
@ConditionalOnBean(
   value = {DataSource.class},
   name = {"dorisDruidDataSource"}
)
@ConditionalOnProperty(
   name = {"kuma.boot.data.datasource.multiple.doris.enabled"},
   havingValue = "true"
)
@EnableTransactionManagement
@EnableJpaRepositories(
   entityManagerFactoryRef = "dorisEntityManagerFactory",
   transactionManagerRef = "dorisTransactionManager",
   basePackages = {"com.kuma.cloud.*.biz.doris.repository"}
)
public class DorisJpaConfiguration {
   private final DataSource dataSource;
   private final JpaProperties jpaProperties;
   private final HibernateProperties hibernateProperties;

   public DorisJpaConfiguration(@Autowired @Qualifier("dorisDruidDataSource") DataSource dataSource, JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
      this.dataSource = dataSource;
      this.jpaProperties = jpaProperties;
      this.hibernateProperties = hibernateProperties;
   }

   @Bean(
      name = {"dorisEntityManager"}
   )
   public EntityManager dorisEntityManager(EntityManagerFactoryBuilder builder) {
      return this.dorisEntityManagerFactory(builder).getObject().createEntityManager();
   }

   @Bean(
      name = {"dorisEntityManagerFactory"}
   )
   public LocalContainerEntityManagerFactoryBean dorisEntityManagerFactory(EntityManagerFactoryBuilder builder) {
      LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
      em.setDataSource(this.dataSource);
      em.setPersistenceUnitName("dorisPersistenceUnit");
      em.setPackagesToScan(new String[]{"com.kuma.cloud.*.biz.doris.entity"});
      em.setJpaPropertyMap(this.getHibernateProperties());
      HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
      vendorAdapter.setGenerateDdl(true);
      vendorAdapter.setShowSql(true);
      em.setJpaVendorAdapter(vendorAdapter);
      return em;
   }

   @Bean(
      name = {"dorisTransactionManager"}
   )
   public PlatformTransactionManager dorisTransactionManager(EntityManagerFactoryBuilder builder) {
      return new JpaTransactionManager(this.dorisEntityManagerFactory(builder).getObject());
   }

   private Map<String, Object> getHibernateProperties() {
      return this.hibernateProperties.determineHibernateProperties(this.jpaProperties.getProperties(), new HibernateSettings());
   }
}
