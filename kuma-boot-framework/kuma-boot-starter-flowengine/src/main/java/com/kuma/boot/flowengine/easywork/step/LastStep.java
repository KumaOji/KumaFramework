package com.kuma.boot.flowengine.easywork.step;

import com.kuma.boot.flowengine.easywork.flow.WorkFlow;
import com.kuma.boot.flowengine.easywork.work.Work;

public interface LastStep {
   WorkFlow lastly(Work work);
}
