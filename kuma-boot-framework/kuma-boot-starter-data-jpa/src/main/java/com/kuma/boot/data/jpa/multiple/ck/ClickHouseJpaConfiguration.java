package com.kuma.boot.data.jpa.multiple.ck;

import com.kuma.boot.data.datasource.multiple.ck.ClickHouseDataSourceConfiguration;
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
   after = {ClickHouseDataSourceConfiguration.class}
)
@ConditionalOnBean(
   value = {DataSource.class},
   name = {"clickHouseDruidDataSource"}
)
@ConditionalOnProperty(
   name = {"kuma.boot.data.datasource.multiple.clickhouse.enabled"},
   havingValue = "true"
)
@EnableTransactionManagement
@EnableJpaRepositories(
   entityManagerFactoryRef = "clickHouseEntityManagerFactory",
   transactionManagerRef = "clickHouseTransactionManager",
   basePackages = {"com.kuma.cloud.*.biz.clickhouse.repository"}
)
public class ClickHouseJpaConfiguration {
   private final DataSource dataSource;
   private final JpaProperties jpaProperties;
   private final HibernateProperties hibernateProperties;

   public ClickHouseJpaConfiguration(@Autowired @Qualifier("clickHouseDruidDataSource") DataSource dataSource, JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
      this.dataSource = dataSource;
      this.jpaProperties = jpaProperties;
      this.hibernateProperties = hibernateProperties;
   }

   @Bean(
      name = {"clickHouseEntityManager"}
   )
   public EntityManager clickHouseEntityManager(EntityManagerFactoryBuilder builder) {
      return this.clickHouseEntityManagerFactory(builder).getObject().createEntityManager();
   }

   @Bean(
      name = {"clickHouseEntityManagerFactory"}
   )
   public LocalContainerEntityManagerFactoryBean clickHouseEntityManagerFactory(EntityManagerFactoryBuilder builder) {
      LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
      em.setDataSource(this.dataSource);
      em.setPersistenceUnitName("clickHousePersistenceUnit");
      em.setPackagesToScan(new String[]{"com.kuma.cloud.*.biz.clickhouse.entity"});
      em.setJpaPropertyMap(this.getHibernateProperties());
      HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
      vendorAdapter.setGenerateDdl(true);
      vendorAdapter.setShowSql(true);
      em.setJpaVendorAdapter(vendorAdapter);
      return em;
   }

   @Bean(
      name = {"clickHouseTransactionManager"}
   )
   public PlatformTransactionManager clickHouseTransactionManager(EntityManagerFactoryBuilder builder) {
      return new JpaTransactionManager(this.clickHouseEntityManagerFactory(builder).getObject());
   }

   private Map<String, Object> getHibernateProperties() {
      return this.hibernateProperties.determineHibernateProperties(this.jpaProperties.getProperties(), new HibernateSettings());
   }
}
