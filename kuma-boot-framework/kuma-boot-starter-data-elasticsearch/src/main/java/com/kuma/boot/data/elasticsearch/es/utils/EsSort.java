package com.kuma.boot.data.elasticsearch.es.utils;

public class EsSort {
   private String field;
   private String sort;

   public EsSort() {
   }

   public String getField() {
      return this.field;
   }

   public void setField(String field) {
      this.field = field;
   }

   public String getSort() {
      return this.sort;
   }

   public void setSort(String sort) {
      this.sort = sort;
   }
}
