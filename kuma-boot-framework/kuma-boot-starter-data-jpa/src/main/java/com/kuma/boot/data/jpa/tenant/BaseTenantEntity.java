package com.kuma.boot.data.jpa.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseTenantEntity extends AbstractEntity {
   @Schema(
      name = "租户ID",
      description = "Partitioned 类型租户ID"
   )
   @Column(
      name = "tenant_id",
      length = 20
   )
   private String tenantId = "public";

   public BaseTenantEntity() {
   }
}
