package com.kuma.boot.canal.canalquick.common;

public class CanalConstants {
   public static final String DEFAULT_HOST = "127.0.0.1";
   public static final int DEFAULT_PORT = 1111;
   public static final String DEFAULT_DESTINATION = "example";
   public static final String DEFAULT_USERNAME = "canal";
   public static final String DEFAULT_PASSWORD = "canal";
   public static final int DEFAULT_BATCHSIZE = 1000;
   public static final String DEFAULT_FILTER = ".*\\..*";
   public static final boolean DEFAULT_CUSTOM = true;
   public static final String UNITY_TABLE = "common_table";
   public static final String UNITY_SCHEMA = "common_table";

   public CanalConstants() {
   }

   public static class Parser {
      public static final String SCHEMA_NAME = "schemaName";
      public static final String TABLE_NAME = "tableName";
      public static final String EVNENT_TYPE = "evenType";
      public static final String BEFORE_COLUMN = "beforeColumn";
      public static final String AFTER_COLUMN = "afterColumn";
      public static final String CHANGED_COLUMN = "changedColumn";
      public static final String CHANGED_COLUMN_BEFORE = "before";
      public static final String CHANGED_COLUMN_AFTER = "after";

      public Parser() {
      }
   }
}
