package com.kuma.boot.flowengine;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.flowengine.exception.FlowException;
import java.util.Map;

public class StandardFlowContext extends AbstractFlowContext {
   private final String location;
   private boolean retryEnable = false;

   public StandardFlowContext(String location, boolean retryEnable) {
      if (StringUtils.isBlank(location)) {
         throw new FlowException(String.format("location=%s不能为空", location));
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
            throw new FlowException("重试启用时,必须要附件");
         }

         if (null == attachment.get("orderId")) {
            throw new FlowException("重试启用时,附件必须有orderId,请attachment.put(FlowContext.ORDER_ID,orderId)");
         }

         attachment.put("retryEnable", true);
      }

      this.flowEngine.execute(flowName, activeNode, version, target, attachment);
   }
}
