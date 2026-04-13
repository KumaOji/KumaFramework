package com.kuma.boot.data.jpa.tenant.hibernate;

import cn.hutool.extra.spring.SpringUtil;
import com.kuma.boot.data.jpa.tenant.datasource.MultiTenantDataSourceFactory;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;

public class DatabaseMultiTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<Object> implements HibernatePropertiesCustomizer {
   private static final Logger log = LoggerFactory.getLogger(DatabaseMultiTenantConnectionProvider.class);
   private boolean isDataSourceInit = false;
   private final Map<String, DataSource> dataSources = new HashMap();
   private final DataSource defaultDataSource;

   public DatabaseMultiTenantConnectionProvider(DataSource dataSource) {
      this.defaultDataSource = dataSource;
      this.dataSources.put("public", dataSource);
   }

   private void initialize() {
      this.isDataSourceInit = true;
      MultiTenantDataSourceFactory factory = (MultiTenantDataSourceFactory)SpringUtil.getBean(MultiTenantDataSourceFactory.class);
      this.dataSources.putAll(factory.getAll(this.defaultDataSource));
   }

   protected DataSource selectAnyDataSource() {
      log.debug("[ttc] |- Select any dataSource: " + String.valueOf(this.defaultDataSource));
      return this.defaultDataSource;
   }

   protected DataSource selectDataSource(Object tenantIdentifier) {
      if (!this.isDataSourceInit) {
         this.initialize();
      }

      DataSource currentDataSource = (DataSource)this.dataSources.get(tenantIdentifier.toString());
      if (ObjectUtils.isNotEmpty(currentDataSource)) {
         log.debug("[ttc] |- Found the dataSource of [{}]", tenantIdentifier);
         return currentDataSource;
      } else {
         log.warn("[ttc] |- Cannot found the dataSource for [{}], change to use default.", tenantIdentifier);
         return this.defaultDataSource;
      }
   }

   public void customize(Map<String, Object> hibernateProperties) {
      hibernateProperties.put("hibernate.multi_tenant_connection_provider", this);
   }
}
