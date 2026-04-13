package com.kuma.boot.data.jpa.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseTenantEntity extends AbstractEntity {
   @Schema(
      name = "\u79df\u6237ID",
      description = "Partitioned \u7c7b\u578b\u79df\u6237ID"
   )
   @Column(
      name = "tenant_id",
      length = 20
   )
   private String tenantId = "public";

   public BaseTenantEntity() {
   }
}
