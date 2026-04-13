package com.kuma.boot.data.jpa.tenant.entity;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.kuma.boot.common.enums.DataItemStatusEnum;
import com.kuma.boot.data.jpa.tenant.QBaseSysEntity;
import java.util.Date;

public class QSysTenantDataSource extends EntityPathBase<SysTenantDataSource> {
   private static final long serialVersionUID = -1674281863L;
   public static final QSysTenantDataSource sysTenantDataSource = new QSysTenantDataSource("sysTenantDataSource");
   public final QBaseSysEntity _super = new QBaseSysEntity(this);
   public final DateTimePath<Date> createTime;
   public final StringPath datasourceId;
   public final StringPath description;
   public final StringPath driverClassName;
   public final BooleanPath initialize;
   public final StringPath password;
   public final NumberPath<Integer> ranking;
   public final BooleanPath reserved;
   public final NumberPath<Integer> reversion;
   public final EnumPath<DataItemStatusEnum> status;
   public final StringPath tenantId;
   public final DateTimePath<Date> updateTime;
   public final StringPath url;
   public final StringPath username;

   public QSysTenantDataSource(String variable) {
      super(SysTenantDataSource.class, PathMetadataFactory.forVariable(variable));
      this.createTime = this._super.createTime;
      this.datasourceId = this.createString("datasourceId");
      this.description = this._super.description;
      this.driverClassName = this.createString("driverClassName");
      this.initialize = this.createBoolean("initialize");
      this.password = this.createString("password");
      this.ranking = this._super.ranking;
      this.reserved = this._super.reserved;
      this.reversion = this._super.reversion;
      this.status = this._super.status;
      this.tenantId = this.createString("tenantId");
      this.updateTime = this._super.updateTime;
      this.url = this.createString("url");
      this.username = this.createString("username");
   }

   public QSysTenantDataSource(Path<? extends SysTenantDataSource> path) {
      super(path.getType(), path.getMetadata());
      this.createTime = this._super.createTime;
      this.datasourceId = this.createString("datasourceId");
      this.description = this._super.description;
      this.driverClassName = this.createString("driverClassName");
      this.initialize = this.createBoolean("initialize");
      this.password = this.createString("password");
      this.ranking = this._super.ranking;
      this.reserved = this._super.reserved;
      this.reversion = this._super.reversion;
      this.status = this._super.status;
      this.tenantId = this.createString("tenantId");
      this.updateTime = this._super.updateTime;
      this.url = this.createString("url");
      this.username = this.createString("username");
   }

   public QSysTenantDataSource(PathMetadata metadata) {
      super(SysTenantDataSource.class, metadata);
      this.createTime = this._super.createTime;
      this.datasourceId = this.createString("datasourceId");
      this.description = this._super.description;
      this.driverClassName = this.createString("driverClassName");
      this.initialize = this.createBoolean("initialize");
      this.password = this.createString("password");
      this.ranking = this._super.ranking;
      this.reserved = this._super.reserved;
      this.reversion = this._super.reversion;
      this.status = this._super.status;
      this.tenantId = this.createString("tenantId");
      this.updateTime = this._super.updateTime;
      this.url = this.createString("url");
      this.username = this.createString("username");
   }
}
