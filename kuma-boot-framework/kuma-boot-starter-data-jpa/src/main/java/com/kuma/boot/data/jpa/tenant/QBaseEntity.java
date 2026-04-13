package com.kuma.boot.data.jpa.tenant;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import java.util.Date;

public class QBaseEntity extends EntityPathBase<BaseEntity> {
   private static final long serialVersionUID = 663232444L;
   public static final QBaseEntity baseEntity = new QBaseEntity("baseEntity");
   public final DateTimePath<Date> createTime = this.createDateTime("createTime", Date.class);
   public final NumberPath<Integer> ranking = this.createNumber("ranking", Integer.class);
   public final DateTimePath<Date> updateTime = this.createDateTime("updateTime", Date.class);

   public QBaseEntity(String variable) {
      super(BaseEntity.class, PathMetadataFactory.forVariable(variable));
   }

   public QBaseEntity(Path<? extends BaseEntity> path) {
      super(path.getType(), path.getMetadata());
   }

   public QBaseEntity(PathMetadata metadata) {
      super(BaseEntity.class, metadata);
   }
}
