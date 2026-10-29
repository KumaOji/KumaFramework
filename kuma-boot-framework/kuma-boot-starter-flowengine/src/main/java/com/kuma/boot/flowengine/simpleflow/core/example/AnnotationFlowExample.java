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
      LogUtils.info("开始注解流程示例", new Object[0]);

      try {
         AnnotationFlowConfigurationManager configManager = new AnnotationFlowConfigurationManager(new String[]{"com.kuma.boot.flowengine.simpleflow.core.example"});
         FlowEngine flowEngine = configManager.initialize();
         Map<String, Object> variables = new HashMap();
         variables.put("userName", "张三");
         variables.put("age", 25);
         variables.put("score", 85);
         FlowResult result = flowEngine.execute("UserProcessFlow2", variables);
         LogUtils.info("流程执行结果: {}", new Object[]{result.isSuccess()});
         LogUtils.info("流程输出数据: {}", new Object[]{result.getOutputData()});
      } catch (Exception e) {
         LogUtils.error("执行注解流程示例时发生错误", new Object[]{e});
      }

      LogUtils.info("注解流程示例结束", new Object[0]);
   }
}
