package com.kuma.boot.data.jpa.tenant;

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
import java.util.Date;

public class QBaseSysEntity extends EntityPathBase<BaseSysEntity> {
   private static final long serialVersionUID = 2040924823L;
   public static final QBaseSysEntity baseSysEntity = new QBaseSysEntity("baseSysEntity");
   public final QBaseEntity _super = new QBaseEntity(this);
   public final DateTimePath<Date> createTime;
   public final StringPath description;
   public final NumberPath<Integer> ranking;
   public final BooleanPath reserved;
   public final NumberPath<Integer> reversion;
   public final EnumPath<DataItemStatusEnum> status;
   public final DateTimePath<Date> updateTime;

   public QBaseSysEntity(String variable) {
      super(BaseSysEntity.class, PathMetadataFactory.forVariable(variable));
      this.createTime = this._super.createTime;
      this.description = this.createString("description");
      this.ranking = this._super.ranking;
      this.reserved = this.createBoolean("reserved");
      this.reversion = this.createNumber("reversion", Integer.class);
      this.status = this.createEnum("status", DataItemStatusEnum.class);
      this.updateTime = this._super.updateTime;
   }

   public QBaseSysEntity(Path<? extends BaseSysEntity> path) {
      super(path.getType(), path.getMetadata());
      this.createTime = this._super.createTime;
      this.description = this.createString("description");
      this.ranking = this._super.ranking;
      this.reserved = this.createBoolean("reserved");
      this.reversion = this.createNumber("reversion", Integer.class);
      this.status = this.createEnum("status", DataItemStatusEnum.class);
      this.updateTime = this._super.updateTime;
   }

   public QBaseSysEntity(PathMetadata metadata) {
      super(BaseSysEntity.class, metadata);
      this.createTime = this._super.createTime;
      this.description = this.createString("description");
      this.ranking = this._super.ranking;
      this.reserved = this.createBoolean("reserved");
      this.reversion = this.createNumber("reversion", Integer.class);
      this.status = this.createEnum("status", DataItemStatusEnum.class);
      this.updateTime = this._super.updateTime;
   }
}
