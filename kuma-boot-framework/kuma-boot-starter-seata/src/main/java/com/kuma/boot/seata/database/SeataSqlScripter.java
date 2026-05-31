package com.kuma.boot.seata.database;

import com.google.common.collect.Lists;
import com.kuma.boot.data.datasource.init.StandardDatabaseScript;
import java.util.List;

public class SeataSqlScripter extends StandardDatabaseScript {
   public String getEvaluateTable() {
      return "undo_log";
   }

   public String getComponentName() {
      return "seata";
   }

   public List<String> getInitSqlFile() {
      return Lists.newArrayList(new String[]{"seata.ddl"});
   }
}
