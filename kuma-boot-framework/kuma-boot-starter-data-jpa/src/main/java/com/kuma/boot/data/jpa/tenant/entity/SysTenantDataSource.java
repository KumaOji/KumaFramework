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
   title = "多租户数据源管理"
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
      name = "租户数据源主键"
   )
   @Id
   @UuidGenerator
   @Column(
      name = "datasource_id",
      length = 64
   )
   private String datasourceId;
   @Schema(
      name = "租户ID",
      description = "租户的唯一标识"
   )
   @Column(
      name = "tenant_id",
      length = 64,
      unique = true
   )
   private String tenantId;
   @Schema(
      name = "数据库用户名"
   )
   @Column(
      name = "username",
      length = 100
   )
   private String username;
   @Schema(
      name = "数据库密码"
   )
   @Column(
      name = "password",
      length = 100
   )
   private String password;
   @Schema(
      name = "数据库驱动"
   )
   @Column(
      name = "driver_class_name",
      length = 64
   )
   private String driverClassName;
   @Schema(
      name = "数据库连接"
   )
   @Column(
      name = "url",
      length = 1000
   )
   private String url;
   @Schema(
      name = "是否已经初始化",
      description = "默认值 false"
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
