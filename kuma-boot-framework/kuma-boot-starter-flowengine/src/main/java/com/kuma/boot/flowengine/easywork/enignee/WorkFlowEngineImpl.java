package com.kuma.boot.flowengine.easywork.enignee;

import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.flow.PointWorkFlow;
import com.kuma.boot.flowengine.easywork.flow.WorkFlow;
import com.kuma.boot.flowengine.easywork.report.WorkReport;

public class WorkFlowEngineImpl implements WorkFlowEngine {
   private WorkFlowEngineImpl() {
   }

   public WorkReport run(WorkFlow workFlow, WorkContext workContext) {
      return workFlow.execute(workContext);
   }

   public WorkReport run(PointWorkFlow workFlow, WorkContext workContext, String point) {
      return workFlow.execute(workContext, point);
   }

   public static WorkFlowEngine aNewWorkFlowEngine() {
      return new WorkFlowEngineImpl();
   }
}
