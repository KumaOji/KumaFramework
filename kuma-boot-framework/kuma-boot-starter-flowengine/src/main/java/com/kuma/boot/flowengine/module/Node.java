package com.kuma.boot.flowengine.module;

import com.kuma.boot.flowengine.engine.Execution;

public interface Node {
   void validate();

   void execute(Execution execution);

   void initialize(Flow flow);
}
