package com.kuma.boot.flowengine.module;

import com.kuma.boot.flowengine.engine.Execution;
import com.kuma.boot.flowengine.engine.FlowRefNodeExecution;

public class FlowRef extends ActivityNode {
   private String subFlowName;
   private int version;

   public FlowRef(String name, String subFlowName, int version) {
      this.setName(name);
      this.subFlowName = subFlowName;
      this.version = version;
   }

   public void execute(final Execution execution) {
      (new FlowRefNodeExecution(execution, this)).execute();
   }

   public String getSubFlowName() {
      return this.subFlowName;
   }

   public int getVersion() {
      return this.version;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("FlowRef{");
      sb.append("name='").append(this.getName());
      sb.append(",subFlowName='").append(this.subFlowName);
      sb.append(",version='").append(this.version);
      sb.append(",triggerClass='").append(this.getTriggerClass());
      sb.append('}');
      return sb.toString();
   }
}
