package com.kuma.boot.data.jpa.simplestjpa.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(
   prefix = "tenant"
)
public class TenantProperties {
   private List<String> tables = new ArrayList();
   private String tenantIdColumn = "tenant_id";
   private Boolean enableTenant;
   private Boolean customTenant;

   public TenantProperties() {
      this.enableTenant = Boolean.FALSE;
      this.customTenant = Boolean.FALSE;
   }

   public List<String> getTables() {
      return this.tables;
   }

   public void setTables(List<String> tables) {
      this.tables = tables;
   }

   public String getTenantIdColumn() {
      return this.tenantIdColumn;
   }

   public void setTenantIdColumn(String tenantIdColumn) {
      this.tenantIdColumn = tenantIdColumn;
   }

   public Boolean getEnableTenant() {
      return this.enableTenant;
   }

   public void setEnableTenant(Boolean enableTenant) {
      this.enableTenant = enableTenant;
   }

   public Boolean getCustomTenant() {
      return this.customTenant;
   }

   public void setCustomTenant(Boolean customTenant) {
      this.customTenant = customTenant;
   }
}
