package com.kuma.boot.flowengine.engine;

import com.google.common.collect.Maps;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.flowengine.delegate.InvokeDelegateContext;
import com.kuma.boot.flowengine.delegate.ListenerDelegateContext;
import com.kuma.boot.flowengine.delegate.MvelScriptContext;
import com.kuma.boot.flowengine.exception.FlowException;
import com.kuma.boot.flowengine.module.ActivityNode;
import com.kuma.boot.flowengine.module.ErrorMonitor;
import com.kuma.boot.flowengine.module.EventListener;
import com.kuma.boot.flowengine.module.EventListeners;
import com.kuma.boot.flowengine.module.Flow;
import com.kuma.boot.flowengine.module.FlowNode;
import java.util.Iterator;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class FlowEngine implements InitializingBean, ApplicationContextAware {
   private InvokeDelegateContext invokeDelegateContext;
   private ListenerDelegateContext listenerDelegateContext;
   private MvelScriptContext mvelScriptContext;
   private Map<Flow.Key, Flow> flowsHolder = Maps.newHashMap();
   private Map<Flow.Key, ExceptionMonitor> flowExceptionMonitorHolder = Maps.newHashMap();
   private ApplicationContext applicationContext;

   public FlowEngine() {
   }

   public void afterPropertiesSet() throws Exception {
      this.invokeDelegateContext = new InvokeDelegateContext(this.applicationContext);
      this.mvelScriptContext = new MvelScriptContext(this.applicationContext);
      this.listenerDelegateContext = new ListenerDelegateContext(this.applicationContext);
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
   }

   public void registry(Flow flow) {
      flow.validate();
      flow.initialize((Flow)null);
      Flow.Key key = new Flow.Key(flow.getName(), flow.getVersion());
      if (this.flowsHolder.containsKey(key)) {
         throw new FlowException(String.format("\u91cd\u590d\u7684\u6d41\u7a0b\u5b9a\u4e49FLow=%s,Version=%s", flow.getName(), flow.getVersion()));
      } else {
         this.listenersInitializer(flow);
         this.actionInitializer(flow);
         this.monitorInitializer(flow);
         this.flowsHolder.put(key, flow);
      }
   }

   private void listenersInitializer(Flow flow) {
      EventListeners eventListeners = flow.getEventListeners();
      if (eventListeners != null) {
         for(EventListener eventListener : eventListeners.listeners()) {
            this.listenerDelegateContext.register(flow, eventListener);
         }
      }

   }

   private void monitorInitializer(Flow flow) {
      ErrorMonitor monitor = flow.getErrorMonitor();
      if (monitor != null) {
         String monitorClass = monitor.getErrorMonitorClass();

         try {
            Class<ExceptionMonitor> monitorType = Class.forName(monitorClass);
            Map<String, ExceptionMonitor> monitorMap = this.applicationContext.getBeansOfType(monitorType);
            Iterator<Map.Entry<String, ExceptionMonitor>> monitorIterator = monitorMap.entrySet().iterator();
            if (!monitorIterator.hasNext()) {
               throw new FlowException(String.format("\u6d41\u7a0b\u5b9a\u4e49Flow=%s,Version=%s \u9519\u8bef\u76d1\u89c6\u5668\u6ca1\u6709\u83b7\u53d6\u5230\u6b63\u786e\u7684spring", flow.getName(), flow.getVersion()));
            }

            this.flowExceptionMonitorHolder.put(new Flow.Key(flow.getName(), flow.getVersion()), (ExceptionMonitor)((Map.Entry)monitorIterator.next()).getValue());
         } catch (ClassNotFoundException var7) {
            throw new FlowException(String.format("\u6d41\u7a0b\u5b9a\u4e49Flow=%s,Version=%s \u9519\u8bef\u76d1\u89c6\u5668\u5b9a\u4e49\u51fa\u9519MonitorClass=%s", flow.getName(), flow.getVersion(), monitorClass));
         }
      }

   }

   private void actionInitializer(Flow flow) {
      Flow.Key key = new Flow.Key(flow.getName(), flow.getVersion());
      this.invokeDelegateContext.proceed(key, flow.getStartNode().getName(), flow.getStartNode().getTriggerClass());
      this.invokeDelegateContext.proceed(key, flow.getEndNode().getName(), flow.getEndNode().getTriggerClass());

      for(ActivityNode node : flow.getNodes()) {
         String triggerClass = node.getTriggerClass();
         String mvelScript = node.getCondition().getMvelScript();
         this.invokeDelegateContext.proceed(key, node.getName(), triggerClass);
         this.mvelScriptContext.proceed(key, node.getName(), mvelScript);
      }

   }

   public Execution execute(String flowName, String activeNode, int version, Object target, Map<String, Object> args) {
      Flow flow = this.obtain(flowName, version);
      FlowNode node = null;
      Execution execution = new Execution(this, flow, target, args);
      if (StringUtils.isBlank(activeNode)) {
         node = flow;
      } else {
         String startNodeName = flow.getStartNode().getName();
         String endNodeName = flow.getEndNode().getName();
         if (activeNode.equals(startNodeName)) {
            node = flow.getStartNode();
         } else if (activeNode.equals(endNodeName)) {
            node = flow.getEndNode();
         } else {
            for(ActivityNode nd : flow.getNodes()) {
               if (activeNode.equals(nd.getName())) {
                  node = nd;
                  break;
               }
            }
         }
      }

      if (node == null) {
         throw new FlowException(String.format("\u6307\u5b9a\u6267\u884c\u8282\u70b9\u4e0d\u5b58\u5728Flow=%s,Version=%s,activeNode=%s", flow, version, activeNode));
      } else {
         execution.execute(flow, node);
         return execution;
      }
   }

   private Flow obtain(String flowName, int version) {
      Flow.Key key = new Flow.Key(flowName, version <= 0 ? 1 : version);
      Flow flow = (Flow)this.flowsHolder.get(key);
      if (flow == null) {
         throw new FlowException(String.format("\u6d41\u7a0b\u5b9a\u4e49\u4e0d\u5b58\u5728Flow=%s,version=%s", flowName, version));
      } else {
         return flow;
      }
   }

   public Map<Flow.Key, ExceptionMonitor> getFlowExceptionMonitorHolder() {
      return this.flowExceptionMonitorHolder;
   }

   public MvelScriptContext getMvelScriptContext() {
      return this.mvelScriptContext;
   }

   public InvokeDelegateContext getInvokeDelegateContext() {
      return this.invokeDelegateContext;
   }

   public ListenerDelegateContext getListenerDelegateContext() {
      return this.listenerDelegateContext;
   }
}
