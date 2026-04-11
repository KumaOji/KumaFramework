package com.kuma.boot.flowengine.module;

import com.kuma.boot.flowengine.engine.Execution;

public class StartNode extends ActivityNode {
   public StartNode() {
   }

   public void execute(Execution execution) {
      execution.markFromStart();
      super.execute(execution);
   }
}
