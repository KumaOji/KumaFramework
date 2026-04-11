package com.kuma.boot.webagg.entity;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.SimplePath;
import java.io.Serializable;

public class QSuperEntity extends EntityPathBase<SuperEntity<?, ? extends Serializable>> {
   private static final long serialVersionUID = -550093767L;
   public static final QSuperEntity superEntity = new QSuperEntity("superEntity");
   public final SimplePath<Serializable> id = this.createSimple("id", Serializable.class);

   public QSuperEntity(String variable) {
      super(SuperEntity.class, PathMetadataFactory.forVariable(variable));
   }

   public QSuperEntity(Path<? extends SuperEntity> path) {
      super(path.getType(), path.getMetadata());
   }

   public QSuperEntity(PathMetadata metadata) {
      super(SuperEntity.class, metadata);
   }
}
