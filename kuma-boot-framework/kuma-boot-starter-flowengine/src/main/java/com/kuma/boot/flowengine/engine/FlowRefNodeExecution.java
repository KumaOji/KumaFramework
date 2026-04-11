package com.kuma.boot.flowengine.engine;

import com.kuma.boot.flowengine.module.FlowNode;
import com.kuma.boot.flowengine.module.FlowRef;
import java.sql.Timestamp;

public class FlowRefNodeExecution implements NodeExecution {
   private final Execution execution;
   private final FlowRef ref;

   public FlowRefNodeExecution(Execution execution, FlowRef ref) {
      this.execution = execution;
      this.ref = ref;
      execution.setCurrentNodeExecution(this);
   }

   public void execute() {
      Execution subExecution = this.execution.getEngine().execute(this.ref.getSubFlowName(), (String)null, this.ref.getVersion(), this.execution.getTarget(), this.execution.getAttachment());
      this.execution.setSubExecution(subExecution);
      this.ref.getCondition().execute(this.execution);
   }

   public FlowNode currentNode() {
      return this.ref;
   }

   public String decision() {
      return null;
   }

   public Timestamp getEndTime() {
      return this.execution.getSubExecution().getEndTime();
   }

   public Timestamp getStartTime() {
      return this.execution.getSubExecution().getStartTime();
   }

   public Throwable getError() {
      return null;
   }

   public ExecutionStatus getStatus() {
      return null;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("FlowRefNodeExecution{");
      sb.append("decision=").append(this.decision()).append('\'');
      sb.append("getEndTime=").append(this.getEndTime());
      sb.append("getError=").append(this.getError());
      sb.append("execution=").append(this.execution);
      sb.append("ref=").append(this.ref);
      sb.append(",currentNode=").append(this.currentNode());
      sb.append(",getStartTime=").append(this.getStartTime());
      sb.append(",getStatus=").append(this.getStatus());
      sb.append('}');
      return sb.toString();
   }
}
