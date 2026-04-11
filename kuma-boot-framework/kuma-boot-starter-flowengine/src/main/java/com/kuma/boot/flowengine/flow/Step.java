package com.kuma.boot.flowengine.flow;

public interface Step<Context, Result> {
   boolean hasAction(Context context);

   FlowResultHolder<Result> execute(Context context);

   String getName();

   String getFlowName();
}
