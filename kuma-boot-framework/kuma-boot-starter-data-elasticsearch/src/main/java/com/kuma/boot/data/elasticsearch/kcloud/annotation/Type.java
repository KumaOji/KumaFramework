package com.kuma.boot.data.elasticsearch.kcloud.annotation;

public enum Type {
   AUTO("auto"),
   TEXT("text"),
   KEYWORD("keyword"),
   LONG("long"),
   INTEGER("integer"),
   SHORT("short"),
   BYTE("byte"),
   DOUBLE("double"),
   FLOAT("float"),
   DATE("date"),
   BOOLEAN("boolean"),
   BINARY("binary"),
   INTEGER_RANGE("integer_range"),
   FLOAT_RANGE("float_range"),
   LONG_RANGE("long_range"),
   DOUBLE_RANGE("double_range"),
   DATE_RANGE("date_range"),
   OBJECT("object"),
   IP("ip");

   private final String value;

   private Type(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }

   // $FF: synthetic method
   private static Type[] $values() {
      return new Type[]{AUTO, TEXT, KEYWORD, LONG, INTEGER, SHORT, BYTE, DOUBLE, FLOAT, DATE, BOOLEAN, BINARY, INTEGER_RANGE, FLOAT_RANGE, LONG_RANGE, DOUBLE_RANGE, DATE_RANGE, OBJECT, IP};
   }
}
