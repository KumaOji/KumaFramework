package com.kuma.boot.idgenerator.database;

import com.google.common.collect.Lists;
import com.kuma.boot.data.datasource.init.StandardDatabaseScript;
import java.util.List;

public class IdGeneratorSqlScripter extends StandardDatabaseScript {
   public IdGeneratorSqlScripter() {
   }

   public String getEvaluateTable() {
      return "undo_log";
   }

   public String getComponentName() {
      return "idgenerator";
   }

   public List<String> getInitSqlFile() {
      return Lists.newArrayList(new String[]{"idgenerator.ddl"});
   }
}
