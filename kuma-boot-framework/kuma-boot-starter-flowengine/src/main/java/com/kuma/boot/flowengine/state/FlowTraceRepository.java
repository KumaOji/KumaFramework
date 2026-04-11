package com.kuma.boot.flowengine.state;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.kuma.boot.flowengine.exception.FlowException;
import com.kuma.boot.flowengine.state.retry.RetryFailTypeEnum;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

public class FlowTraceRepository extends DBDialectSupport {
   private static final String INSERT_MYSQL = "INSERT INTO FLOW_TRACE%s ( ORDER_ID,TRACE_ID,FLOW_NAME,VERSION, NODE,RETRY_TIMES, NEXT_RETRY_TIME,NODE_NAME, RETRY_MAX, RETRY_MAX_LIMIT_NODE,TARGET,RETRY_FAIL_TYPE,RETREAT_UNIT,RETREAT_TYPE,RETREAT_TIME_UNIT,START_TIME,EVENT_ID,EVENT_TIME,EXECUTION_TARGET,ATTACHMENT ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
   private static final String INSERT_ORACLE = "INSERT INTO FLOW_TRACES%s ( ORDER_ID,TRACE_ID,FLOW_NAME,VERSION, NODE,RETRY_TIMES, NEXT_RETRY_TIME,NODE_NAME, RETRY_MAX, RETRY_MAX_LIMIT_NODE,TARGET,RETRY_FAIL_TYPE,RETREAT_UNIT,RETREAT_TYPE,RETREAT_TIME_UNIT,START_TIME,EVENT_ID,EVENT_TIME,EXECUTION_TARGET,ATTACHMENT ,ID) VALES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
   private static final String UPDATE = "UPDATE FLOW_TRACE%s SET RETRY_TIMES=?,NEXT_RETRY_TIME=?,EXECUTION_TARGET=?,ATTACHMENT=? WHERE TRACE_ID=?";
   private static final String DELETE = "DELETE FROM FLOW_TRACE%s WHERE TRACE ID=?";
   private static final String LOCK = "SELECT TRACE_ID FROM FLOW_TRACE%s WHERE TRACE_ID=? FOR UPDATE";

   public FlowTraceRepository(String dialect, JdbcTemplate jdbcTemplate) {
      super(dialect, jdbcTemplate);
   }

   public boolean lock(FlowTrace flowTrace) {
      return this.forUpdate(flowTrace, "SELECT TRACE_ID FROM FLOW_TRACE%s WHERE TRACE_ID=? FOR UPDATE", flowTrace.getTraceId());
   }

   public void store(FlowTrace flowTrace) {
      this.logger.debug("\u5b58\u50a8\u6d41\u7a0b\u6267\u884c.traceId: {},orderId:{}", flowTrace.getTraceId(), flowTrace.getOrderId());
      if (this.isMysql()) {
         this.insert(flowTrace, "INSERT INTO FLOW_TRACE%s ( ORDER_ID,TRACE_ID,FLOW_NAME,VERSION, NODE,RETRY_TIMES, NEXT_RETRY_TIME,NODE_NAME, RETRY_MAX, RETRY_MAX_LIMIT_NODE,TARGET,RETRY_FAIL_TYPE,RETREAT_UNIT,RETREAT_TYPE,RETREAT_TIME_UNIT,START_TIME,EVENT_ID,EVENT_TIME,EXECUTION_TARGET,ATTACHMENT ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", this.toSqlParams(flowTrace));
      }

      if (this.isOracle()) {
         this.insert(flowTrace, "INSERT INTO FLOW_TRACES%s ( ORDER_ID,TRACE_ID,FLOW_NAME,VERSION, NODE,RETRY_TIMES, NEXT_RETRY_TIME,NODE_NAME, RETRY_MAX, RETRY_MAX_LIMIT_NODE,TARGET,RETRY_FAIL_TYPE,RETREAT_UNIT,RETREAT_TYPE,RETREAT_TIME_UNIT,START_TIME,EVENT_ID,EVENT_TIME,EXECUTION_TARGET,ATTACHMENT ,ID) VALES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", this.toSqlParams(flowTrace));
      }

   }

   public void remove(FlowTrace flowTrace) {
      this.logger.debug("\u79fb\u9664\u6d41\u7a0b\u6267\u884c.,traceId:{},orderId:{}", flowTrace.getTraceId(), flowTrace.getOrderId());
      this.delete(flowTrace, "DELETE FROM FLOW_TRACE%s WHERE TRACE ID=?", new Object[]{flowTrace.getTraceId()});
   }

   public void restore(FlowTrace flowTrace) {
      this.logger.debug("\u66f4\u65b0\u6d41\u7a0b\u6267\u884c.traceId:{},orderId:{}", flowTrace.getTraceId(), flowTrace.getOrderId());
      this.update(flowTrace, "UPDATE FLOW_TRACE%s SET RETRY_TIMES=?,NEXT_RETRY_TIME=?,EXECUTION_TARGET=?,ATTACHMENT=? WHERE TRACE_ID=?", new Object[]{flowTrace.getRetryTimes(), flowTrace.getNextRetryTime(), flowTrace.getRetryMeta().executionTargetString(), flowTrace.getRetryMeta().attachmentString(), flowTrace.getTraceId()});
   }

   public List<FlowTrace> listFlowTracesWithLock(RetryFailTypeEnum failType, List<String> nodes, String orderBy, String sort, int batch) {
      StringBuilder sqlBuilder = new StringBuilder();
      if (this.isMysql()) {
         sqlBuilder.append("SELECT ID, ORDER_ID,TRACE_ID,FLOW_NAME,VERSION, NODE,RETRY_TIMES, NEXT_RETRY_TIME,NODE_NAME, RETRY_MAX, RETRY_MAX_LIMIT_NODE,TARGET,RETRY_FAIL_TYPE,RETREAT_UNIT,RETREAT_TYPE,RETREAT_TIME_UNIT,START_TIME,EVENT_ID,EVENT_TIME,EXECUTION_TARGET,ATTACHMENT FROM FLOW_TRACE%s");
         this.listSqlCondition(sqlBuilder, nodes, orderBy, sort);
         sqlBuilder.append(" LIMIT 0,").append(batch);
         sqlBuilder.append("");
         return this.selectList(failType, sqlBuilder.toString());
      } else if (this.isOracle()) {
         sqlBuilder.append("SELECT * FROM(");
         sqlBuilder.append(" SELECT T.*,ROWNUM RN");
         sqlBuilder.append(" FROM (SELECT ID, ORDER_ID,TRACE_ID,FLOW_NAME,VERSION, NODE,RETRY_TIMES, NEXT_RETRY_TIME,NODE_NAME, RETRY_MAX, RETRY_MAX_LIMIT_NODE,TARGET,RETRY_FAIL_TYPE,RETREAT_UNIT,RETREAT_TYPE,RETREAT_TIME_UNIT,START_TIME,EVENT_ID,EVENT_TIME,EXECUTION_TARGET,ATTACHMENT FROM FLOW_TRACE%s");
         this.listSqlCondition(sqlBuilder, nodes, orderBy, sort);
         sqlBuilder.append(")T");
         sqlBuilder.append(" WHERE ROWNUM <=" + batch + ")");
         sqlBuilder.append(" WHERE RN >=0");
         return this.selectList(failType, sqlBuilder.toString());
      } else {
         throw new FlowException("\u4e0d\u53ef\u80fd\u7684\u914d\u7f6e");
      }
   }

   private void listSqlCondition(StringBuilder sqlBuilder, List<String> nodes, String orderBy, String sort) {
      if (null != nodes) {
         List<String> withQuoteList = Lists.newArrayList();
         nodes.stream().forEach((el) -> withQuoteList.add("'" + el + "'"));
         sqlBuilder.append(" WHERE NODE IN (").append(Joiner.on(",").join(withQuoteList.iterator())).append(")");
      }

      sqlBuilder.append(" ORDER BY ").append(orderBy.toUpperCase());
      sqlBuilder.append(" ").append(sort.toUpperCase());
   }
}
