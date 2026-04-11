package com.kuma.boot.webagg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public class SuperEntity<T extends SuperEntity<T, I>, I extends Serializable> extends Model<T> implements Serializable {
   private static final long serialVersionUID = -4603650115461757622L;
   @Id
   @GeneratedValue(
      strategy = GenerationType.IDENTITY
   )
   @Column(
      name = "`id`",
      updatable = false,
      columnDefinition = "bigint comment 'id'"
   )
   @TableId(
      value = "id",
      type = IdType.AUTO
   )
   private I id;

   public SuperEntity() {
   }

   public SuperEntity(I id) {
      this.id = id;
   }

   public I getId() {
      return this.id;
   }

   public void setId(I id) {
      this.id = id;
   }

   public I id() {
      return this.id;
   }

   public T id(I id) {
      this.id = id;
      return (T)this;
   }
}
