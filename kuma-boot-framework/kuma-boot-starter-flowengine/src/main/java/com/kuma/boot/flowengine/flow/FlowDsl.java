package com.kuma.boot.flowengine.flow;

public interface FlowDsl<Context, Result> {
   default FlowDefinitionBuilder<Context, Result> start(String name) {
      return new FlowDefinitionBuilder<Context, Result>(name);
   }

   FlowDefinition<Context, Result> definition();
}
