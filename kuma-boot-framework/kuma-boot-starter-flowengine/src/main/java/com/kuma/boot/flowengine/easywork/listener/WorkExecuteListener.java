package com.kuma.boot.flowengine.easywork.listener;

import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.report.DefaultWorkReport;

@FunctionalInterface
public interface WorkExecuteListener {
   void onWorkExecute(DefaultWorkReport workReport, WorkContext workContext, Exception exception);
}
