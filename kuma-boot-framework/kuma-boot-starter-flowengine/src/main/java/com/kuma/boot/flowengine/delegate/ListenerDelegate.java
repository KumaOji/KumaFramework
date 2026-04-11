package com.kuma.boot.flowengine.delegate;

import com.kuma.boot.flowengine.engine.Execution;

public interface ListenerDelegate {
   void action(Execution execution, String eventName);
}
