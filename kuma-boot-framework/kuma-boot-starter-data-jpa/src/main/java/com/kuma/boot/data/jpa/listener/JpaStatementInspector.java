package com.kuma.boot.data.jpa.listener;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import java.util.List;
import org.hibernate.resource.jdbc.spi.StatementInspector;

public class JpaStatementInspector implements StatementInspector {
   public JpaStatementInspector() {
   }

   public String inspect(String sql) {
      List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
      if (statementList != null && statementList.size() != 0) {
         this.tenantEnhancements(statementList);
         this.logicEnhancements(statementList);
         return SQLUtils.toSQLString(statementList, JdbcConstants.MYSQL);
      } else {
         return sql;
      }
   }

   private void tenantEnhancements(List<SQLStatement> statementList) {
   }

   private void logicEnhancements(List<SQLStatement> statementList) {
   }
}
