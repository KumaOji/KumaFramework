package com.kuma.boot.ddd.model.types;

public class UserId {
   private Long id;

   public UserId(Long id) {
      this.id = id;
   }

   public Long getId() {
      return this.id;
   }

   public void setId(Long id) {
      this.id = id;
   }
}
