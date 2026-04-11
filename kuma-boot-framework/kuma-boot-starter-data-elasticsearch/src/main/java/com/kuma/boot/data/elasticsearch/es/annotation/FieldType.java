package com.kuma.boot.data.elasticsearch.es.annotation;

public enum FieldType {
   TEXT("text"),
   KEYWORD("keyword"),
   INTEGER("integer"),
   DOUBLE("double"),
   DATE("date"),
   OBJECT("object"),
   NESTED("nested");

   private String type;

   private FieldType(String type) {
      this.type = type;
   }

   public String getType() {
      return this.type;
   }

   // $FF: synthetic method
   private static FieldType[] $values() {
      return new FieldType[]{TEXT, KEYWORD, INTEGER, DOUBLE, DATE, OBJECT, NESTED};
   }
}
