package com.kuma.boot.data.jpa.tenant;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringPath;

public class QBaseTenantEntity extends EntityPathBase<BaseTenantEntity> {
   private static final long serialVersionUID = -75020730L;
   public static final QBaseTenantEntity baseTenantEntity = new QBaseTenantEntity("baseTenantEntity");
   public final StringPath tenantId = this.createString("tenantId");

   public QBaseTenantEntity(String variable) {
      super(BaseTenantEntity.class, PathMetadataFactory.forVariable(variable));
   }

   public QBaseTenantEntity(Path<? extends BaseTenantEntity> path) {
      super(path.getType(), path.getMetadata());
   }

   public QBaseTenantEntity(PathMetadata metadata) {
      super(BaseTenantEntity.class, metadata);
   }
}
