package com.kuma.boot.data.jpa.tenant.datasource;

import com.kuma.boot.data.jpa.tenant.entity.SysTenantDataSource;
import com.kuma.boot.data.jpa.tenant.repository.SysTenantDataSourceRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

@Component
public class MultiTenantDataSourceFactory {
   @Autowired
   private SysTenantDataSourceRepository sysTenantDataSourceRepository;

   public MultiTenantDataSourceFactory() {
   }

   private DataSource createDataSource(DataSource defaultDataSource, SysTenantDataSource sysTenantDataSource) {
      if (defaultDataSource instanceof HikariDataSource defaultHikariDataSource) {
         Properties defaultDataSourceProperties = defaultHikariDataSource.getDataSourceProperties();
         HikariConfig hikariConfig = new HikariConfig();
         hikariConfig.setDriverClassName(sysTenantDataSource.getDriverClassName());
         hikariConfig.setJdbcUrl(sysTenantDataSource.getUrl());
         hikariConfig.setUsername(sysTenantDataSource.getUsername());
         hikariConfig.setPassword(sysTenantDataSource.getPassword());
         if (ObjectUtils.isNotEmpty(defaultDataSource)) {
            defaultDataSourceProperties.forEach((key, value) -> hikariConfig.addDataSourceProperty(String.valueOf(key), value));
         }

         return new HikariDataSource(hikariConfig);
      } else {
         return DataSourceBuilder.create().type(HikariDataSource.class).url(sysTenantDataSource.getUrl()).driverClassName(sysTenantDataSource.getDriverClassName()).username(sysTenantDataSource.getUsername()).password(sysTenantDataSource.getPassword()).build();
      }
   }

   public Map<String, DataSource> getAll(DataSource defaultDataSource) {
      List<SysTenantDataSource> sysTenantDataSources = this.sysTenantDataSourceRepository.findAll();
      return (Map<String, DataSource>)(CollectionUtils.isNotEmpty(sysTenantDataSources) ? (Map)sysTenantDataSources.stream().collect(Collectors.toMap(SysTenantDataSource::getTenantId, (value) -> this.createDataSource(defaultDataSource, value))) : new HashMap());
   }
}
