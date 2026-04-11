package com.kuma.boot.flowengine.delegate;

import com.kuma.boot.flowengine.engine.Execution;
import com.kuma.boot.flowengine.module.Flow;

public interface Script {
   String calculate(Execution execution, Flow.Key key, String nodeName);
}
