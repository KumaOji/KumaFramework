package com.kuma.boot.flowengine.easywork.enignee;

import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.flow.WorkFlow;
import com.kuma.boot.flowengine.easywork.report.WorkReport;

public interface WorkFlowEngine {
   WorkReport run(WorkFlow workFlow, WorkContext workContext);
}
