package com.kuma.boot.flowengine.easywork.flow;

import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.report.WorkReport;

public interface PointWorkFlow extends WorkFlow {
   WorkReport execute(WorkContext context, String point);
}
