package com.kuma.boot.data.jpa.autoconfigure;

import cn.hutool.core.util.ReflectUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.jpa.autoconfigure.properties.HibernateProperties;
import com.kuma.boot.data.jpa.autoconfigure.properties.TenantProperties;
import com.kuma.boot.data.jpa.bean.AuditorBean;
import com.kuma.boot.data.jpa.event.AnAggregateRoot;
import com.kuma.boot.data.jpa.extend.JpaExtendRepositoryFactoryBean;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.hibernate.event.internal.DefaultMergeEventListener;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.MergeEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.jpa.event.spi.CallbackRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@AutoConfiguration
@EnableJpaAuditing
@EnableTransactionManagement
@Import({AnAggregateRoot.class})
@EnableJpaRepositories(
   basePackages = {"com.kuma.cloud.*.biz.repository", "com.kuma.cloud.*.infrastructure.persistent.repository", "com.kuma.cloud.*.infrastructure.persistent.*.repository"},
   repositoryFactoryBeanClass = JpaExtendRepositoryFactoryBean.class
)
@EnableConfigurationProperties({TenantProperties.class, HibernateProperties.class, JpaProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.data.jpa",
   name = {"enabled"},
   havingValue = "true",
   matchIfMissing = true
)
public class HibernateAutoConfiguration implements InitializingBean {
   private final JpaProperties jpaProperties;
   private final HibernateProperties hibernateProperties;

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(HibernateAutoConfiguration.class, "kuma-boot-starter-data-jpa", new String[0]);
   }

   @Bean
   public JPAQueryFactory jpaQueryFactory(@Autowired(required = false) EntityManager entityManager) {
      return new JPAQueryFactory(entityManager);
   }

   public HibernateAutoConfiguration(JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
      this.hibernateProperties = hibernateProperties;
      this.jpaProperties = jpaProperties;
   }

   @Bean
   public AuditorBean auditorBean() {
      return new AuditorBean();
   }

   @Bean
   public JpaVendorAdapter jpaVendorAdapter() {
      HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
      hibernateJpaVendorAdapter.setShowSql(this.hibernateProperties.isShowSql());
      hibernateJpaVendorAdapter.setGenerateDdl(this.hibernateProperties.isGenerateDdl());
      hibernateJpaVendorAdapter.setDatabase(this.hibernateProperties.getDatabase());
      return hibernateJpaVendorAdapter;
   }

   @Bean
   public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
      return (newJpaProperties) -> {
         newJpaProperties.put("hibernate.format_sql", this.hibernateProperties.isFormatSql());
         newJpaProperties.put("hibernate.highlight_sql", this.hibernateProperties.isHighlightSql());
         newJpaProperties.put("hibernate.implicit_naming_strategy", this.hibernateProperties.getImplicitNamingStrategy());
         newJpaProperties.put("hibernate.physical_naming_strategy", this.hibernateProperties.getPhysicalNamingStrategy());
         newJpaProperties.put("hibernate.dialect", this.hibernateProperties.getDialect());
         newJpaProperties.put("hibernate.jdbc.time_zone", this.hibernateProperties.getTimeZone());
         newJpaProperties.put("hibernate.session_factory.statement_inspector", this.hibernateProperties.getStatementInspector());
         newJpaProperties.put("hibernate.session_factory.interceptor", this.hibernateProperties.getInterceptor());
      };
   }

   @Configuration
   public static class HibernateListenerConfigurer {
      @PersistenceUnit
      private EntityManagerFactory entityManagerFactory;

      public HibernateListenerConfigurer() {
      }

      @PostConstruct
      protected void init() {
         SessionFactoryImpl sessionFactory = (SessionFactoryImpl)this.entityManagerFactory.unwrap(SessionFactoryImpl.class);
         EventListenerRegistry registry = (EventListenerRegistry)sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
         EventListenerGroup<MergeEventListener> eventListenerGroup = registry.getEventListenerGroup(EventType.MERGE);

         for(MergeEventListener listener : eventListenerGroup.listeners()) {
            if (listener instanceof DefaultMergeEventListener oldListener) {
               CallbackRegistry callbackRegistry = (CallbackRegistry)ReflectUtil.getFieldValue(oldListener, "callbackRegistry");
               IgnoreNullMergeEventListener newListener = new IgnoreNullMergeEventListener(callbackRegistry);
               registry.getEventListenerGroup(EventType.MERGE).clearListeners();
               registry.getEventListenerGroup(EventType.MERGE).prependListener(newListener);
               Object var10 = null;
            }
         }

      }
   }
}
