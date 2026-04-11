package com.kuma.boot.flowengine.state;

import cn.hutool.core.lang.Assert;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.flowengine.state.retry.RetryFailTypeEnum;
import com.kuma.boot.flowengine.state.retry.RetryMeta;
import com.kuma.boot.flowengine.state.retry.RetryRetreatTimeUnitEnum;
import com.kuma.boot.flowengine.state.retry.RetryRetreatTypeEnum;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.BeanUtils;

public class FlowTrace extends FlowTraceEvent {
   private long id;
   private String flowName;
   private String node;
   private Integer version;
   private String orderId;
   private String traceId;
   private Integer retryTimes;
   private Date nextRetryTime;
   private Date startTime;
   private Date updateTime;
   private RetryMeta retryMeta;
   private int flag;
   private boolean fromSchedule;

   public FlowTrace() {
      this(UUID.randomUUID().toString());
   }

   public FlowTrace(String eventId) {
      this(eventId, new Date());
   }

   public FlowTrace(String eventId, Date eventTime) {
      super(eventId, eventTime);
      this.retryTimes = 0;
      this.retryMeta = new RetryMeta();
      this.flag = 0;
      this.fromSchedule = false;
   }

   public String retryFast() {
      if (this.limitMax()) {
         return this.retryMaxLimitTo() ? "retry_max_limit_event" : "retry_end";
      } else if (!this.limitMax()) {
         try {
            TimeUnit.MILLISECONDS.sleep(this.getRetryMeta().getFailFastTimeMills());
         } catch (InterruptedException var2) {
         }

         ++this.retryTimes;
         this.setUpdateTime(new Date());
         return "retry_to_target";
      } else {
         return "retry_end";
      }
   }

   public String retryBomb() {
      if (-1 == this.flag) {
         return "retry_normal_end";
      } else {
         ++this.retryTimes;
         this.setUpdateTime(new Date());
         return "retry_to_target";
      }
   }

   public String retryRetreat() {
      Assert.notNull(this.nextRetryTime);
      if (this.limitMax()) {
         return this.retryMaxLimitTo() ? "retry_max_limit_event" : "retry_end";
      } else if (-1 == this.flag) {
         return "retry_normal_end";
      } else {
         Date nowDate = this.getEventTime();
         if (nowDate.before(this.nextRetryTime)) {
            return "retry_normal_end";
         } else if (!this.limitMax()) {
            ++this.retryTimes;
            this.setUpdateTime(new Date());
            this.retreatNextTime();
            return "retry_to_target";
         } else {
            return "retry_end";
         }
      }
   }

   public FlowHistoryTrace end(String error) {
      FlowHistoryTrace historyTrace = new FlowHistoryTrace();
      BeanUtils.copyProperties(this, historyTrace, new String[]{"eventId"});
      historyTrace.setEndTime(new Date());
      historyTrace.setError(error);
      return historyTrace;
   }

   public boolean limitMax() {
      return this.retryTimes >= this.getRetryMeta().getRetryMax();
   }

   public static FlowTrace convertFromMap(DBDialectSupport.AdaptiveDataMap map) {
      FlowTrace trace = new FlowTrace(map.getString("EVENT_ID"), map.getDate("EVENT_TIME"));
      trace.setId(map.getLong("ID"));
      trace.setOrderId(map.getString("ORDER_ID"));
      trace.setTraceId(map.getString("TRACE_ID"));
      trace.setFlowName(map.getString("FLOW_NAME"));
      trace.setVersion(map.getInteger("VERSION"));
      trace.setNode(map.getString("NODE"));
      trace.setStartTime(map.getDate("START_TIME"));
      trace.setUpdateTime(map.getDate("UPDATE_TIME"));
      trace.setNextRetryTime(map.getDate("NEXT_RETRY_TIME"));
      trace.setRetryTimes(map.getInteger("RETRY_TIMES"));
      trace.getRetryMeta().setNodeName(map.getString("NODE_NAME"));
      trace.getRetryMeta().setTarget(map.getString("TARGET"));
      trace.getRetryMeta().setRetryMax(map.getInteger("RETRY_MAX"));
      trace.getRetryMeta().setRetryFailType(RetryFailTypeEnum.getByCode(map.getString("RETRY_FAIL_TYPE")));
      trace.getRetryMeta().setRetryMaxLimitNode(map.getString("RETRY_MAX_LIMIT_NODE"));
      trace.getRetryMeta().setRetreatUnit(map.getInteger("RETREAT_UNIT"));
      trace.getRetryMeta().setRetreatType(RetryRetreatTypeEnum.getByCode(map.getString("RETREAT_TYPE")));
      trace.getRetryMeta().setRetreatTimeUnit(RetryRetreatTimeUnitEnum.getByCode(map.getString("RETREAT_TIME_UNIT")));
      trace.getRetryMeta().executionTarget(map.getString("EXECUTION_TARGET"));
      trace.getRetryMeta().attachment(map.getString("ATTACHMENT"));
      return trace;
   }

   public void updateMeta(Object target, Map<String, Object> attachment) {
      attachment.remove("flowTrace");
      this.clean();
      this.getRetryMeta().setExecutionTarget(target);
      this.getRetryMeta().getAttachment().putAll(attachment);
   }

   public void clean() {
      this.getRetryMeta().getAttachment().remove("flowTrace");
   }

   public void retreatNextTime() {
      if (null != this.getRetryMeta().getRetreatType()) {
         this.getRetryMeta().getRetreatType().retreatNextTime(this);
      }

   }

   private boolean retryMaxLimitTo() {
      return StringUtils.isNotEmpty(this.getRetryMeta().getRetryMaxLimitNode());
   }

   public long getId() {
      return this.id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public String getFlowName() {
      return this.flowName;
   }

   public void setFlowName(String flowName) {
      this.flowName = flowName;
   }

   public String getNode() {
      return this.node;
   }

   public void setNode(String node) {
      this.node = node;
   }

   public Integer getVersion() {
      return this.version;
   }

   public void setVersion(Integer version) {
      this.version = version;
   }

   public String getOrderId() {
      return this.orderId;
   }

   public void setOrderId(String orderId) {
      this.orderId = orderId;
   }

   public String getTraceId() {
      return this.traceId;
   }

   public void setTraceId(String traceId) {
      this.traceId = traceId;
   }

   public Integer getRetryTimes() {
      return this.retryTimes;
   }

   public void setRetryTimes(Integer retryTimes) {
      this.retryTimes = retryTimes;
   }

   public Date getNextRetryTime() {
      return this.nextRetryTime;
   }

   public void setNextRetryTime(Date nextRetryTime) {
      this.nextRetryTime = nextRetryTime;
   }

   public Date getStartTime() {
      return this.startTime;
   }

   public void setStartTime(Date startTime) {
      this.startTime = startTime;
   }

   public Date getUpdateTime() {
      return this.updateTime;
   }

   public void setUpdateTime(Date updateTime) {
      this.updateTime = updateTime;
   }

   public RetryMeta getRetryMeta() {
      return this.retryMeta;
   }

   public void setRetryMeta(RetryMeta retryMeta) {
      this.retryMeta = retryMeta;
   }

   public int getFlag() {
      return this.flag;
   }

   public void setFlag(int flag) {
      this.flag = flag;
   }

   public boolean isFromSchedule() {
      return this.fromSchedule;
   }

   public void setFromSchedule(boolean fromSchedule) {
      this.fromSchedule = fromSchedule;
   }
}
