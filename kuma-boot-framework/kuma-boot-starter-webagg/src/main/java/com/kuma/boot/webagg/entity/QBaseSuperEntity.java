package com.kuma.boot.webagg.entity;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.StringPath;
import java.io.Serializable;
import java.time.LocalDateTime;

public class QBaseSuperEntity extends EntityPathBase<BaseSuperEntity<? extends SuperEntity<?, ? extends Serializable>, ? extends Serializable>> {
   private static final long serialVersionUID = 929639208L;
   public static final QBaseSuperEntity baseSuperEntity = new QBaseSuperEntity("baseSuperEntity");
   public final QSuperEntity _super = new QSuperEntity(this);
   public final DateTimePath<LocalDateTime> createDate = this.createDateTime("createDate", LocalDateTime.class);
   public final StringPath createUser = this.createString("createUser");
   public final BooleanPath delFlag = this.createBoolean("delFlag");
   public final StringPath extra = this.createString("extra");
   public final SimplePath<Serializable> id;
   public final DateTimePath<LocalDateTime> modifyDate;
   public final StringPath modifyUser;
   public final StringPath remark;
   public final NumberPath<Integer> version;

   public QBaseSuperEntity(String variable) {
      super(BaseSuperEntity.class, PathMetadataFactory.forVariable(variable));
      this.id = this._super.id;
      this.modifyDate = this.createDateTime("modifyDate", LocalDateTime.class);
      this.modifyUser = this.createString("modifyUser");
      this.remark = this.createString("remark");
      this.version = this.createNumber("version", Integer.class);
   }

   public QBaseSuperEntity(Path<? extends BaseSuperEntity> path) {
      super(path.getType(), path.getMetadata());
      this.id = this._super.id;
      this.modifyDate = this.createDateTime("modifyDate", LocalDateTime.class);
      this.modifyUser = this.createString("modifyUser");
      this.remark = this.createString("remark");
      this.version = this.createNumber("version", Integer.class);
   }

   public QBaseSuperEntity(PathMetadata metadata) {
      super(BaseSuperEntity.class, metadata);
      this.id = this._super.id;
      this.modifyDate = this.createDateTime("modifyDate", LocalDateTime.class);
      this.modifyUser = this.createString("modifyUser");
      this.remark = this.createString("remark");
      this.version = this.createNumber("version", Integer.class);
   }
}
