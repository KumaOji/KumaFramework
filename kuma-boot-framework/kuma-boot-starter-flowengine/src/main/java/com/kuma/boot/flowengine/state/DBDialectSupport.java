package com.kuma.boot.flowengine.state;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kuma.boot.flowengine.state.retry.RetryFailTypeEnum;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

public class DBDialectSupport {
   protected Logger logger = LoggerFactory.getLogger(this.getClass());
   protected static final String FIELDS = " ORDER_ID,TRACE_ID,FLOW_NAME,VERSION, NODE,RETRY_TIMES, NEXT_RETRY_TIME,NODE_NAME, RETRY_MAX, RETRY_MAX_LIMIT_NODE,TARGET,RETRY_FAIL_TYPE,RETREAT_UNIT,RETREAT_TYPE,RETREAT_TIME_UNIT,START_TIME,EVENT_ID,EVENT_TIME,EXECUTION_TARGET,ATTACHMENT ";
   private static final String NEW_ID = "SELECT SEO_FLOWENGINE.nextVaL FROM DUAL";
   private JdbcTemplate jdbcTemplate;
   private String dialect;

   public DBDialectSupport(String dialect, JdbcTemplate jdbcTemplate) {
      this.dialect = dialect;
      this.jdbcTemplate = jdbcTemplate;
   }

   protected boolean forUpdate(FlowTrace flowTrace, String sql, String traceId) {
      try {
         return null != this.jdbcTemplate.queryForObject(this.sql(flowTrace, sql), String.class, new Object[]{traceId});
      } catch (DataAccessException e) {
         this.logger.error("\u5b58\u50a8\u5c42\u5f02\u5e38", flowTrace, e);
         return false;
      } catch (Exception e) {
         this.logger.error("\u672a\u77e5\u5f02\u5e38", flowTrace, e);
         return false;
      }
   }

   protected boolean insert(FlowTrace flowTrace, String sql, List<Object> params) {
      if (this.isOracle()) {
         long idSeq = this.newSequence();
         flowTrace.setId(idSeq);
         params.add(idSeq);
      }

      return this.update(flowTrace, sql, params.toArray());
   }

   protected boolean delete(FlowTrace flowTrace, String sql, Object[] args) {
      return this.update(flowTrace, sql, args);
   }

   protected boolean update(FlowTrace flowTrace, String sql, Object[] args) {
      try {
         int result = this.jdbcTemplate.update(this.sql(flowTrace, sql), new ArgumentPreparedStatementSetter(args));
         return result != 0;
      } catch (DataAccessException e) {
         this.logger.error("\u5b58\u50a8\u5c42\u5f02\u5e38", flowTrace, e);
         return false;
      } catch (Exception e) {
         this.logger.error("\u672a\u77e5\u5f02\u5e38", flowTrace, e);
         return false;
      }
   }

   protected List<FlowTrace> selectList(RetryFailTypeEnum failType, String sql) {
      List<FlowTrace> traces = Lists.newArrayList();
      List<Map<String, Object>> resultList = this.jdbcTemplate.queryForList(this.sql(failType, sql));
      traces.addAll((Collection)resultList.stream().map((map) -> FlowTrace.convertFromMap(new AdaptiveDataMap(map))).collect(Collectors.toList()));
      return traces;
   }

   protected List<Object> toSqlParams(FlowTrace trace) {
      return Lists.newArrayList(new Object[]{trace.getOrderId(), trace.getTraceId(), trace.getFlowName(), trace.getVersion(), trace.getNode(), trace.getRetryTimes(), trace.getNextRetryTime(), trace.getRetryMeta().getNodeName(), trace.getRetryMeta().getRetryMax(), trace.getRetryMeta().getRetryMaxLimitNode(), trace.getRetryMeta().getTarget(), trace.getRetryMeta().getRetryFailType().getCode(), trace.getRetryMeta().getRetreatUnit(), null != trace.getRetryMeta().getRetreatType() ? trace.getRetryMeta().getRetreatType().getCode() : null, null != trace.getRetryMeta().getRetreatTimeUnit() ? trace.getRetryMeta().getRetreatTimeUnit().getCode() : null, trace.getStartTime(), trace.getEventId(), trace.getEventTime(), trace.getRetryMeta().executionTargetString(), trace.getRetryMeta().attachmentString()});
   }

   protected boolean isMysql() {
      return "mysql".equals(this.dialect);
   }

   protected boolean isOracle() {
      return "oracle".equals(this.dialect);
   }

   private long newSequence() {
      return (Long)this.jdbcTemplate.queryForObject("SELECT SEO_FLOWENGINE.nextVaL FROM DUAL", Long.class);
   }

   private String sql(FlowTrace flowTrace, String baseSql) {
      return flowTrace instanceof FlowHistoryTrace ? baseSql : this.sql(flowTrace.getRetryMeta().getRetryFailType(), baseSql);
   }

   private String sql(RetryFailTypeEnum failType, String baseSql) {
      return RetryFailTypeEnum.FAIL_BOMB == failType ? String.format(baseSql, "_BOMB") : String.format(baseSql, "");
   }

   public static class AdaptiveDataMap extends HashMap<String, Object> {
      private Map<String, Object> target = Maps.newHashMap();

      public AdaptiveDataMap(Map<String, Object> map) {
         this.target = map;
      }

      public String getString(String key) {
         Object result = this.target.get(key);
         return null == result ? null : (String)result;
      }

      public Integer getInteger(String key) {
         Object result = this.target.get(key);
         return null == result ? null : Integer.parseInt(result.toString());
      }

      public Long getLong(String key) {
         Object result = this.target.get(key);
         return null == result ? null : Long.parseLong(result.toString());
      }

      public Date getDate(String key) {
         Object result = this.target.get(key);
         return null == result ? null : (Date)result;
      }
   }
}
