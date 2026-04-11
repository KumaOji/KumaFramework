package com.kuma.boot.flowengine.state.retry;

import com.kuma.boot.flowengine.annotation.After;
import com.kuma.boot.flowengine.annotation.Before;
import com.kuma.boot.flowengine.annotation.Executor;
import com.kuma.boot.flowengine.engine.Execution;
import com.kuma.boot.flowengine.module.RetryNode;
import com.kuma.boot.flowengine.state.FlowTrace;
import java.util.Date;
import java.util.Map;

public abstract class FlowRetryTrigger {
   public FlowRetryTrigger() {
   }

   @Before
   public void before(Execution execution) {
      this.moreTargetAndAttachment(execution);
      FlowTrace flowTrace = execution.getFlowTrace();
      this.beforeRetry(flowTrace.getOrderId(), flowTrace.getFlowName(), flowTrace.getVersion(), flowTrace.getNode(), flowTrace.getRetryMeta().getTarget(), flowTrace.getRetryMeta().getRetryFailType(), flowTrace.getRetryMeta().getRetryMax(), flowTrace.getRetryTimes(), flowTrace.getStartTime(), flowTrace.getUpdateTime());
   }

   @Executor
   public String execute(Execution execution) {
      RetryNode retryNode = (RetryNode)execution.getCurrentNodeExecution().currentNode();
      String result = retryNode.getRetryFailType().execute(execution);
      retryNode.getRetryFailType().prepareNext(execution);
      return result;
   }

   @After
   public void after(Execution execution) {
      FlowTrace flowTrace = execution.getFlowTrace();
      this.afterRetry(flowTrace.getOrderId(), flowTrace.getFlowName(), flowTrace.getVersion(), flowTrace.getNode(), flowTrace.getRetryMeta().getTarget(), flowTrace.getRetryMeta().getRetryFailType(), flowTrace.getRetryMeta().getRetryMax(), flowTrace.getRetryTimes(), flowTrace.getStartTime(), flowTrace.getUpdateTime());
      execution.getFlowTrace().setFromSchedule(false);
   }

   protected void beforeRetry(String orderId, String flowName, int version, String retryNode, String targetNode, RetryFailTypeEnum retryFail, int retryMax, int retryTimes, Date startTime, Date lastRetryTime) {
   }

   protected void afterRetry(String orderId, String flowName, int version, String retryNode, String targetNode, RetryFailTypeEnum retryFail, int retryMax, int retryTimes, Date startTime, Date lastRetryTime) {
   }

   protected Object target(String orderId) {
      return null;
   }

   protected Map<String, Object> attachment(String orderId, Object target) {
      return null;
   }

   private void moreTargetAndAttachment(Execution execution) {
      if (this.isFromSchedule(execution)) {
         String orderId = execution.orderId();
         Object target = this.target(orderId);
         if (null != target) {
            execution.setTarget(target);
         }

         Map<String, Object> attachment = this.attachment(orderId, target);
         if (null != attachment && attachment.size() > 0) {
            execution.getAttachment().putAll(attachment);
         }

         execution.getFlowTrace().updateMeta(execution.getTarget(), execution.getAttachment());
      }

   }

   private boolean isFromSchedule(Execution execution) {
      return execution.getFlowTrace().isFromSchedule();
   }
}
