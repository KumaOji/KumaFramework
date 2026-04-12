package com.kuma.boot.ddd.model.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   name = "DomainEntity",
   description = "标识"
)
public abstract class DomainEntity implements Entity {
   @Schema(
      name = "id",
      description = "ID"
   )
   protected Object id;

   public Object getId() {
      return this.id;
   }

   public void setId(Object id) {
      this.id = id;
   }

   public Object id() {
      return this.id;
   }

   public DomainEntity id(Object id) {
      this.id = id;
      return this;
   }
}
