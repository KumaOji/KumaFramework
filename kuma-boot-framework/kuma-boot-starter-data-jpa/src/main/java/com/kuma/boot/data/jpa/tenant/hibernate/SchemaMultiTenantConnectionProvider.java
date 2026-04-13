package com.kuma.boot.data.jpa.tenant.hibernate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

@Component
public class SchemaMultiTenantConnectionProvider implements MultiTenantConnectionProvider, HibernatePropertiesCustomizer {
   private static final Logger log = LoggerFactory.getLogger(SchemaMultiTenantConnectionProvider.class);
   private final DataSource dataSource;

   public SchemaMultiTenantConnectionProvider(DataSource dataSource) {
      this.dataSource = dataSource;
   }

   public Connection getAnyConnection() throws SQLException {
      return this.dataSource.getConnection();
   }

   public void releaseAnyConnection(Connection connection) throws SQLException {
      connection.close();
   }

   public Connection getConnection(Object tenantIdentifier) throws SQLException {
      return null;
   }

   public void releaseConnection(Object tenantIdentifier, Connection connection) throws SQLException {
   }

   public boolean supportsAggressiveRelease() {
      return false;
   }

   public boolean isUnwrappableAs(Class<?> aClass) {
      return false;
   }

   public <T> T unwrap(Class<T> aClass) {
      return null;
   }

   public void customize(Map<String, Object> hibernateProperties) {
      hibernateProperties.put("hibernate.multi_tenant_connection_provider", this);
   }
}
