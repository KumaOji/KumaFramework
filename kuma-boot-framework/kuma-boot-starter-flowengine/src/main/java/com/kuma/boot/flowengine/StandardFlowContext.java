package com.kuma.boot.flowengine;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.flowengine.exception.FlowException;
import java.util.Map;

public class StandardFlowContext extends AbstractFlowContext {
   private final String location;
   private boolean retryEnable = false;

   public StandardFlowContext(String location, boolean retryEnable) {
      if (StringUtils.isBlank(location)) {
         throw new FlowException(String.format("location=%s\u4e0d\u80fd\u4e3a\u7a7a", location));
      } else {
         this.location = location;
         this.retryEnable = retryEnable;
      }
   }

   public void afterPropertiesSet() throws Exception {
      super.afterPropertiesSet();
      this.loadDefinition(this.location);
   }

   public void start(String flowName, Object target) {
      this.start(flowName, -1, target);
   }

   public void start(String flowName, int version, Object target) {
      this.start(flowName, version, target, (Map)null);
   }

   public void start(String flowName, Map<String, Object> attachment) {
      this.start(flowName, (Object)null, attachment);
   }

   public void start(String flowName, int version, Map<String, Object> attachment) {
      this.start(flowName, version, (Object)null, attachment);
   }

   public void start(String flowName, Object target, Map<String, Object> attachment) {
      this.start(flowName, -1, target, attachment);
   }

   public void start(String flowName, int version, Object target, Map<String, Object> attachment) {
      this.execute(flowName, (String)null, version, target, attachment);
   }

   public void execute(String flowName, String activeNode, Map<String, Object> attachment) {
      this.execute(flowName, activeNode, (Object)null, attachment);
   }

   public void execute(String flowName, String activeNode, Object target) {
      this.execute(flowName, activeNode, target, (Map)null);
   }

   public void execute(String flowName, String activeNode, Object target, Map<String, Object> attachment) {
      this.execute(flowName, activeNode, -1, target, attachment);
   }

   public void execute(String flowName, String activeNode, int version, Object target) {
      this.execute(flowName, activeNode, version, target, (Map)null);
   }

   public void execute(String flowName, String activeNode, int version, Map<String, Object> attachment) {
      this.execute(flowName, activeNode, version, (Object)null, attachment);
   }

   public void execute(String flowName, String activeNode, int version, Object target, Map<String, Object> attachment) {
      if (this.retryEnable) {
         if (null == attachment) {
            throw new FlowException("\u91cd\u8bd5\u542f\u7528\u65f6,\u5fc5\u987b\u8981\u9644\u4ef6");
         }

         if (null == attachment.get("orderId")) {
            throw new FlowException("\u91cd\u8bd5\u542f\u7528\u65f6,\u9644\u4ef6\u5fc5\u987b\u6709orderId,\u8bf7attachment.put(FlowContext.ORDER_ID,orderId)");
         }

         attachment.put("retryEnable", true);
      }

      this.flowEngine.execute(flowName, activeNode, version, target, attachment);
   }
}
