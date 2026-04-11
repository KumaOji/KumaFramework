package com.kuma.boot.flowengine.state;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

public class FlowHistoryTraceRepository extends DBDialectSupport {
   private static final String INSERT_MYSQL = "INSERT INTO FLOW_TRACE_HISTORY ( ORDER_ID,TRACE_ID,FLOW_NAME,VERSION, NODE,RETRY_TIMES, NEXT_RETRY_TIME,NODE_NAME, RETRY_MAX, RETRY_MAX_LIMIT_NODE,TARGET,RETRY_FAIL_TYPE,RETREAT_UNIT,RETREAT_TYPE,RETREAT_TIME_UNIT,START_TIME,EVENT_ID,EVENT_TIME,EXECUTION_TARGET,ATTACHMENT  END_TINE, ERROR) VALES(?,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,?, ? ,? ,? ,? ,?)";
   private static final String INSERT_ORACLE = "INSERT INTO FLOW_TRACE_HISTORY ( ORDER_ID,TRACE_ID,FLOW_NAME,VERSION, NODE,RETRY_TIMES, NEXT_RETRY_TIME,NODE_NAME, RETRY_MAX, RETRY_MAX_LIMIT_NODE,TARGET,RETRY_FAIL_TYPE,RETREAT_UNIT,RETREAT_TYPE,RETREAT_TIME_UNIT,START_TIME,EVENT_ID,EVENT_TIME,EXECUTION_TARGET,ATTACHMENT  EN_TIME,EROR,ID) VAUS(?,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,?, ? ,? ,? ,? ,?, ?)";

   public FlowHistoryTraceRepository(String dialect, JdbcTemplate jdbcTemplate) {
      super(dialect, jdbcTemplate);
   }

   public void store(FlowHistoryTrace flowHistoryTrace) {
      this.logger.debug("\u5b58\u50a8\u5386\u53f2\u6267\u884c.traceId: {},orderId:{}", flowHistoryTrace.getTraceId(), flowHistoryTrace.getOrderId());
      List<Object> params = this.toSqlParams(flowHistoryTrace);
      params.add(flowHistoryTrace.getEndTime());
      params.add(flowHistoryTrace.getError());
      if (this.isMysql()) {
         this.insert(flowHistoryTrace, "INSERT INTO FLOW_TRACE_HISTORY ( ORDER_ID,TRACE_ID,FLOW_NAME,VERSION, NODE,RETRY_TIMES, NEXT_RETRY_TIME,NODE_NAME, RETRY_MAX, RETRY_MAX_LIMIT_NODE,TARGET,RETRY_FAIL_TYPE,RETREAT_UNIT,RETREAT_TYPE,RETREAT_TIME_UNIT,START_TIME,EVENT_ID,EVENT_TIME,EXECUTION_TARGET,ATTACHMENT  END_TINE, ERROR) VALES(?,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,?, ? ,? ,? ,? ,?)", params);
      }

      if (this.isOracle()) {
         this.insert(flowHistoryTrace, "INSERT INTO FLOW_TRACE_HISTORY ( ORDER_ID,TRACE_ID,FLOW_NAME,VERSION, NODE,RETRY_TIMES, NEXT_RETRY_TIME,NODE_NAME, RETRY_MAX, RETRY_MAX_LIMIT_NODE,TARGET,RETRY_FAIL_TYPE,RETREAT_UNIT,RETREAT_TYPE,RETREAT_TIME_UNIT,START_TIME,EVENT_ID,EVENT_TIME,EXECUTION_TARGET,ATTACHMENT  EN_TIME,EROR,ID) VAUS(?,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,?, ? ,? ,? ,? ,?, ?)", params);
      }

   }
}
