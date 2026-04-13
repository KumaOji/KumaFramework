package com.kuma.boot.data.jpa.tenant.entity;

import com.google.common.base.MoreObjects;
import com.kuma.boot.data.jpa.tenant.BaseSysEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.UuidGenerator;

@Schema(
   title = "\u591a\u79df\u6237\u6570\u636e\u6e90\u7ba1\u7406"
)
@Entity
@Table(
   name = "sys_tenant_datasource",
   uniqueConstraints = {@UniqueConstraint(
   columnNames = {"tenant_id"}
)},
   indexes = {@Index(
   name = "sys_tenant_datasource_id_idx",
   columnList = "datasource_id"
)}
)
@Cacheable
@Cache(
   usage = CacheConcurrencyStrategy.READ_WRITE,
   region = "data:core:sys:tenant:datasource"
)
public class SysTenantDataSource extends BaseSysEntity {
   @Schema(
      name = "\u79df\u6237\u6570\u636e\u6e90\u4e3b\u952e"
   )
   @Id
   @UuidGenerator
   @Column(
      name = "datasource_id",
      length = 64
   )
   private String datasourceId;
   @Schema(
      name = "\u79df\u6237ID",
      description = "\u79df\u6237\u7684\u552f\u4e00\u6807\u8bc6"
   )
   @Column(
      name = "tenant_id",
      length = 64,
      unique = true
   )
   private String tenantId;
   @Schema(
      name = "\u6570\u636e\u5e93\u7528\u6237\u540d"
   )
   @Column(
      name = "username",
      length = 100
   )
   private String username;
   @Schema(
      name = "\u6570\u636e\u5e93\u5bc6\u7801"
   )
   @Column(
      name = "password",
      length = 100
   )
   private String password;
   @Schema(
      name = "\u6570\u636e\u5e93\u9a71\u52a8"
   )
   @Column(
      name = "driver_class_name",
      length = 64
   )
   private String driverClassName;
   @Schema(
      name = "\u6570\u636e\u5e93\u8fde\u63a5"
   )
   @Column(
      name = "url",
      length = 1000
   )
   private String url;
   @Schema(
      name = "\u662f\u5426\u5df2\u7ecf\u521d\u59cb\u5316",
      description = "\u9ed8\u8ba4\u503c false"
   )
   private Boolean initialize = false;

   public SysTenantDataSource() {
   }

   public String getDatasourceId() {
      return this.datasourceId;
   }

   public void setDatasourceId(String datasourceId) {
      this.datasourceId = datasourceId;
   }

   public String getTenantId() {
      return this.tenantId;
   }

   public void setTenantId(String tenantId) {
      this.tenantId = tenantId;
   }

   public String getUsername() {
      return this.username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return this.password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getDriverClassName() {
      return this.driverClassName;
   }

   public void setDriverClassName(String driverClassName) {
      this.driverClassName = driverClassName;
   }

   public String getUrl() {
      return this.url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public Boolean getInitialize() {
      return this.initialize;
   }

   public void setInitialize(Boolean initialize) {
      this.initialize = initialize;
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("datasourceId", this.datasourceId).add("tenantId", this.tenantId).add("username", this.username).add("password", this.password).add("driverClassName", this.driverClassName).add("url", this.url).add("initialize", this.initialize).toString();
   }
}
