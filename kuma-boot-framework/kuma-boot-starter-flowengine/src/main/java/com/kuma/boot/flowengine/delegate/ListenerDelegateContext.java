package com.kuma.boot.flowengine.delegate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.flowengine.Compiler;
import com.kuma.boot.flowengine.annotation.Listen;
import com.kuma.boot.flowengine.engine.Execution;
import com.kuma.boot.flowengine.exception.FlowException;
import com.kuma.boot.flowengine.module.EventListener;
import com.kuma.boot.flowengine.module.Flow;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javassist.CtClass;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;

public class ListenerDelegateContext implements ListenerDelegate {
   private ApplicationContext applicationContext;
   private Map<Flow.Key, Map<String, List<AbstractListenerDelegate>>> listenerHolder = Maps.newConcurrentMap();

   public ListenerDelegateContext(ApplicationContext applicationContext) {
      this.applicationContext = applicationContext;
   }

   public void action(Execution execution, String eventName) {
      Flow flow = execution.getCurrentFlow();
      Flow.Key key = new Flow.Key(flow.getName(), flow.getVersion());
      Map<String, List<AbstractListenerDelegate>> eventListeners = (Map)this.listenerHolder.get(key);
      if (eventListeners != null && !eventListeners.isEmpty()) {
         List<AbstractListenerDelegate> listenerDelegates = (List)eventListeners.get(eventName);
         if (listenerDelegates != null && !listenerDelegates.isEmpty()) {
            for(AbstractListenerDelegate each : listenerDelegates) {
               each.action(execution, eventName);
            }

         }
      }
   }

   public void register(Flow flow, EventListener eventListener) {
      Flow.Key key = new Flow.Key(flow.getName(), flow.getVersion());
      Map<String, List<AbstractListenerDelegate>> eventListeners = (Map)this.listenerHolder.get(key);
      if (eventListeners == null) {
         eventListeners = Maps.newConcurrentMap();
         this.listenerHolder.put(key, eventListeners);
         int i = 0;

         for(int j = flow.getEvents().size(); i < j; ++i) {
            String event = (String)flow.getEvents().get(i);
            List<AbstractListenerDelegate> listenerDelegates = Lists.newArrayList();
            eventListeners.put(event, listenerDelegates);
         }
      }

      String description = eventListener.getDescription();
      String eventListenerClass = eventListener.getListenerClass();

      try {
         Class<?> eventListenerType = Class.forName(eventListenerClass);
         Map<String, Object> listenerBeans = this.applicationContext.getBeansOfType(eventListenerType);
         Iterator<Map.Entry<String, Object>> listenerBeansIterator = listenerBeans.entrySet().iterator();
         if (!listenerBeansIterator.hasNext()) {
            throw new FlowException(String.format("Flow=%s,version=%s\u5904\u7406\u5668\u7c7b\u578b\u5206\u6790\u5931\u8d25,\u7c7b\u578b\u4e3alistenerClass=%s\u7684\u76d1\u542c\u5668\u4e0d\u5b58\u5728\u76f8\u5e94\u7684spring bean\u5b9a\u4e49", key.getFlowName(), key.getVersion(), eventListenerClass));
         } else if (listenerBeans.size() != 1) {
            throw new FlowException(String.format("Flow=%s, version=%s\u5904\u7406\u5668\u7c7b\u578b\u5206\u6790\u5931\u8d25,\u7c7b\u578b\u4e3alistenerClass=%s\u7684\u4e0a\u706f\u5668\u5b9a\u4e49\u5b58\u5728\u591a\u4e2a\u5b9e\u6cb3\u7248\u672c.,beans=%s", key.getFlowName(), key.getVersion(), eventListenerClass, listenerBeans));
         } else {
            Object listenerBean = ((Map.Entry)listenerBeansIterator.next()).getValue();
            if (AopUtils.isAopProxy(listenerBean)) {
               throw new FlowException(String.format(":\u6d41\u7a0b\u5b9aFLowe=%s,version=%s\u4e8b\u4ef6\u76d1\u542c\u5668\u5206\u6790\u5148\u8d25,\u7c7b\u578b\u4e3atargetType=%s\u7684\u7247\u542c\u5668\u662f\u4e00\u4e2a\u52a8\u6001\u4ee3\u7406\u5bf9\u8c61->(%s)", flow.getName(), flow.getVersion(), ((Advised)listenerBean).getTargetSource().getTarget().getClass().getName(), listenerBean));
            } else {
               Class<?> supperType = eventListenerType;

               do {
                  for(Method method : supperType.getDeclaredMethods()) {
                     if (method.isAnnotationPresent(Listen.class)) {
                        Listen listen = (Listen)method.getAnnotation(Listen.class);
                        String eventExpression = listen.eventExpression();
                        int priority = listen.priority();
                        AbstractListenerDelegate delegate = this.dynamicBuilder(key, listenerBean, method, priority);
                        if ("*".equals(eventExpression)) {
                           Iterator<Map.Entry<String, List<AbstractListenerDelegate>>> it = eventListeners.entrySet().iterator();

                           while(it.hasNext()) {
                              this.addAndSort((List)((Map.Entry)it.next()).getValue(), delegate);
                           }
                        } else {
                           boolean isBreak = false;

                           for(String event : flow.getEvents()) {
                              if (event.matches(eventExpression)) {
                                 this.addAndSort((List)eventListeners.get(event), delegate);
                                 isBreak = true;
                              }
                           }

                           if (!isBreak && eventExpression.matches("^(!\\$+|\\$+)(,!\\S+|\\$+)+$")) {
                              String[] eventExpressions = eventExpression.split(",");

                              for(String event : flow.getEvents()) {
                                 for(String expression : eventExpressions) {
                                    if (!expression.startsWith("!") && StringUtils.isNotBlank(expression) && event.equals(expression)) {
                                       isBreak = true;
                                       break;
                                    }
                                 }

                                 if (isBreak) {
                                    this.addAndSort((List)eventListeners.get(event), delegate);
                                    isBreak = false;
                                 }
                              }
                           }
                        }
                     }
                  }
               } while((supperType = supperType.getSuperclass()) != Object.class);

            }
         }
      } catch (Exception e) {
         if (e instanceof FlowException) {
            throw (FlowException)e;
         } else {
            throw new FlowException(String.format(":\u7684!\u51fa\u5e7f\u5668EXListenerclas=%s,Description=%s,stateachine=%s,version=%s", eventListeners, description, key.getFlowName(), key.getVersion()), e);
         }
      }
   }

   private void addAndSort(List<AbstractListenerDelegate> delegates, AbstractListenerDelegate delegate) {
      if (!delegates.contains(delegate)) {
         delegates.add(delegate);
         Collections.sort(delegates);
      }

   }

   private AbstractListenerDelegate dynamicBuilder(Flow.Key key, Object listenerBean, Method method, int priority) {
      Class<?>[] parameterTypes = method.getParameterTypes();
      Class<?> returnType = method.getReturnType();
      this.valid(key, listenerBean, method, parameterTypes, returnType);
      Compiler compiler = Compiler.getInstance();
      CtClass ctclass = compiler.newCtClass(AbstractListenerDelegate.class);
      StringBuilder constructScript = new StringBuilder();
      constructScript.append("public ").append(ctclass.getSimpleName()).append("(Object target,int priority){\n\t").append("super(target,priority);\n").append("}\n");
      StringBuilder actionScript = new StringBuilder();
      actionScript.append("public void action(com.kuma.boot.flowengine.engine.Execution execution , String eventName){\n\t").append("((").append(listenerBean.getClass().getName()).append(")getTarget()).").append(method.getName()).append("(execution,eventName);\n}\n");
      compiler.constructImplement(ctclass, constructScript.toString());
      return (AbstractListenerDelegate)compiler.methodWeave(ctclass, AbstractListenerDelegate.class, actionScript.toString()).newInstance(ctclass, new Class[]{Object.class, Integer.TYPE}, new Object[]{listenerBean, priority});
   }

   private void valid(Flow.Key key, Object listenerBean, Method method, Class<?>[] parameterTypes, Class<?> returnType) {
      if (parameterTypes.length == 2 && (parameterTypes[0] == Execution.class || parameterTypes[1] == String.class)) {
         if (returnType != Void.TYPE && returnType != Void.class) {
            throw new FlowException(String.format("Flow=%s,version=%s,listener=%s, method=%s,returnType=%s,\u8fd9\u56de\u53c2\u6559\u5b9a\u4e49\u51fa\u9519,\u4f8b\u52a0: void func(Execution execution,String eventName)", key.getFlowName(), key.getVersion(), listenerBean.getClass().getName(), method.getName(), returnType.getName()));
         }
      } else {
         throw new FlowException(String.format("Floe%s,version=%s,listener=%s, methode=%s,parameterlwes=%s, void funExecution execution", key.getFlowName(), key.getVersion(), listenerBean.getClass().getName(), method.getName(), Arrays.toString(parameterTypes)));
      }
   }
}
