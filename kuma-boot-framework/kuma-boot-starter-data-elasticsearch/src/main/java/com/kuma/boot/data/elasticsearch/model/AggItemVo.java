package com.kuma.boot.data.elasticsearch.model;

public class AggItemVo {
   private String name;
   private Long value;

   public AggItemVo() {
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Long getValue() {
      return this.value;
   }

   public void setValue(Long value) {
      this.value = value;
   }
}
