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
            throw new FlowException("targetClass\u4e3aObject\u7c7b\u578b,\u8fd8\u641e\u4e2a\u55b5!");
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
               throw new FlowException(String.format("\u6d41\u7a0b\u5b9a\u4e49flow=%s,,version=%s\u5904\u7406\u5668\u7c7b\u578b\u5206\u6790\u5931\u8d25,\u7c7b\u578b\u4e3atargetType=%s\u7684\u5904\u7406\u5668\u4e0d\u5b58\u5728\u76f8\u5e94\u7684spring bean\u5b9a\u4e49", flowKey.getFlowName(), flowKey.getVersion(), targetType));
            }

            if (targets.size() != 1) {
               throw new FlowException(String.format("\u6d41\u7a0b\u5b9a\u4e49Flow=%s,version=%s\u5904\u7406\u5668\u7c7b\u578b\u5206\u6790\u5931\u8d25,\u7c7b\u578b\u4e3atargetType=%s\u7684\u5904\u7406\u5668\u5b9a\u4e49\u5b58\u5728\u591a\u4e2a\u5b9e\u73b0\u7248\u672c.beans=%s", flowKey.getFlowName(), flowKey.getVersion(), targetType, targets));
            }

            Object targetInstance = ((Map.Entry)targetsIterator.next()).getValue();
            if (AopUtils.isAopProxy(targetInstance)) {
               throw new FlowException(String.format("\u6d41\u7a0b\u5b9a\u4e49Flow=%s,,version=%s\u5904\u7406\u5668\u7c7b\u578b\u5206\u6790\u5931\u8d25,\u7c7b\u578b\u4e3atargetType=%s\u7684\u5904\u7406\u5668\u662f\u4e00\u4e2a\u52a8\u6001\u4ee3\u7406\u5bf9\u8c61->(%s)", flowKey.getFlowName(), flowKey.getVersion(), targetType, targetInstance));
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

            throw new FlowException(String.format("Flow=%s,Version=%s\u5904\u7406\u5668\u7c7b\u578b\u5206\u6790\u5931\u8d25,targetClasses=%s", flowKey.getFlowName(), flowKey.getVersion(), target), e);
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
