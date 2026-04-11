package com.kuma.boot.flowengine.engine;

import com.kuma.boot.flowengine.annotation.After;
import com.kuma.boot.flowengine.annotation.Before;
import com.kuma.boot.flowengine.annotation.End;
import com.kuma.boot.flowengine.annotation.Error;
import com.kuma.boot.flowengine.annotation.Executor;
import com.kuma.boot.flowengine.delegate.InvokeDelegateContext;
import com.kuma.boot.flowengine.module.ActivityNode;
import com.kuma.boot.flowengine.module.Condition;
import com.kuma.boot.flowengine.module.Flow;
import com.kuma.boot.flowengine.module.FlowNode;
import com.kuma.boot.flowengine.state.TraceLogFactory;
import java.sql.Timestamp;
import org.slf4j.Logger;

public class StandardNodeExecution implements NodeExecution {
   private final Logger logger;
   private FlowNode currentFlowNode;
   private final Execution execution;
   private ExecutionStatus status;
   private String decision;
   private Timestamp startTime;
   private Timestamp endTime;
   private Throwable error;

   public StandardNodeExecution(Execution execution, FlowNode flowNode) {
      this.execution = execution;
      this.currentFlowNode = flowNode;
      execution.setCurrentNodeExecution(this);
      this.logger = TraceLogFactory.getLogger(execution.getCurrentFlow().getLogName());
      execution.getTrace().add(flowNode);
   }

   public void execute() {
      this.startTime = new Timestamp(System.currentTimeMillis());
      Flow flow = this.execution.getCurrentFlow();
      InvokeDelegateContext invokeDelegatecontext = this.execution.getEngine().getInvokeDelegateContext();
      Flow.Key flowKey = new Flow.Key(flow.getName(), flow.getVersion());
      String nodeName = this.currentFlowNode.getName();

      try {
         invokeDelegatecontext.invoke(new Object[]{flowKey, nodeName, Before.class, this.execution});
         Object executorResult = invokeDelegatecontext.invoke(new Object[]{flowKey, nodeName, Executor.class, this.execution});
         invokeDelegatecontext.invoke(new Object[]{flowKey, nodeName, After.class, this.execution});
         if (executorResult != null) {
            this.decision = executorResult.toString();
         }

         this.status = ExecutionStatus.SUCCESS;
      } catch (Throwable var10) {
         this.status = ExecutionStatus.FAIL;
         this.error = var10;
         Object result = invokeDelegatecontext.invoke(new Object[]{flowKey, nodeName, Error.class, this.execution});
         if (null == result && this.execution.isRetryEnable() && this.execution.getCurrentFlow().getRetryException().contains(var10.getClass())) {
            this.execution.setError(var10);
            result = this.tryToFindTheFirstRetryNodeIfPossible(this.currentFlowNode, this.execution);
         }

         if (null == result) {
            throw var10;
         }

         this.decision = result.toString();
      } finally {
         invokeDelegatecontext.invoke(new Object[]{flowKey, nodeName, End.class, this.execution});
         this.endTime = new Timestamp(System.currentTimeMillis());
         if (this.currentFlowNode.isTraceLog() && this.logger.isInfoEnabled()) {
            this.logger.info("\u6267\u884c\u5b8c\u6210NodeName=l},stateMachine={},Version={},NodeExecutionstatus={},Target={},Attachment={},\u8017\u65f6:{}\u4eb3\u79d2", new Object[]{this.currentFlowNode.getName(), flow.getName(), flow.getVersion(), this.status, this.execution.getTarget(), this.execution.getAttachment(), this.endTime.getTime() - this.startTime.getTime()});
         }

      }

      this.decision(this.currentFlowNode, this.execution);
   }

   private void decision(FlowNode flowNode, Execution execution) {
      if (flowNode instanceof ActivityNode activityNode) {
         Condition condition = activityNode.getCondition();
         condition.execute(execution);
      }

   }

   private String tryToFindTheFirstRetryNodeIfPossible(FlowNode flowNode, Execution execution) {
      return execution.isRetryEnable() && flowNode instanceof ActivityNode && null != flowNode.getRetryNode() ? "retry_from_target" : null;
   }

   public Throwable getError() {
      return this.error;
   }

   public void setError(Exception error) {
      this.error = error;
   }

   public Timestamp getEndTime() {
      return this.endTime;
   }

   public Timestamp getStartTime() {
      return this.startTime;
   }

   public ExecutionStatus getStatus() {
      return this.status;
   }

   public void setStatus(ExecutionStatus status) {
      this.status = status;
   }

   public FlowNode getCurrentFlowNode() {
      return this.currentFlowNode;
   }

   public FlowNode currentNode() {
      return this.currentFlowNode;
   }

   public String decision() {
      return this.decision;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("StandardNodeExecution{");
      sb.append("getEndTime=").append(this.getEndTime());
      sb.append("execution=").append(this.execution);
      sb.append("getError=").append(this.getError());
      sb.append(",getStartTime=").append(this.getStartTime());
      sb.append(",getStatus=").append(this.getStatus());
      sb.append('}');
      return sb.toString();
   }
}
