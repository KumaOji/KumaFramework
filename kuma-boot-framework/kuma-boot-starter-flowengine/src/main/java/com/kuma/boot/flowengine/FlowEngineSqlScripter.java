package com.kuma.boot.flowengine;

import com.google.common.collect.Lists;
import com.kuma.boot.data.datasource.init.StandardDatabaseScript;
import java.util.List;

public class FlowEngineSqlScripter extends StandardDatabaseScript {
   public FlowEngineSqlScripter() {
   }

   public String getEvaluateTable() {
      return "FLOW_TRACE";
   }

   public String getComponentName() {
      return "flowengine";
   }

   public List<String> getInitSqlFile() {
      return Lists.newArrayList(new String[]{"flowengine.ddl"});
   }
}
