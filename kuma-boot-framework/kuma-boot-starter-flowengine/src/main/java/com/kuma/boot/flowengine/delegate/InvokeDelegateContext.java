package com.kuma.boot.flowengine.delegate;

import com.google.common.collect.Maps;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.flowengine.engine.Execution;
import com.kuma.boot.flowengine.exception.FlowException;
import com.kuma.boot.flowengine.module.Flow;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;

public class InvokeDelegateContext extends AbstractInvokeDelegate implements InvokeSupport {
   private final ApplicationContext applicationContext;
   private Map<Flow.Key, Map<Key, InvokeDelegate>> invokeDelegateArrayHolder = Maps.newConcurrentMap();

   public InvokeDelegateContext(ApplicationContext applicationContext) {
      super((Object)null);
      this.applicationContext = applicationContext;
   }

   public Object invoke(Object[] args) {
      Object result = null;
      Flow.Key key = (Flow.Key)args[0];
      String nodeName = (String)args[1];
      Class<Annotation> annotationType = (Class)args[2];
      Execution execution = (Execution)args[3];
      Map<Key, InvokeDelegate> invokeDelegateHolder = (Map)this.invokeDelegateArrayHolder.get(key);
      Key mKey = new Key(key.getFlowName(), key.getVersion(), nodeName, annotationType);
      InvokeDelegate invokeDelegate = (InvokeDelegate)invokeDelegateHolder.get(mKey);
      if (invokeDelegate != null) {
         result = invokeDelegate.invoke(new Object[]{execution});
      }

      return result;
   }

   public void proceed(Flow.Key flowKey, String nodeName, String target) {
      if (StringUtils.isNotBlank(target)) {
         if (Object.class.getName().equals(target)) {
            throw new FlowException("targetClass为Object类型,还搞个喵!");
         }

         Map<Key, InvokeDelegate> invokeHolder = (Map)this.invokeDelegateArrayHolder.get(flowKey);
         if (invokeHolder == null) {
            invokeHolder = Maps.newConcurrentMap();
            this.invokeDelegateArrayHolder.put(flowKey, invokeHolder);
         }

         try {
            Class targetType = Class.forName(target);
            Map<String, Object> targets = this.applicationContext.getBeansOfType(targetType);
            Iterator<Map.Entry<String, Object>> targetsIterator = targets.entrySet().iterator();
            if (!targetsIterator.hasNext()) {
               throw new FlowException(String.format("流程定义flow=%s,,version=%s处理器类型分析失败,类型为targetType=%s的处理器不存在相应的spring bean定义", flowKey.getFlowName(), flowKey.getVersion(), targetType));
            }

            if (targets.size() != 1) {
               throw new FlowException(String.format("流程定义Flow=%s,version=%s处理器类型分析失败,类型为targetType=%s的处理器定义存在多个实现版本.beans=%s", flowKey.getFlowName(), flowKey.getVersion(), targetType, targets));
            }

            Object targetInstance = ((Map.Entry)targetsIterator.next()).getValue();
            if (AopUtils.isAopProxy(targetInstance)) {
               throw new FlowException(String.format("流程定义Flow=%s,,version=%s处理器类型分析失败,类型为targetType=%s的处理器是一个动态代理对象->(%s)", flowKey.getFlowName(), flowKey.getVersion(), targetType, targetInstance));
            }

            Class<?> supperType = targetType;

            do {
               for(Method method : supperType.getDeclaredMethods()) {
                  Annotation[] annotations = method.getDeclaredAnnotations();

                  for(Annotation annotation : annotations) {
                     InvokeCoder coder = InvokeCoder.coder(annotation);
                     if (coder != null) {
                        Key key = new Key(flowKey.getFlowName(), flowKey.getVersion(), nodeName, annotation.annotationType());
                        if (!invokeHolder.containsKey(key)) {
                           InvokeDelegate invokeDelegate = coder.create(key, targetInstance, method);
                           invokeHolder.put(key, invokeDelegate);
                        }
                     }
                  }
               }
            } while((supperType = supperType.getSuperclass()) != Object.class);
         } catch (Exception e) {
            if (e instanceof FlowException) {
               throw (FlowException)e;
            }

            throw new FlowException(String.format("Flow=%s,Version=%s处理器类型分析失败,targetClasses=%s", flowKey.getFlowName(), flowKey.getVersion(), target), e);
         }
      }

   }

   public static class Key {
      private String flowName;
      private int version;
      private String nodeName;
      private Class<? extends Annotation> annotationType;

      public Key(String flowName, int version, String nodeName, Class<? extends Annotation> annotationType) {
         this.flowName = flowName;
         this.version = version;
         this.nodeName = nodeName;
         this.annotationType = annotationType;
      }

      public String getFlowName() {
         return this.flowName;
      }

      public int getVersion() {
         return this.version;
      }

      public String getNodeName() {
         return this.nodeName;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Key key = (Key)o;
            return this.version == key.version && Objects.equals(this.flowName, key.flowName) && Objects.equals(this.nodeName, key.nodeName) && Objects.equals(this.annotationType, key.annotationType);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.flowName, this.version, this.nodeName, this.annotationType});
      }
   }
}
