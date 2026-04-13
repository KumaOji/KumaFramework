package com.kuma.boot.data.jpa.autoconfigure.properties;

import com.kuma.boot.data.jpa.listener.HibernateInspector;
import com.kuma.boot.data.jpa.listener.HibernateInterceptor;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;
import org.hibernate.dialect.MySQLDialect;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.hibernate.SpringImplicitNamingStrategy;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.orm.jpa.vendor.Database;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.data.jpa"
)
public class HibernateProperties {
   public static final String PREFIX = "kuma.boot.data.jpa";
   private Boolean enabled = true;
   private boolean showSql = true;
   private boolean generateDdl = true;
   private Database database;
   private boolean formatSql;
   private boolean highlightSql;
   private String implicitNamingStrategy;
   private String physicalNamingStrategy;
   private String dialect;
   private String timeZone;
   private String statementInspector;
   private String interceptor;
   private String persistenceUnitName;
   private String packages;

   public HibernateProperties() {
      this.database = Database.MYSQL;
      this.formatSql = true;
      this.highlightSql = true;
      this.implicitNamingStrategy = SpringImplicitNamingStrategy.class.getName();
      this.physicalNamingStrategy = PhysicalNamingStrategySnakeCaseImpl.class.getName();
      this.dialect = MySQLDialect.class.getName();
      this.timeZone = "Asia/Shanghai";
      this.statementInspector = HibernateInspector.class.getName();
      this.interceptor = HibernateInterceptor.class.getName();
      this.persistenceUnitName = "default";
      this.packages = "com.kuma.cloud.*.biz.model.entity";
   }

   public Boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }

   public String getPackages() {
      return this.packages;
   }

   public void setPackages(String packages) {
      this.packages = packages;
   }

   public boolean isShowSql() {
      return this.showSql;
   }

   public void setShowSql(boolean showSql) {
      this.showSql = showSql;
   }

   public boolean isGenerateDdl() {
      return this.generateDdl;
   }

   public void setGenerateDdl(boolean generateDdl) {
      this.generateDdl = generateDdl;
   }

   public Database getDatabase() {
      return this.database;
   }

   public void setDatabase(Database database) {
      this.database = database;
   }

   public boolean isFormatSql() {
      return this.formatSql;
   }

   public void setFormatSql(boolean formatSql) {
      this.formatSql = formatSql;
   }

   public boolean isHighlightSql() {
      return this.highlightSql;
   }

   public void setHighlightSql(boolean highlightSql) {
      this.highlightSql = highlightSql;
   }

   public String getImplicitNamingStrategy() {
      return this.implicitNamingStrategy;
   }

   public void setImplicitNamingStrategy(String implicitNamingStrategy) {
      this.implicitNamingStrategy = implicitNamingStrategy;
   }

   public String getPhysicalNamingStrategy() {
      return this.physicalNamingStrategy;
   }

   public void setPhysicalNamingStrategy(String physicalNamingStrategy) {
      this.physicalNamingStrategy = physicalNamingStrategy;
   }

   public String getDialect() {
      return this.dialect;
   }

   public void setDialect(String dialect) {
      this.dialect = dialect;
   }

   public String getTimeZone() {
      return this.timeZone;
   }

   public void setTimeZone(String timeZone) {
      this.timeZone = timeZone;
   }

   public String getStatementInspector() {
      return this.statementInspector;
   }

   public void setStatementInspector(String statementInspector) {
      this.statementInspector = statementInspector;
   }

   public String getInterceptor() {
      return this.interceptor;
   }

   public void setInterceptor(String interceptor) {
      this.interceptor = interceptor;
   }

   public String getPersistenceUnitName() {
      return this.persistenceUnitName;
   }

   public void setPersistenceUnitName(String persistenceUnitName) {
      this.persistenceUnitName = persistenceUnitName;
   }
}
