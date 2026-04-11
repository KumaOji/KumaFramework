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
   name = "\u7528\u6237\u5904\u7406\u6d41\u7a0b",
   description = "\u6f14\u793a\u7528\u6237\u4fe1\u606f\u5904\u7406\u7684\u5b8c\u6574\u6d41\u7a0b",
   version = "1.0.0",
   enableParallel = false,
   timeout = 30000L
)
public class UserProcessFlow {
   public UserProcessFlow() {
   }

   @FlowStep(
      id = "validateUser",
      name = "\u9a8c\u8bc1\u7528\u6237\u4fe1\u606f",
      description = "\u9a8c\u8bc1\u7528\u6237\u8f93\u5165\u7684\u57fa\u672c\u4fe1\u606f",
      order = 1,
      type = FlowStep.StepType.SERVICE,
      timeout = 5000L
   )
   public boolean validateUser(FlowContext context) {
      LogUtils.info("\u5f00\u59cb\u9a8c\u8bc1\u7528\u6237\u4fe1\u606f", new Object[0]);
      String userName = (String)context.get("userName").orElse("");
      Integer age = (Integer)context.get("age").orElse(0);
      boolean isValid = !userName.trim().isEmpty() && age > 0;
      context.set("userValid", isValid);
      LogUtils.info("\u7528\u6237\u4fe1\u606f\u9a8c\u8bc1\u7ed3\u679c: {}", new Object[]{isValid});
      return isValid;
   }

   @ConditionalStep(
      id = "checkAge",
      name = "\u68c0\u67e5\u5e74\u9f84",
      description = "\u68c0\u67e5\u7528\u6237\u5e74\u9f84\u662f\u5426\u7b26\u5408\u8981\u6c42",
      order = 2,
      dependsOn = {"validateUser"},
      onTrue = {"processAdult"},
      onFalse = {"processMinor"},
      timeout = 3000L
   )
   public boolean checkAge(FlowContext context) {
      LogUtils.info("\u5f00\u59cb\u68c0\u67e5\u7528\u6237\u5e74\u9f84", new Object[0]);
      Integer age = (Integer)context.get("age").orElse(0);
      boolean isAdult = age >= 18;
      context.set("isAdult", isAdult);
      LogUtils.info("\u5e74\u9f84\u68c0\u67e5\u7ed3\u679c: {} (\u5e74\u9f84: {})", new Object[]{isAdult ? "\u6210\u5e74\u4eba" : "\u672a\u6210\u5e74\u4eba", age});
      return isAdult;
   }

   @FlowStep(
      id = "processAdult",
      name = "\u5904\u7406\u6210\u5e74\u7528\u6237",
      description = "\u5904\u7406\u6210\u5e74\u7528\u6237\u7684\u4e1a\u52a1\u903b\u8f91",
      order = 3,
      type = FlowStep.StepType.SERVICE,
      condition = "#{isAdult == true}",
      timeout = 8000L
   )
   public String processAdult(FlowContext context) {
      LogUtils.info("\u5f00\u59cb\u5904\u7406\u6210\u5e74\u7528\u6237", new Object[0]);
      String userName = (String)context.get("userName").orElse("\u672a\u77e5\u7528\u6237");
      Integer score = (Integer)context.get("score").orElse(0);
      String level = "\u666e\u901a";
      if (score >= 90) {
         level = "\u4f18\u79c0";
      } else if (score >= 80) {
         level = "\u826f\u597d";
      } else if (score >= 60) {
         level = "\u53ca\u683c";
      } else {
         level = "\u4e0d\u53ca\u683c";
      }

      String result = String.format("\u6210\u5e74\u7528\u6237 %s \u5904\u7406\u5b8c\u6210\uff0c\u7b49\u7ea7: %s", userName, level);
      context.set("processResult", result);
      LogUtils.info("\u6210\u5e74\u7528\u6237\u5904\u7406\u7ed3\u679c: {}", new Object[]{result});
      return result;
   }

   @FlowStep(
      id = "processMinor",
      name = "\u5904\u7406\u672a\u6210\u5e74\u7528\u6237",
      description = "\u5904\u7406\u672a\u6210\u5e74\u7528\u6237\u7684\u4e1a\u52a1\u903b\u8f91",
      order = 3,
      type = FlowStep.StepType.SERVICE,
      condition = "#{isAdult == false}",
      timeout = 6000L
   )
   public String processMinor(FlowContext context) {
      LogUtils.info("\u5f00\u59cb\u5904\u7406\u672a\u6210\u5e74\u7528\u6237", new Object[0]);
      String userName = (String)context.get("userName").orElse("\u672a\u77e5\u7528\u6237");
      String result = String.format("\u672a\u6210\u5e74\u7528\u6237 %s \u9700\u8981\u76d1\u62a4\u4eba\u540c\u610f", userName);
      context.set("processResult", result);
      context.set("needGuardianConsent", true);
      LogUtils.info("\u672a\u6210\u5e74\u7528\u6237\u5904\u7406\u7ed3\u679c: {}", new Object[]{result});
      return result;
   }

   @FlowStep(
      id = "generateReport",
      name = "\u751f\u6210\u5904\u7406\u62a5\u544a",
      description = "\u751f\u6210\u7528\u6237\u5904\u7406\u7684\u6700\u7ec8\u62a5\u544a",
      order = 4,
      dependsOn = {"processAdult", "processMinor"},
      type = FlowStep.StepType.SERVICE,
      timeout = 5000L
   )
   public Map<String, Object> generateReport(FlowContext context) {
      LogUtils.info("\u5f00\u59cb\u751f\u6210\u5904\u7406\u62a5\u544a", new Object[0]);
      Map<String, Object> report = new HashMap();
      report.put("userName", context.get("userName").orElse("\u672a\u77e5\u7528\u6237"));
      report.put("age", context.get("age").orElse(0));
      report.put("isAdult", context.get("isAdult").orElse(false));
      report.put("processResult", context.get("processResult").orElse("\u65e0\u5904\u7406\u7ed3\u679c"));
      report.put("timestamp", System.currentTimeMillis());
      if (Boolean.TRUE.equals(context.get("needGuardianConsent").orElse(false))) {
         report.put("needGuardianConsent", true);
      }

      context.set("finalReport", report);
      LogUtils.info("\u5904\u7406\u62a5\u544a\u751f\u6210\u5b8c\u6210: {}", new Object[]{report});
      return report;
   }
}
