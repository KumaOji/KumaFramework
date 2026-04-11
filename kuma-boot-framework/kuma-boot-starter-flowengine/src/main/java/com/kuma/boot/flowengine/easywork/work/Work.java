package com.kuma.boot.flowengine.easywork.work;

import com.kuma.boot.flowengine.easywork.context.WorkContext;

@FunctionalInterface
public interface Work {
   Object execute(WorkContext context);
}
