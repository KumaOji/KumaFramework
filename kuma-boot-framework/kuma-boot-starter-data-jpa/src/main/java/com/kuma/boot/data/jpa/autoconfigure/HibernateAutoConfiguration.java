/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.data.jpa.autoconfigure;

import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.FORMAT_SQL;
import static org.hibernate.cfg.AvailableSettings.HIGHLIGHT_SQL;
import static org.hibernate.cfg.AvailableSettings.IMPLICIT_NAMING_STRATEGY;
import static org.hibernate.cfg.AvailableSettings.INTERCEPTOR;
import static org.hibernate.cfg.AvailableSettings.JDBC_TIME_ZONE;
import static org.hibernate.cfg.AvailableSettings.PHYSICAL_NAMING_STRATEGY;
import static org.hibernate.cfg.AvailableSettings.STATEMENT_INSPECTOR;

import cn.hutool.core.util.ReflectUtil;
//import com.infobip.spring.data.jpa.ExtendedQuerydslJpaConfiguration;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.jpa.bean.AuditorBean;
import com.kuma.boot.data.jpa.event.AnAggregateRoot;
import com.kuma.boot.data.jpa.extend.JpaExtendRepositoryFactoryBean;
import com.kuma.boot.data.jpa.autoconfigure.properties.HibernateProperties;
import com.kuma.boot.data.jpa.autoconfigure.properties.TenantProperties;
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

/**
 * HibernateAutoConfiguration
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-04 07:31:41
 */
@AutoConfiguration
@EnableJpaAuditing
//@EnableEnversRepositories
@EnableTransactionManagement
//@Import({AnAggregateRoot.class, ExtendedQuerydslJpaConfiguration.class})
@Import({AnAggregateRoot.class})
//@EnableJdbcRepositories(basePackages = {
//	"com.kuma.cloud.*.infrastructure.persistent.jdbc",
//	"com.kuma.cloud.*.infrastructure.persistent.*.jdbc"
//})
@EnableJpaRepositories(
        basePackages = {
                "com.kuma.cloud.*.biz.repository",
                "com.kuma.cloud.*.infrastructure.persistent.repository",
                "com.kuma.cloud.*.infrastructure.persistent.*.repository"
        },
        repositoryFactoryBeanClass = JpaExtendRepositoryFactoryBean.class)
@EnableConfigurationProperties({
        TenantProperties.class,
        HibernateProperties.class,
        JpaProperties.class
})
@ConditionalOnProperty(
        prefix = HibernateProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class HibernateAutoConfiguration implements InitializingBean {

   @Override
   public void afterPropertiesSet() throws Exception {
      LogUtils.started(HibernateAutoConfiguration.class, StarterNameConstants.DATA_JPA_STARTER);
   }

   // 让Spring管理JPAQueryFactory
   @Bean
   public JPAQueryFactory jpaQueryFactory(
           @Autowired(required = false) EntityManager entityManager ) {
      return new JPAQueryFactory(entityManager);
   }

   private final JpaProperties jpaProperties;
   private final HibernateProperties hibernateProperties;

   public HibernateAutoConfiguration(
           JpaProperties jpaProperties, HibernateProperties hibernateProperties ) {
      this.hibernateProperties = hibernateProperties;
      this.jpaProperties = jpaProperties;
   }

   @Bean
   public AuditorBean auditorBean() {
      return new AuditorBean();
   }

   // 用于设置JPA实现厂商的特定属性，如设置hibernate的是否自动生成DDL的属性generateDdl，这些属性是厂商特定的，因此最好在这里设置。
   // 目前spring提供HibernateJpaVendorAdapter，OpenJpaVendorAdapter，EclipseJpaVendorAdapter，
   @Bean
   public JpaVendorAdapter jpaVendorAdapter() {
      HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
      hibernateJpaVendorAdapter.setShowSql(hibernateProperties.isShowSql());
      hibernateJpaVendorAdapter.setGenerateDdl(hibernateProperties.isGenerateDdl());
      hibernateJpaVendorAdapter.setDatabase(hibernateProperties.getDatabase());
      return hibernateJpaVendorAdapter;
   }

   // 高级控制：过滤扫描的实体
   // 通过注册 ManagedClassNameFilter，可以指定仅扫描部分包中的实体：
   // @Bean
   // public ManagedClassNameFilter entityScanFilter() {
   //	return (className) -> className.startsWith("com.example.app.customer");
   // }

   // Hibernate 命名策略自定义
   // 默认情况下，Spring Boot 使用 CamelCaseToUnderscoresNamingStrategy 将类名映射为下划线分隔的小写表名。
   // 例如，TelephoneNumber 会被映射为 telephone_number。如果需要自定义，可以注册命名策略：
   // @Bean
   // public CamelCaseToUnderscoresNamingStrategy customNamingStrategy() {
   //	return new CamelCaseToUnderscoresNamingStrategy() {
   //		@Override
   //		protected boolean isCaseInsensitive(JdbcEnvironment jdbcEnvironment) {
   //			return false;
   //		}
   //	};
   // }

   // @Bean
   // @ConditionalOnBean
   // public JdbcTemplate jdbcTemplate(final DataSource dataSource) {
   //	return new JdbcTemplate(dataSource);
   // }

   @Bean
   public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
      return newJpaProperties -> {
         newJpaProperties.put(FORMAT_SQL, hibernateProperties.isFormatSql());
         newJpaProperties.put(HIGHLIGHT_SQL, hibernateProperties.isHighlightSql());

         newJpaProperties.put(
                 IMPLICIT_NAMING_STRATEGY, hibernateProperties.getImplicitNamingStrategy());
         newJpaProperties.put(
                 PHYSICAL_NAMING_STRATEGY, hibernateProperties.getPhysicalNamingStrategy());
         newJpaProperties.put(DIALECT, hibernateProperties.getDialect());
         newJpaProperties.put(JDBC_TIME_ZONE, hibernateProperties.getTimeZone());

         newJpaProperties.put(STATEMENT_INSPECTOR, hibernateProperties.getStatementInspector());
         newJpaProperties.put(INTERCEPTOR, hibernateProperties.getInterceptor());

         // 配置二级缓存Hibernate 支持二级缓存，
         // Spring Boot 允许通过 HibernatePropertiesCustomizer 轻松配置缓存提供器
         // newJpaProperties.put(CACHE_MANAGER,cacheManager.getCacheManager());
      };
   }

   /**
    * HibernateListenerConfigurer
    *
    * @author kuma
    * @version 2026.03
    * @since 2025-12-19 09:30:45
    */
   @Configuration
   public static class HibernateListenerConfigurer {

      @PersistenceUnit
      private EntityManagerFactory entityManagerFactory;

      @PostConstruct
      protected void init() {
         SessionFactoryImpl sessionFactory =
                 entityManagerFactory.unwrap(SessionFactoryImpl.class);
         EventListenerRegistry registry =
                 sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
         EventListenerGroup<MergeEventListener> eventListenerGroup =
                 registry.getEventListenerGroup(EventType.MERGE);
         // 获取所有Merge事件相关的监听器, 其中就有JpaMergeEventListener
         Iterable<MergeEventListener> listeners = eventListenerGroup.listeners();

         for (MergeEventListener listener : listeners) {
            if (listener instanceof DefaultMergeEventListener oldListener) { // 防止转换异常
               // 获取老的JpaMergeEventListener
               // 反射获取老监听器当中的CallbackRegistry, 防止AuditEventListener等叠加不进去.
               CallbackRegistry callbackRegistry =
                       (CallbackRegistry)
                               ReflectUtil.getFieldValue(oldListener, "callbackRegistry");

               // 加入CallbackRegistry, 在执行MERGE之前执行对应的Callback行为(如:
               // AuditEventListener等--@PreCreate,
               // @PreUpdate注释的监听函数)
               IgnoreNullMergeEventListener newListener =
                       new IgnoreNullMergeEventListener(callbackRegistry);
               // 清除老的监听器, 避免重复监听
               registry.getEventListenerGroup(EventType.MERGE).clearListeners();
               // 加入新的监听器
               registry.getEventListenerGroup(EventType.MERGE).prependListener(newListener);

               // for GC.
               oldListener = null;
            }
         }
      }
   }

   // @Bean
   // public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(
   //	final MultiTenantConnectionProvider multiTenantConnectionProvider,
   //	final CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
   //	return newJpaProperties -> {
   //		newJpaProperties.put(FORMAT_SQL, hibernateProperties.isFormatSql());
   //		newJpaProperties.put(HIGHLIGHT_SQL, hibernateProperties.isHighlightSql());
   //		newJpaProperties.put(MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
   //		newJpaProperties.put(MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);
   //
   //		newJpaProperties.put(IMPLICIT_NAMING_STRATEGY,
   //			hibernateProperties.getImplicitNamingStrategy());
   //		newJpaProperties.put(
   //			PHYSICAL_NAMING_STRATEGY, hibernateProperties.getPhysicalNamingStrategy());
   //		newJpaProperties.put(DIALECT, hibernateProperties.getDialect());
   //		newJpaProperties.put(JDBC_TIME_ZONE, hibernateProperties.getTimeZone());
   //
   //		newJpaProperties.put(STATEMENT_INSPECTOR, hibernateProperties.getStatementInspector());
   //		newJpaProperties.put(INTERCEPTOR, hibernateProperties.getInterceptor());
   //	};
   // }

   // @Bean
   // LocalContainerEntityManagerFactoryBean entityManagerFactory(
   //	final DataSource dataSource,
   //	final JpaVendorAdapter jpaVendorAdapter,
   //	final org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties
   // hibernateProperties1,
   //	final MultiTenantConnectionProvider multiTenantConnectionProvider,
   //	final CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
   //
   //	Supplier<String> defaultDdlMode = hibernateProperties1::getDdlAuto;
   //	Map<String, Object> newJpaProperties = hibernateProperties1.determineHibernateProperties(
   //		jpaProperties.getProperties(), new HibernateSettings().ddlAuto(defaultDdlMode));
   //
   //	// newJpaProperties.put(MULTI_TENANT, hibernateProperties.getMultiTenancy());
   //	newJpaProperties.put(FORMAT_SQL, hibernateProperties.isFormatSql());
   //	newJpaProperties.put(HIGHLIGHT_SQL, hibernateProperties.isHighlightSql());
   //	newJpaProperties.put(MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
   //	newJpaProperties.put(MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);
   //
   //	newJpaProperties.put(IMPLICIT_NAMING_STRATEGY,
   //		hibernateProperties.getImplicitNamingStrategy());
   //	newJpaProperties.put(
   //		PHYSICAL_NAMING_STRATEGY, hibernateProperties.getPhysicalNamingStrategy());
   //	newJpaProperties.put(DIALECT, hibernateProperties.getDialect());
   //	newJpaProperties.put(JDBC_TIME_ZONE, hibernateProperties.getTimeZone());
   //
   //	newJpaProperties.put(STATEMENT_INSPECTOR, hibernateProperties.getStatementInspector());
   //	newJpaProperties.put(INTERCEPTOR, hibernateProperties.getInterceptor());
   //
   //	//newJpaProperties.put(HBM2DDL_AUTO, "update");
   //
   //	final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
   //		new LocalContainerEntityManagerFactoryBean();
   //
   //	entityManagerFactoryBean.setDataSource(dataSource);
   //	entityManagerFactoryBean.setJpaPropertyMap(newJpaProperties);
   //	entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
   //
   //	entityManagerFactoryBean.setPackagesToScan(hibernateProperties.getPackages());
   //	entityManagerFactoryBean.setPersistenceUnitName(
   //		hibernateProperties.getPersistenceUnitName());
   //
   //	return entityManagerFactoryBean;
   // }

   // @Configuration
   // public static class HibernateListener implements InitializingBean {
   //
   //	@Override
   //	public void afterPropertiesSet() throws Exception {
   //		LogUtils.started(HibernateListener.class, StarterName.DATA_JPA_STARTER);
   //	}
   //
   //	@PersistenceUnit
   //	private EntityManagerFactory entityManagerFactory;
   //
   //	@PostConstruct
   //	public void registerListener() {
   //		if (entityManagerFactory != null) {
   //			SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(
   //				SessionFactoryImpl.class);
   //			EventListenerRegistry registry = sessionFactory.getServiceRegistry()
   //				.getService(EventListenerRegistry.class);
   //
   //			SaveOrUpdateListener saveOrUpdateListener = new SaveOrUpdateListener();
   //			registry.getEventListenerGroup(EventType.SAVE_UPDATE)
   //				.appendListener(saveOrUpdateListener);
   //			registry.getEventListenerGroup(EventType.SAVE)
   //				.appendListener(saveOrUpdateListener);
   //			registry.getEventListenerGroup(EventType.UPDATE)
   //				.appendListener(saveOrUpdateListener);
   //
   //			registry.getEventListenerGroup(EventType.PERSIST)
   //				.appendListener(new HibernateInspector.PersistEventListener());
   //
   //			registry.getEventListenerGroup(EventType.REFRESH)
   //				.appendListener(new HibernateInspector.RefreshEventListener());
   //
   //			registry.getEventListenerGroup(EventType.DELETE)
   //				.appendListener(new HibernateInspector.DeleteListener());
   //
   //			registry.getEventListenerGroup(EventType.LOAD)
   //				.appendListener(new HibernateInspector.LoadListener());
   //		}
   //	}
   //
   //	@Bean
   //	public HibernateDaoSupport hibernateDaoSupport() {
   //		StandardHibernateDaoSupport daoSupport = new StandardHibernateDaoSupport();
   //		daoSupport.setSessionFactory(entityManagerFactory.unwrap(SessionFactoryImpl.class));
   //		return daoSupport;
   //	}
   //
   //	public static class StandardHibernateDaoSupport extends HibernateDaoSupport {
   //
   //	}
   // }
}
