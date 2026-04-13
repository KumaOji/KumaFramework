package com.kuma.boot.data.jpa.model;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUnionQueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.util.JdbcConstants;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.List;

public class SqlConditionWrapper {
   private ITableFieldConditionDecision conditionDecision;

   public SqlConditionWrapper(ITableFieldConditionDecision conditionDecision) {
      this.conditionDecision = conditionDecision;
   }

   public void addStatementCondition(SQLStatement sqlStatement, String fieldName, String fieldValue) {
      if (sqlStatement instanceof SQLSelectStatement) {
         SQLSelectQueryBlock queryObject = (SQLSelectQueryBlock)((SQLSelectStatement)sqlStatement).getSelect().getQuery();
         this.addSelectStatementCondition(queryObject, queryObject.getFrom(), fieldName, fieldValue);
      } else if (sqlStatement instanceof SQLUpdateStatement) {
         SQLUpdateStatement updateStatement = (SQLUpdateStatement)sqlStatement;
         this.addUpdateStatementCondition(updateStatement, fieldName, fieldValue);
      } else {
         if (sqlStatement instanceof SQLInsertStatement) {
            return;
         }

         if (sqlStatement instanceof SQLDeleteStatement) {
            SQLDeleteStatement deleteStatement = (SQLDeleteStatement)sqlStatement;
            this.addDeleteStatementCondition(deleteStatement, fieldName, fieldValue);
            return;
         }
      }

   }

   private void addInsertStatementCondition(SQLInsertStatement insertStatement, String fieldName, String fieldValue) {
      if (insertStatement != null) {
         SQLSelect sqlSelect = ((SQLInsertInto)insertStatement).getQuery();
         if (sqlSelect != null) {
            SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock)sqlSelect.getQuery();
            this.addSelectStatementCondition(selectQueryBlock, selectQueryBlock.getFrom(), fieldName, fieldValue);
         } else if (this.conditionDecision.adjudge(insertStatement.getTableName().getSimpleName(), fieldName) && !((SQLInsertInto)insertStatement).getColumns().stream().anyMatch((e) -> fieldName.equalsIgnoreCase(e.clone().toString()))) {
            ((SQLInsertInto)insertStatement).getColumns().add(new SQLIdentifierExpr(fieldName));
            ((SQLInsertStatement.ValuesClause)((SQLInsertInto)insertStatement).getValuesList().get(0)).addValue(new SQLCharExpr(fieldValue));
         }
      }

   }

   private void addDeleteStatementCondition(SQLDeleteStatement deleteStatement, String fieldName, String fieldValue) {
      SQLExpr where = deleteStatement.getWhere();
      this.addSQLExprCondition(where, fieldName, fieldValue);
      SQLExpr newCondition = this.newEqualityCondition(deleteStatement.getTableName().getSimpleName(), deleteStatement.getTableSource().getAlias(), fieldName, fieldValue, where);
      deleteStatement.setWhere(newCondition);
   }

   private void addSQLExprCondition(SQLExpr where, String fieldName, String fieldValue) {
      if (where instanceof SQLInSubQueryExpr inWhere) {
         SQLSelect subSelectObject = inWhere.getSubQuery();
         SQLSelectQueryBlock subQueryObject = (SQLSelectQueryBlock)subSelectObject.getQuery();
         this.addSelectStatementCondition(subQueryObject, subQueryObject.getFrom(), fieldName, fieldValue);
      } else if (where instanceof SQLBinaryOpExpr opExpr) {
         SQLExpr left = opExpr.getLeft();
         SQLExpr right = opExpr.getRight();
         this.addSQLExprCondition(left, fieldName, fieldValue);
         this.addSQLExprCondition(right, fieldName, fieldValue);
      } else if (where instanceof SQLQueryExpr) {
         SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock)((SQLQueryExpr)where).getSubQuery().getQuery();
         this.addSelectStatementCondition(selectQueryBlock, selectQueryBlock.getFrom(), fieldName, fieldValue);
      }

   }

   private void addUpdateStatementCondition(SQLUpdateStatement updateStatement, String fieldName, String fieldValue) {
      SQLExpr where = updateStatement.getWhere();
      this.addSQLExprCondition(where, fieldName, fieldValue);
      SQLExpr newCondition = this.newEqualityCondition(updateStatement.getTableName().getSimpleName(), updateStatement.getTableSource().getAlias(), fieldName, fieldValue, where);
      updateStatement.setWhere(newCondition);
   }

   private void addSelectStatementCondition(SQLSelectQueryBlock queryObject, SQLTableSource from, String fieldName, String fieldValue) {
      if (!StringUtils.isBlank(fieldName) && from != null && queryObject != null) {
         SQLExpr originCondition = queryObject.getWhere();
         if (from instanceof SQLExprTableSource) {
            String tableName = ((SQLIdentifierExpr)((SQLExprTableSource)from).getExpr()).getName();
            String alias = from.getAlias();
            SQLExpr newCondition = this.newInCondition(tableName, alias, fieldName, fieldValue, originCondition);
            queryObject.setWhere(newCondition);
         } else if (from instanceof SQLJoinTableSource) {
            SQLJoinTableSource joinObject = (SQLJoinTableSource)from;
            SQLTableSource left = joinObject.getLeft();
            SQLTableSource right = joinObject.getRight();
            this.addSelectStatementCondition(queryObject, left, fieldName, fieldValue);
            this.addSelectStatementCondition(queryObject, right, fieldName, fieldValue);
         } else if (from instanceof SQLSubqueryTableSource) {
            SQLSelect subSelectObject = ((SQLSubqueryTableSource)from).getSelect();
            SQLSelectQueryBlock subQueryObject = (SQLSelectQueryBlock)subSelectObject.getQuery();
            this.addSelectStatementCondition(subQueryObject, subQueryObject.getFrom(), fieldName, fieldValue);
         } else if (from instanceof SQLUnionQueryTableSource) {
            SQLUnionQueryTableSource union = (SQLUnionQueryTableSource)from;
            SQLUnionQuery sqlUnionQuery = union.getUnion();
            this.addSelectStatementConditionUnion(queryObject, sqlUnionQuery, fieldName, fieldValue);
         } else {
            System.out.println("sql\u589e\u5f3a\u5f02\u5e38");
         }

      }
   }

   private void addSelectStatementConditionUnion(SQLSelectQueryBlock queryObject, SQLUnionQuery sqlUnionQuery, String fieldName, String fieldValue) {
      if (sqlUnionQuery.getLeft() instanceof SQLUnionQuery) {
         SQLUnionQuery temQuery = (SQLUnionQuery)sqlUnionQuery.getLeft();
         this.addSelectStatementConditionUnion(queryObject, temQuery, fieldName, fieldValue);
      }

      if (sqlUnionQuery.getLeft() instanceof SQLSelectQueryBlock) {
         SQLSelectQueryBlock left = (SQLSelectQueryBlock)sqlUnionQuery.getLeft();
         this.addSelectStatementCondition(left, left.getFrom(), fieldName, fieldValue);
      }

      if (sqlUnionQuery.getRight() instanceof SQLSelectQueryBlock) {
         SQLSelectQueryBlock right = (SQLSelectQueryBlock)sqlUnionQuery.getRight();
         this.addSelectStatementCondition(right, right.getFrom(), fieldName, fieldValue);
      }

   }

   private SQLExpr newEqualityCondition(String tableName, String tableAlias, String fieldName, String fieldValue, SQLExpr originCondition) {
      if (!this.conditionDecision.adjudge(tableName, fieldName)) {
         return originCondition;
      } else {
         SQLExpr condition = null;
         SQLBinaryOpExpr var8;
         if (fieldValue == null) {
            var8 = new SQLBinaryOpExpr(new SQLIdentifierExpr(fieldName), new SQLNullExpr(), SQLBinaryOperator.Is);
         } else {
            String filedName = StringUtils.isBlank(tableAlias) ? fieldName : tableAlias + "." + fieldName;
            var8 = new SQLBinaryOpExpr(new SQLIdentifierExpr(filedName), new SQLCharExpr(fieldValue), SQLBinaryOperator.Equality);
         }

         return SQLUtils.buildCondition(SQLBinaryOperator.BooleanAnd, var8, false, originCondition);
      }
   }

   private SQLExpr newInCondition(String tableName, String tableAlias, String fieldName, String fieldValue, SQLExpr originCondition) {
      if (!this.conditionDecision.adjudge(tableName, fieldName)) {
         return originCondition;
      } else if (fieldValue == null && this.conditionDecision.isNull()) {
         return originCondition;
      } else {
         String filedName = StringUtils.isBlank(tableAlias) ? fieldName : tableAlias + "." + fieldName;
         SQLExpr condition = null;
         Object var11;
         if (fieldValue != null && fieldValue.contains(",")) {
            String[] split = fieldValue.split(",");
            StringBuffer stringBuffer = new StringBuffer();

            for(int i = 0; i < split.length; ++i) {
               if (i != split.length - 1) {
                  stringBuffer.append("'" + split[i].trim() + "',");
               } else {
                  stringBuffer.append("'" + split[i].trim() + "'");
               }
            }

            var11 = new SQLIdentifierExpr(filedName + " in ( " + stringBuffer.toString() + ")");
         } else if (fieldValue == null) {
            var11 = new SQLBinaryOpExpr(new SQLIdentifierExpr(fieldName), new SQLNullExpr(), SQLBinaryOperator.Is);
         } else {
            var11 = new SQLBinaryOpExpr(new SQLIdentifierExpr(filedName), new SQLCharExpr(fieldValue), SQLBinaryOperator.Equality);
         }

         return SQLUtils.buildCondition(SQLBinaryOperator.BooleanAnd, (SQLExpr)var11, false, originCondition);
      }
   }

   public static void main(String[] args) {
      String sql = "select u.*,g.name from user u join (select * from user_group g  join user_role r on g.role_code=r.code  ) g on u.groupId=g.groupId where u.name='123'";
      List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
      SQLStatement sqlStatement = (SQLStatement)statementList.get(0);
      DbType dbType = sqlStatement.getDbType();
      SqlConditionWrapper helper = new SqlConditionWrapper(new ITableFieldConditionDecision() {
         public boolean adjudge(String tableName, String fieldName) {
            return true;
         }

         public boolean isNull() {
            return true;
         }
      });
      helper.addStatementCondition(sqlStatement, "tenant_id", "1");
      System.out.println("dbType\uff1a" + String.valueOf(dbType));
      System.out.println("\u6e90sql\uff1a" + sql);
      System.out.println("\u4fee\u6539\u540esql:" + SQLUtils.toSQLString(statementList, JdbcConstants.POSTGRESQL));
   }

   public interface ITableFieldConditionDecision {
      boolean adjudge(String tableName, String fieldName);

      boolean isNull();
   }
}
