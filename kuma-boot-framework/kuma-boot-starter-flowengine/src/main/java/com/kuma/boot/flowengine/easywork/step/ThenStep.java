package com.kuma.boot.flowengine.easywork.step;

import com.kuma.boot.flowengine.easywork.flow.WorkFlow;
import com.kuma.boot.flowengine.easywork.report.WorkReport;
import com.kuma.boot.flowengine.easywork.work.Work;
import java.util.function.Function;

public interface ThenStep extends WorkFlow {
   WorkFlow then(Function<WorkReport, Work> fun);

   WorkFlow then(Work work);
}
