package com.kuma.boot.flowengine.engine;

import com.kuma.boot.flowengine.module.Flow;
import com.kuma.boot.flowengine.module.FlowNode;

public interface ExceptionMonitor {
   void catcher(Flow flow, FlowNode node, Execution execution, Throwable throwable);
}
