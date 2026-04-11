package com.kuma.boot.flowengine.module;

import cn.hutool.core.lang.Assert;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.flowengine.exception.FlowException;
import com.kuma.boot.flowengine.state.retry.RetryFailTypeEnum;
import com.kuma.boot.flowengine.state.retry.RetryRetreatTimeUnitEnum;
import com.kuma.boot.flowengine.state.retry.RetryRetreatTypeEnum;

public class RetryNode extends StandardActivityNode {
   public static final String RETRY_END_EVENT = "retry_end";
   public static final String RETRY_END_NORMAL_EVENT = "retry_normal_end";
   public static final String RETRY_TO_TARGET_EVENT = "retry_to_target";
   public static final String RETRY_FROM_TARGET_EVENT = "retry_from_target";
   public static final String RETRY_EXIT_EVENT = "retry_exit";
   public static final String RETRY_MAX_LIMIT_EVENT = "retry_max_limit_event";
   public static final int DEFAULT_RETRY_RETREAT_MAX = 8;
   public static final int DEFAULT_RETRY_FAST_MAX = 3;
   private String target;
   private String retryInfo;
   private String retryMaxLimitNode;
   private RetryFailTypeEnum retryFailType;
   private Integer retryMax;
   private Integer retreatUnit;
   private RetryRetreatTimeUnitEnum retreatTimeUnit;
   private RetryRetreatTypeEnum retreatType;
   private long failFastTimeMills;

   public RetryNode() {
      this.retryFailType = RetryFailTypeEnum.FAIL_RETREAT;
      this.failFastTimeMills = 0L;
   }

   public void addRetryInit(Flow flow) {
      this.checkError(flow);
      Condition condition = new Condition();
      this.setCondition(condition);
      this.addRetryEndTransition(flow);
      this.addRetryEndNormalTransition(flow);
      this.addRetryMaxLimitTransition(flow);
      this.addRetryToTargetTransition(flow);
      this.addFromTargetTransition(flow);
   }

   private void checkError(Flow flow) {
      if (StringUtils.isNotEmpty(this.retryMaxLimitNode) && this.target.equals(this.retryMaxLimitNode)) {
         throw new FlowException(String.format("\u91cd\u8bd5\u8282\u70b9[%s]\u7684retryMaxLimitNode\u4e0d\u80fd\u4e0etarget[%s]\u76f8\u540c", this.getName(), this.target));
      } else if (flow.notExistNode(this.target)) {
         throw new FlowException(String.format("\u91cd\u8bd5\u8282\u70b9[%s]\u7684target[%s]\u8282\u70b9\u4e0d\u5b58\u5728", this.getName(), this.target));
      } else if (StringUtils.isNotEmpty(this.retryMaxLimitNode) && flow.notExistNode(this.retryMaxLimitNode)) {
         throw new FlowException(String.format("\u91cd\u8bd5\u8282\u70b9[%s]\u7684retryMaxLimitNode[%s]\u8282\u70b9\u4e0d\u5b58\u5728", this.getName(), this.retryMaxLimitNode));
      }
   }

   private void addRetryEndTransition(Flow flow) {
      Transition endTransition = new Transition();
      endTransition.setEvent("retry_end");
      endTransition.setFrom(this);
      endTransition.setDescription("\u91cd\u8bd5\u6d41\u7a0b\u7ed3\u675f");
      endTransition.setTo(new NodeRef(flow.getEndNode().getName()));
      this.getCondition().addTransition(endTransition);
      flow.addEvent(endTransition.getEvent());
   }

   private void addRetryEndNormalTransition(Flow flow) {
      Transition endTransition = new Transition();
      endTransition.setEvent("retry_normal_end");
      endTransition.setFrom(this);
      endTransition.setDescription("\u91cd\u8bd5\u6d41\u7a0b\u6b63\u5e38\u7ed3\u675f(\u65f6\u95f4\u672a\u5230,\u4e0d\u6267\u884c\u91cd\u8bd5)");
      endTransition.setTo(new NodeRef(flow.getEndNode().getName()));
      this.getCondition().addTransition(endTransition);
      flow.addEvent(endTransition.getEvent());
   }

   private void addRetryMaxLimitTransition(Flow flow) {
      if (StringUtils.isNotEmpty(this.retryMaxLimitNode)) {
         Transition maxLimitTransition = new Transition();
         maxLimitTransition.setEvent("retry_max_limit_event");
         maxLimitTransition.setFrom(this);
         maxLimitTransition.setDescription("\u91cd\u8bd5\u6d41\u7a0b\u8d85\u9650");
         NodeRef limitRef = new NodeRef(this.getRetryMaxLimitNode());
         limitRef.initialize(flow);
         maxLimitTransition.setTo(limitRef);
         this.getCondition().addTransition(maxLimitTransition);
         flow.addEvent(maxLimitTransition.getEvent());
      }

   }

   private void addRetryToTargetTransition(Flow flow) {
      Transition retryTransition = new Transition();
      retryTransition.setEvent("retry_to_target");
      retryTransition.setDescription("\u91cd\u8bd5\u5230\u76ee\u6807\u8282\u70b9:" + this.target);
      retryTransition.setFrom(this);
      NodeRef retryRef = new NodeRef(this.getTarget());
      retryRef.initialize(flow);
      retryTransition.setTo(retryRef);
      this.getCondition().addTransition(retryTransition);
      flow.addEvent(retryTransition.getEvent());
   }

   private void addFromTargetTransition(Flow flow) {
      ActivityNode node = flow.nodeByName(this.target);
      Transition retryFromTransition = new Transition();
      retryFromTransition.setFrom(node);
      retryFromTransition.setEvent("retry_from_target");
      retryFromTransition.setDescription("\u8fdb\u5165\u91cd\u8bd5\u8282\u70b9:" + this.getName());
      NodeRef retryRef = new NodeRef(this.getName());
      retryRef.initialize(flow);
      retryFromTransition.setTo(retryRef);
      node.getCondition().addTransition(retryFromTransition);
      flow.addEvent(retryFromTransition.getEvent());
      node.setRetryNode(this);
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
      if (RetryFailTypeEnum.FAIL_FAST == retryFailType) {
         String[] params = this.retryInfo.split(",");
         this.setRetryMax(Integer.valueOf(params[0]));
         if (params.length == 2) {
            this.setFailFastTimeMills(Long.parseLong(params[1]));
         }
      }

      if (RetryFailTypeEnum.FAIL_BOMB == retryFailType) {
         this.setRetryMax(Integer.valueOf(this.retryInfo));
      }

      if (RetryFailTypeEnum.FAIL_RETREAT == retryFailType) {
         String[] params = this.retryInfo.split(",");
         Assert.isTrue(4 == params.length);
         this.setRetryMax(Integer.parseInt(params[1]));
         this.setRetreatUnit(Integer.parseInt(params[1]));
         this.setRetreatTimeUnit(RetryRetreatTimeUnitEnum.getByCode(params[2]));
         this.setRetreatType(RetryRetreatTypeEnum.getByCode(params[3]));
      }

   }

   public String getRetryInfo() {
      return this.retryInfo;
   }

   public void setRetryInfo(String retryInfo) {
      this.retryInfo = retryInfo;
   }

   public Integer getRetryMax() {
      return this.retryMax;
   }

   public void setRetryMax(Integer retryMax) {
      if (null == this.retryFailType) {
         throw new IllegalArgumentException("\u8bf7\u5148\u8bbe\u7f6eretryFail\u7c7b\u578b");
      } else if (this.retryFailType == RetryFailTypeEnum.FAIL_RETREAT && retryMax > 8) {
         this.retryMax = 8;
      } else if (this.retryFailType == RetryFailTypeEnum.FAIL_FAST && retryMax > 3) {
         this.retryMax = 3;
      } else {
         this.retryMax = retryMax;
      }
   }

   public Integer getRetreatUnit() {
      return this.retreatUnit;
   }

   public void setRetreatUnit(Integer retreatUnit) {
      this.retreatUnit = retreatUnit;
   }

   public RetryRetreatTimeUnitEnum getRetreatTimeUnit() {
      return this.retreatTimeUnit;
   }

   public void setRetreatTimeUnit(RetryRetreatTimeUnitEnum retreatTimeUnit) {
      this.retreatTimeUnit = retreatTimeUnit;
   }

   public RetryRetreatTypeEnum getRetreatType() {
      return this.retreatType;
   }

   public void setRetreatType(RetryRetreatTypeEnum retreatType) {
      this.retreatType = retreatType;
   }

   public String getRetryMaxLimitNode() {
      return this.retryMaxLimitNode;
   }

   public void setRetryMaxLimitNode(String retryMaxLimitNode) {
      this.retryMaxLimitNode = retryMaxLimitNode;
   }

   public long getFailFastTimeMills() {
      return this.failFastTimeMills;
   }

   public void setFailFastTimeMills(long failFastTimeMills) {
      this.failFastTimeMills = failFastTimeMills;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("RetryNode{");
      sb.append("target=").append(this.target);
      sb.append("retryInfo=").append(this.retryInfo);
      sb.append("retryMaxLimitNode=").append(this.retryMaxLimitNode);
      sb.append("retryFailType=").append(this.retryFailType);
      sb.append("retryMax=").append(this.retryMax);
      sb.append("retreatUnit=").append(this.retreatUnit);
      sb.append("retreatTimeUnit=").append(this.retreatTimeUnit);
      sb.append("retreatType=").append(this.retreatType);
      sb.append("failFastTimeMills=").append(this.failFastTimeMills);
      sb.append('}');
      return sb.toString();
   }
}
