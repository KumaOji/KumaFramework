//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.data.jpa.base.entity;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import java.io.Serializable;
import java.time.LocalDateTime;

public class QJpaSuperEntity extends EntityPathBase<JpaSuperEntity<? extends Serializable>> {
   private static final long serialVersionUID = -586916531L;
   public static final QJpaSuperEntity jpaSuperEntity = new QJpaSuperEntity("jpaSuperEntity");
   public final DateTimePath<LocalDateTime> createDate = this.createDateTime("createDate", LocalDateTime.class);
   public final StringPath createUser = this.createString("createUser");
   public final BooleanPath delFlag = this.createBoolean("delFlag");
   public final NumberPath<Long> id = this.createNumber("id", Long.class);
   public final DateTimePath<LocalDateTime> modifyDate = this.createDateTime("modifyDate", LocalDateTime.class);
   public final StringPath modifyUser = this.createString("modifyUser");
   public final NumberPath<Integer> version = this.createNumber("version", Integer.class);

   public QJpaSuperEntity(String variable) {
      super((Class<? extends JpaSuperEntity<? extends Serializable>>) JpaSuperEntity.class, PathMetadataFactory.forVariable(variable));
   }

   public QJpaSuperEntity(Path<? extends JpaSuperEntity> path) {
      super((Class<? extends JpaSuperEntity<? extends Serializable>>) path.getType(), path.getMetadata());
   }

   public QJpaSuperEntity(PathMetadata metadata) {
      super((Class<? extends JpaSuperEntity<? extends Serializable>>) JpaSuperEntity.class, metadata);
   }
}
