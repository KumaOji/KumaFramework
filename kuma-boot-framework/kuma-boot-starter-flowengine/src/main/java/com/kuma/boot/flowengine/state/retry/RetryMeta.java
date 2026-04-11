package com.kuma.boot.flowengine.state.retry;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.Map;

public class RetryMeta {
   private String nodeName;
   private Integer retryMax;
   private String retryMaxLimitNode;
   private String target;
   private RetryFailTypeEnum retryFailType;
   private Integer retreatUnit;
   private RetryRetreatTypeEnum retreatType;
   private RetryRetreatTimeUnitEnum retreatTimeUnit;
   private Object executionTarget;
   private Map<String, Object> attachment;
   private long failFastTimeMills;

   public RetryMeta() {
      this.retryFailType = RetryFailTypeEnum.FAIL_RETREAT;
      this.retreatUnit = 1;
      this.retreatType = RetryRetreatTypeEnum.BY_DOUBLE;
      this.retreatTimeUnit = RetryRetreatTimeUnitEnum.HOUR;
      this.attachment = Maps.newHashMap();
      this.failFastTimeMills = 0L;
   }

   public String executionTargetString() {
      return null == this.executionTarget ? "" : JSON.toJSONString(this.executionTarget);
   }

   public void executionTarget(String json) {
      if (StringUtils.isNotBlank(json)) {
         this.setExecutionTarget(JSON.parse(json));
      }

   }

   public String attachmentString() {
      return null == this.attachment ? "" : JSON.toJSONString(this.attachment);
   }

   public void attachment(String json) {
      if (StringUtils.isNotBlank(json)) {
         this.setAttachment(JSON.parseObject(json));
      }

   }

   public String getNodeName() {
      return this.nodeName;
   }

   public void setNodeName(String nodeName) {
      this.nodeName = nodeName;
   }

   public Integer getRetryMax() {
      return this.retryMax;
   }

   public void setRetryMax(Integer retryMax) {
      this.retryMax = retryMax;
   }

   public void setRetryMaxLimitNode(String retryMaxLimitNode) {
      this.retryMaxLimitNode = retryMaxLimitNode;
   }

   public String getRetryMaxLimitNode() {
      return this.retryMaxLimitNode;
   }

   public String getTarget() {
      return this.target;
   }

   public void setTarget(String target) {
      this.target = target;
   }

   public RetryFailTypeEnum getRetryFailType() {
      return this.retryFailType;
   }

   public void setRetryFailType(RetryFailTypeEnum retryFailType) {
      this.retryFailType = retryFailType;
   }

   public Integer getRetreatUnit() {
      return this.retreatUnit;
   }

   public void setRetreatUnit(Integer retreatUnit) {
      this.retreatUnit = retreatUnit;
   }

   public RetryRetreatTypeEnum getRetreatType() {
      return this.retreatType;
   }

   public void setRetreatType(RetryRetreatTypeEnum retreatType) {
      this.retreatType = retreatType;
   }

   public RetryRetreatTimeUnitEnum getRetreatTimeUnit() {
      return this.retreatTimeUnit;
   }

   public void setRetreatTimeUnit(RetryRetreatTimeUnitEnum retreatTimeUnit) {
      this.retreatTimeUnit = retreatTimeUnit;
   }

   public Object getExecutionTarget() {
      return this.executionTarget;
   }

   public void setExecutionTarget(Object executionTarget) {
      this.executionTarget = executionTarget;
   }

   public Map<String, Object> getAttachment() {
      return this.attachment;
   }

   public void setAttachment(Map<String, Object> attachment) {
      this.attachment = attachment;
   }

   public long getFailFastTimeMills() {
      return this.failFastTimeMills;
   }

   public void setFailFastTimeMills(long failFastTimeMills) {
      this.failFastTimeMills = failFastTimeMills;
   }
}
