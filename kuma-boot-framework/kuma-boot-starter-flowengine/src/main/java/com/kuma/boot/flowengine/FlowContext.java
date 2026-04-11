package com.kuma.boot.flowengine;

import java.util.Map;

public interface FlowContext extends FlowDefReader, FlowDefRegistry {
   String ORDER_ID = "orderId";
   String FLOW_TRACE = "flowTrace";
   String RETRY_ENABLE = "retryEnable";
   String FID = "flowengineOid";

   void start(String flowName, int version, Object target);

   void start(String flowName, int version, Map<String, Object> attachment);

   void start(String flowName, int version, Object target, Map<String, Object> attachment);

   void start(String flowName, Object target);

   void start(String flowName, Map<String, Object> attachment);

   void start(String flowName, Object target, Map<String, Object> attachment);

   void execute(String flowName, String activeNode, Map<String, Object> attachment);

   void execute(String flowName, String activeNode, Object target);

   void execute(String flowName, String activeNode, Object target, Map<String, Object> attachment);

   void execute(String flowName, String activeNode, int version, Object target);

   void execute(String flowName, String activeNode, int version, Map<String, Object> attachment);

   void execute(String flowName, String activeNode, int version, Object target, Map<String, Object> attachment);
}
