package com.kuma.boot.flowengine.simpleflow.core.example;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.api.FlowContext;
import com.kuma.boot.flowengine.simpleflow.core.annotation.ConditionalStep;
import com.kuma.boot.flowengine.simpleflow.core.annotation.FlowDefinition;
import com.kuma.boot.flowengine.simpleflow.core.annotation.FlowStep;
import java.util.HashMap;
import java.util.Map;

@FlowDefinition(
   id = "UserProcessFlow2",
   name = "用户处理流程",
   description = "演示用户信息处理的完整流程",
   version = "1.0.0",
   enableParallel = false,
   timeout = 30000L
)
public class UserProcessFlow {
   public UserProcessFlow() {
   }

   @FlowStep(
      id = "validateUser",
      name = "验证用户信息",
      description = "验证用户输入的基本信息",
      order = 1,
      type = FlowStep.StepType.SERVICE,
      timeout = 5000L
   )
   public boolean validateUser(FlowContext context) {
      LogUtils.info("开始验证用户信息", new Object[0]);
      String userName = (String)context.get("userName").orElse("");
      Integer age = (Integer)context.get("age").orElse(0);
      boolean isValid = !userName.trim().isEmpty() && age > 0;
      context.set("userValid", isValid);
      LogUtils.info("用户信息验证结果: {}", new Object[]{isValid});
      return isValid;
   }

   @ConditionalStep(
      id = "checkAge",
      name = "检查年龄",
      description = "检查用户年龄是否符合要求",
      order = 2,
      dependsOn = {"validateUser"},
      onTrue = {"processAdult"},
      onFalse = {"processMinor"},
      timeout = 3000L
   )
   public boolean checkAge(FlowContext context) {
      LogUtils.info("开始检查用户年龄", new Object[0]);
      Integer age = (Integer)context.get("age").orElse(0);
      boolean isAdult = age >= 18;
      context.set("isAdult", isAdult);
      LogUtils.info("年龄检查结果: {} (年龄: {})", new Object[]{isAdult ? "成年人" : "未成年人", age});
      return isAdult;
   }

   @FlowStep(
      id = "processAdult",
      name = "处理成年用户",
      description = "处理成年用户的业务逻辑",
      order = 3,
      type = FlowStep.StepType.SERVICE,
      condition = "#{isAdult == true}",
      timeout = 8000L
   )
   public String processAdult(FlowContext context) {
      LogUtils.info("开始处理成年用户", new Object[0]);
      String userName = (String)context.get("userName").orElse("未知用户");
      Integer score = (Integer)context.get("score").orElse(0);
      String level = "普通";
      if (score >= 90) {
         level = "优秀";
      } else if (score >= 80) {
         level = "良好";
      } else if (score >= 60) {
         level = "及格";
      } else {
         level = "不及格";
      }

      String result = String.format("成年用户 %s 处理完成，等级: %s", userName, level);
      context.set("processResult", result);
      LogUtils.info("成年用户处理结果: {}", new Object[]{result});
      return result;
   }

   @FlowStep(
      id = "processMinor",
      name = "处理未成年用户",
      description = "处理未成年用户的业务逻辑",
      order = 3,
      type = FlowStep.StepType.SERVICE,
      condition = "#{isAdult == false}",
      timeout = 6000L
   )
   public String processMinor(FlowContext context) {
      LogUtils.info("开始处理未成年用户", new Object[0]);
      String userName = (String)context.get("userName").orElse("未知用户");
      String result = String.format("未成年用户 %s 需要监护人同意", userName);
      context.set("processResult", result);
      context.set("needGuardianConsent", true);
      LogUtils.info("未成年用户处理结果: {}", new Object[]{result});
      return result;
   }

   @FlowStep(
      id = "generateReport",
      name = "生成处理报告",
      description = "生成用户处理的最终报告",
      order = 4,
      dependsOn = {"processAdult", "processMinor"},
      type = FlowStep.StepType.SERVICE,
      timeout = 5000L
   )
   public Map<String, Object> generateReport(FlowContext context) {
      LogUtils.info("开始生成处理报告", new Object[0]);
      Map<String, Object> report = new HashMap();
      report.put("userName", context.get("userName").orElse("未知用户"));
      report.put("age", context.get("age").orElse(0));
      report.put("isAdult", context.get("isAdult").orElse(false));
      report.put("processResult", context.get("processResult").orElse("无处理结果"));
      report.put("timestamp", System.currentTimeMillis());
      if (Boolean.TRUE.equals(context.get("needGuardianConsent").orElse(false))) {
         report.put("needGuardianConsent", true);
      }

      context.set("finalReport", report);
      LogUtils.info("处理报告生成完成: {}", new Object[]{report});
      return report;
   }
}
