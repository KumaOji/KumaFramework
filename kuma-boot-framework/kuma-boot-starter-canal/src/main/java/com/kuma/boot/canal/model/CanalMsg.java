package com.kuma.boot.canal.model;

public class CanalMsg {
   private String destination;
   private String schemaName;
   private String tableName;

   public CanalMsg() {
   }

   public String getDestination() {
      return this.destination;
   }

   public void setDestination(String destination) {
      this.destination = destination;
   }

   public String getSchemaName() {
      return this.schemaName;
   }

   public void setSchemaName(String schemaName) {
      this.schemaName = schemaName;
   }

   public String getTableName() {
      return this.tableName;
   }

   public void setTableName(String tableName) {
      this.tableName = tableName;
   }
}
