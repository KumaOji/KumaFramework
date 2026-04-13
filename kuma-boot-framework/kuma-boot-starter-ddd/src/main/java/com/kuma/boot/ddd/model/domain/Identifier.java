//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.model.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "Identifier",
        description = "标识"
)
public abstract class Identifier<ID> implements Entity {
   @Schema(
           name = "id",
           description = "ID"
   )
   protected ID id;

   public Identifier() {
   }

   public ID getId() {
      return this.id;
   }

   public void setId(ID id) {
      this.id = id;
   }

   public ID id() {
      return this.id;
   }

   public <T extends Identifier<ID>> T id(ID id) {
      this.id = id;
      return (T)this;
   }
}
