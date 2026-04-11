package com.kuma.boot.flowengine.simpleflow.core.example;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.api.FlowEngine;
import com.kuma.boot.flowengine.simpleflow.api.model.FlowResult;
import com.kuma.boot.flowengine.simpleflow.core.annotation.AnnotationFlowConfigurationManager;
import java.util.HashMap;
import java.util.Map;

public class AnnotationFlowExample {
   public AnnotationFlowExample() {
   }

   public static void main(String[] args) {
      LogUtils.info("\u5f00\u59cb\u6ce8\u89e3\u6d41\u7a0b\u793a\u4f8b", new Object[0]);

      try {
         AnnotationFlowConfigurationManager configManager = new AnnotationFlowConfigurationManager(new String[]{"com.kuma.boot.flowengine.simpleflow.core.example"});
         FlowEngine flowEngine = configManager.initialize();
         Map<String, Object> variables = new HashMap();
         variables.put("userName", "\u5f20\u4e09");
         variables.put("age", 25);
         variables.put("score", 85);
         FlowResult result = flowEngine.execute("UserProcessFlow2", variables);
         LogUtils.info("\u6d41\u7a0b\u6267\u884c\u7ed3\u679c: {}", new Object[]{result.isSuccess()});
         LogUtils.info("\u6d41\u7a0b\u8f93\u51fa\u6570\u636e: {}", new Object[]{result.getOutputData()});
      } catch (Exception e) {
         LogUtils.error("\u6267\u884c\u6ce8\u89e3\u6d41\u7a0b\u793a\u4f8b\u65f6\u53d1\u751f\u9519\u8bef", new Object[]{e});
      }

      LogUtils.info("\u6ce8\u89e3\u6d41\u7a0b\u793a\u4f8b\u7ed3\u675f", new Object[0]);
   }
}
