package com.kuma.boot.canal.canalquick.event;

public class TableInfo {
   private String schemaName;
   private String tableName;

   public TableInfo(String schemaName, String tableName) {
      this.schemaName = schemaName;
      this.tableName = tableName;
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
