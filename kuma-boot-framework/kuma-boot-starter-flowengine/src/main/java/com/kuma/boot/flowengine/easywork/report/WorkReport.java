package com.kuma.boot.flowengine.easywork.report;

import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.step.ExecuteStep;
import com.kuma.boot.flowengine.easywork.work.WorkStatus;

public interface WorkReport extends ExecuteStep {
   WorkStatus getStatus();

   Throwable getError();

   WorkContext getWorkContext();

   Object getResult();

   String getWorkName();
}
