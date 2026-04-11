package com.kuma.boot.flowengine.simpleflow.api;

import com.kuma.boot.flowengine.simpleflow.api.model.StepResult;

public interface StepHandler {
   StepResult execute(FlowContext context);
}
