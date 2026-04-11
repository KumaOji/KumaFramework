package com.kuma.boot.flowengine.engine;

import com.kuma.boot.flowengine.exception.FlowException;
import com.kuma.boot.flowengine.module.Flow;
import com.kuma.boot.flowengine.module.FlowNode;
import com.kuma.boot.flowengine.module.StartNode;
import com.kuma.boot.flowengine.state.FlowTrace;
import com.kuma.boot.flowengine.state.TraceLogFactory;
import java.sql.Timestamp;
import java.util.Map;
import org.slf4j.Logger;

public class Execution {
   private final Logger logger;
   private final FlowEngine engine;
   private final Flow currentFlow;
   private final Map<String, Object> attachment;
   private Object target;
   private boolean fromStart;
   private Execution subExecution;
   private Timestamp startTime;
   private Timestamp endTime;
   private Throwable error;
   private NodeExecution currentNodeExecution;
   private ExecuteNodeTrace trace;
   private boolean isRetryEnable = false;
   private FlowTrace flowTrace;

   public Execution(FlowEngine engine, Flow currentFlow, Object target, Map<String, Object> attachment) {
      this.engine = engine;
      this.currentFlow = currentFlow;
      this.target = target;
      this.attachment = attachment;
      this.logger = TraceLogFactory.getLogger(currentFlow.getLogName());
      this.trace = new ExecuteNodeTrace();
      this.init();
   }

   private void init() {
      if (null != this.attachment) {
         if (this.attachment.containsKey("flowTrace")) {
            FlowTrace trace = (FlowTrace)this.attachment.get("flowTrace");
            this.setFlowTrace(trace);
         }

         if (this.attachment.containsKey("retryEnable")) {
            this.setIsRetryEnable(true);
            this.attachment.remove("retryEnable");
         }
      }

   }

   public String orderId() {
      return (String)this.getAttribute("orderId");
   }

   public ExecuteNodeTrace getTrace() {
      return this.trace;
   }

   public void execute(Flow flow, FlowNode node) {
      this.startTime = new Timestamp(System.currentTimeMillis());
      if (this.logger.isInfoEnabled()) {
         if (node instanceof StartNode) {
            this.logger.info("\u542f\u52a8\u6d41\u7a0bFow={},Version={},\u6267\u884c\u8282\u70b9Node={},Target={},Attachment={}", new Object[]{flow.getName(), flow.getVersion(), flow.equals(node) ? flow.getStartNode().getName() : node.getName(), this.target, this.attachment});
         } else {
            this.logger.info("\u6fc0\u6d3b\u6d41\u7a0bFLow={},Version={},\u6267\u884c\u8282\u70b9Node={},Target={},Attachment={}", new Object[]{flow.getName(), flow.getVersion(), flow.equals(node) ? flow.getStartNode().getName() : node.getName(), this.target, this.attachment});
         }
      }

      try {
         node.execute(this);
      } catch (Throwable var12) {
         Throwable throwable = var12;
         this.error = var12;
         if (var12 instanceof Error && this.logger.isErrorEnabled()) {
            this.logger.error("\u6d41\u7a0b\u6267\u884c\u51fa\u73b0ERROR\u7ea7\u9519\u8bef,\u65e0\u6cd5\u76f4\u63a5\u5904\u7406,\u5ffd\u7565\u5f02\u5e38\u76d1\u89c6\u5668,FLow={},version={},\u6267\u884c\u8282\u70b9Node=[name={}.class={}]", new Object[]{flow.getName(), flow.getVersion(), node.getName(), node.getClass()});
            throw var12;
         }

         Flow.Key key = new Flow.Key(flow.getName(), flow.getVersion());
         ExceptionMonitor monitor = (ExceptionMonitor)this.engine.getFlowExceptionMonitorHolder().get(key);
         boolean match = false;
         if (null != monitor) {
            for(Class<? extends Throwable> ex : flow.getErrorMonitor().getExceptionMapping().getThrowables()) {
               if (ex.isInstance(throwable)) {
                  monitor.catcher(flow, node, this, throwable);
                  match = true;
                  break;
               }
            }
         }

         if (!match) {
            if (this.logger.isWarnEnabled()) {
               this.logger.warn("\u6267\u884c\u6d41\u7a0b\u51fa\u9519,\u5e76\u6ca1\u6709\u53d1\u73b0\u9519\u8bef\u76d1\u89c6\u5668\u6216\u5bf9\u76f8\u5e94\u9519\u8bef\u8fdb\u884c\u76d1\u89c6,Flow={},Version={},Throwable={},\u6267\u884c\u8282\u70b9Node=[name={},class={}]", new Object[]{flow.getName(), flow.getVersion(), throwable.getClass().getName(), this.currentNodeExecution.currentNode().getName(), this.currentNodeExecution.currentNode().getClass()});
            }

            throw throwable;
         }
      } finally {
         this.endTime = new Timestamp(System.currentTimeMillis());
         if (this.logger.isInfoEnabled()) {
            this.logger.info("\u6d41\u7a0b\u6267\u884c\u7ed3\u675f\u7ed3\u675fFlow={},Version={},CurrentNode={},Target={},Attachment={},\u603b.\u8017\u65f6:{}\u6beb\u79d2", new Object[]{flow.getName(), flow.getVersion(), this.currentNodeExecution.currentNode().getName(), this.target, this.attachment, this.endTime.getTime() - this.startTime.getTime()});
         }

      }

   }

   public void setCurrentNodeExecution(NodeExecution currentNodeExecution) {
      this.currentNodeExecution = currentNodeExecution;
   }

   public NodeExecution currentNodeExecution() {
      return this.currentNodeExecution;
   }

   public FlowEngine getEngine() {
      return this.engine;
   }

   public Map<String, Object> getAttachment() {
      return this.attachment;
   }

   public <T> T getAttribute(String key) {
      if (this.attachment == null) {
         throw new FlowException("\u4e0d\u652f\u6301\u7684\u83b7\u53d6\u64cd\u4f5c,attachment\u6ca1\u6709\u521d\u59cb\u5316\u3002");
      } else {
         return (T)(null == this.attachment.get(key) ? null : this.attachment.get(key));
      }
   }

   public <T> void addAttribute(String key, T attribute) {
      if (this.attachment == null) {
         throw new FlowException("\u4e0d\u652f\u6301\u7684\u6dfb\u52a0\u64cd\u4f5c,attachment\u6ca1\u6709\u521d\u59cb\u5316\u3002");
      } else {
         this.attachment.put(key, attribute);
      }
   }

   public boolean isRetryEnable() {
      return this.isRetryEnable;
   }

   public void setIsRetryEnable(boolean isRetryEnable) {
      this.isRetryEnable = isRetryEnable;
   }

   public FlowTrace getFlowTrace() {
      if (null != this.flowTrace) {
         this.flowTrace.updateMeta(this.getTarget(), this.getAttachment());
      }

      return this.flowTrace;
   }

   public void setFlowTrace(FlowTrace flowTrace) {
      this.flowTrace = flowTrace;
      if (null != this.flowTrace) {
         this.flowTrace.updateMeta(this.getTarget(), this.getAttachment());
      }

   }

   public <T> T getTarget() {
      return (T)this.target;
   }

   public void setTarget(Object target) {
      this.target = target;
   }

   public Throwable getError() {
      return this.error;
   }

   public void setError(Throwable error) {
      this.error = error;
   }

   public Execution getSubExecution() {
      return this.subExecution;
   }

   public Flow getCurrentFlow() {
      return this.currentFlow;
   }

   public Timestamp getStartTime() {
      return this.startTime;
   }

   public Timestamp getEndTime() {
      return this.endTime;
   }

   public NodeExecution getCurrentNodeExecution() {
      return this.currentNodeExecution;
   }

   public void setSubExecution(Execution subExecution) {
      this.subExecution = subExecution;
   }

   public Logger getLogger() {
      return this.logger;
   }

   public void markFromStart() {
      this.fromStart = true;
   }

   public boolean isExecuteFromStartNode() {
      return this.fromStart;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("Execution{");
      sb.append("Target=").append(this.target);
      sb.append(",Attachment=").append(this.attachment);
      sb.append(",StateMachine=").append(this.currentFlow.getName());
      sb.append(",Version=").append(this.currentFlow.getVersion());
      sb.append('}');
      return sb.toString();
   }
}
