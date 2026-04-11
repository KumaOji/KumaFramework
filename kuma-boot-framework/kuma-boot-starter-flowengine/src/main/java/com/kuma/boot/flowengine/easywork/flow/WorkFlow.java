package com.kuma.boot.flowengine.easywork.flow;

import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.report.WorkReport;
import com.kuma.boot.flowengine.easywork.work.Work;

public interface WorkFlow extends Work {
   WorkReport execute(WorkContext context);

   WorkFlow context(WorkContext workContext);
}
