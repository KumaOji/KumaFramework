package com.kuma.boot.flowengine.delegate;

import com.kuma.boot.flowengine.Compiler;
import com.kuma.boot.flowengine.annotation.After;
import com.kuma.boot.flowengine.annotation.Before;
import com.kuma.boot.flowengine.annotation.Condition;
import com.kuma.boot.flowengine.annotation.End;
import com.kuma.boot.flowengine.annotation.Error;
import com.kuma.boot.flowengine.annotation.Executor;
import com.kuma.boot.flowengine.engine.Execution;
import com.kuma.boot.flowengine.exception.FlowException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import javassist.CtClass;

public enum InvokeCoder {
   executor(Executor.class) {
      protected void validateTypes(InvokeDelegateContext.Key key, Method method, Class<?>[] parameterTypes, Class<?> returnType) {
         if (parameterTypes.length == 1 && parameterTypes[0] == Execution.class) {
            if (returnType != Void.class && returnType != Void.TYPE && returnType != String.class) {
               throw new FlowException(String.format("\u6d41\u7a0b\u5b9a\u4e49Flow=%s,version=%s,nodeName=%s,Executor\u5904\u7406\u5668\u8fd0\u56de\u53c2\u6570\u5b9a\u4e49\u51fa\u9519,\u4f8b\u5982:Object\u3001void\u3001void\u3001String func(Execution execution),\u51fa\u9519\u65b9\u6cd5method=%s,parameterTypes=%s", key.getFlowName(), key.getVersion(), key.getNodeName(), method, Arrays.toString(parameterTypes)));
            }
         } else {
            throw new FlowException(String.format("\u6d41\u7a0b\u5b9a\u4e49flow=%s,version=%s,nodeName=%s,Executor\u5904\u7406\u5668\u5165\u53c2\u5b9a\u4e49\u51fa\u9519,\u4f8b\u5982:func(Execution execution),\u51fa\u9519\u65b9\u6cd5method=%s,parameterTypes=%s", key.getFlowName(), key.getVersion(), key.getNodeName(), method, Arrays.toString(parameterTypes)));
         }
      }
   },
   before(Before.class) {
      protected void validateTypes(InvokeDelegateContext.Key key, Method method, Class<?>[] parameterTypes, Class<?> returnType) {
         if (parameterTypes.length == 1 && parameterTypes[0] == Execution.class) {
            if (returnType != Void.class && returnType != Void.TYPE) {
               throw new FlowException(String.format("\u6d41\u7a0b\u5b9a\u4e49Flow=%s,version=%s,nodeName=%s, Before\u5904\u7406\u5668\u8fd4\u56de\u53c2\u6570\u5b9a\u4e49\u51fa\u9519,\u4f8b\u52a0:void\u3001void func(Execution execution),\u51fa\u9519\u65b9\u6cd5method=%s,parameterTypes=%s", key.getFlowName(), key.getVersion(), key.getNodeName(), method, Arrays.toString(parameterTypes)));
            }
         } else {
            throw new FlowException(String.format("\u6d41\u7a0b\u5b9a\u4e49Flow=%s,version=%s,nodeName=%s,Before\u5904\u7406\u5668\u4eba\u53c2\u5b9a\u4e49\u51fa\u9519,\u4f8b\u52a0:func(Execution execution),\u51fa\u9519\u65b9\u6cd5method=%s,parameterTypes=%s", key.getFlowName(), key.getVersion(), key.getNodeName(), method, Arrays.toString(parameterTypes)));
         }
      }
   },
   after(After.class) {
      protected void validateTypes(InvokeDelegateContext.Key key, Method method, Class<?>[] parameterTypes, Class<?> returnType) {
         if (parameterTypes.length == 1 && parameterTypes[0] == Execution.class) {
            if (returnType != Void.class && returnType != Void.TYPE) {
               throw new FlowException(String.format("\u6d41\u7a0b\u5b9a\u4e49Flow=%s,version=%s,nodeName=%s, After\u5904\u7406\u5668\u8fd4\u56de\u53c2\u6570\u5b9a\u4e49\u51fa\u9519,\u4f8b\u52a0:void\u3001void func(Execution execution),\u51fa\u9519\u65b9\u6cd5method=%s,parameterTypes=%s", key.getFlowName(), key.getVersion(), key.getNodeName(), method, Arrays.toString(parameterTypes)));
            }
         } else {
            throw new FlowException(String.format("\u6d41\u7a0b\u5b9a\u4e49Flow=%s,version=%s,nodeName=%s,After\u5904\u7406\u5668\u4eba\u53c2\u5b9a\u4e49\u51fa\u9519,\u4f8b\u52a0:func(Execution execution),\u51fa\u9519\u65b9\u6cd5method=%s,parameterTypes=%s", key.getFlowName(), key.getVersion(), key.getNodeName(), method, Arrays.toString(parameterTypes)));
         }
      }
   },
   end(End.class) {
      protected void validateTypes(InvokeDelegateContext.Key key, Method method, Class<?>[] parameterTypes, Class<?> returnType) {
         if (parameterTypes.length == 1 && parameterTypes[0] == Execution.class) {
            if (returnType != Void.class && returnType != Void.TYPE) {
               throw new FlowException(String.format("\u6d41\u7a0b\u5b9a\u4e49Flow=%s,version=%s,nodeName=%s, End\u5904\u7406\u5668\u8fd4\u56de\u53c2\u6570\u5b9a\u4e49\u51fa\u9519,\u4f8b\u52a0:void\u3001void func(Execution execution),\u51fa\u9519\u65b9\u6cd5method=%s,parameterTypes=%s", key.getFlowName(), key.getVersion(), key.getNodeName(), method, Arrays.toString(parameterTypes)));
            }
         } else {
            throw new FlowException(String.format("\u6d41\u7a0b\u5b9a\u4e49Flow=%s,version=%s,nodeName=%s,End\u5904\u7406\u5668\u4eba\u53c2\u5b9a\u4e49\u51fa\u9519,\u4f8b\u52a0:func(Execution execution),\u51fa\u9519\u65b9\u6cd5method=%s,parameterTypes=%s", key.getFlowName(), key.getVersion(), key.getNodeName(), method, Arrays.toString(parameterTypes)));
         }
      }
   },
   condition(Condition.class) {
      protected void validateTypes(InvokeDelegateContext.Key key, Method method, Class<?>[] parameterTypes, Class<?> returnType) {
         if (parameterTypes.length == 1 && parameterTypes[0] == Execution.class) {
            if (returnType != String.class) {
               throw new FlowException(String.format("\u6d41\u7a0b\u5b9a\u4e49Flow=%s,version=%s,nodeName=%s, Condition\u5904\u7406\u5668\u8fd4\u56de\u53c2\u6570\u5b9a\u4e49\u51fa\u9519,\u4f8b\u52a0:void\u3001void func(Execution execution),\u51fa\u9519\u65b9\u6cd5method=%s,parameterTypes=%s", key.getFlowName(), key.getVersion(), key.getNodeName(), method, Arrays.toString(parameterTypes)));
            }
         } else {
            throw new FlowException(String.format("\u6d41\u7a0b\u5b9a\u4e49Flow=%s,version=%s,nodeName=%s,Condition\u5904\u7406\u5668\u4eba\u53c2\u5b9a\u4e49\u51fa\u9519,\u4f8b\u52a0:func(Execution execution),\u51fa\u9519\u65b9\u6cd5method=%s,parameterTypes=%s", key.getFlowName(), key.getVersion(), key.getNodeName(), method, Arrays.toString(parameterTypes)));
         }
      }
   },
   error(Error.class) {
      protected void validateTypes(InvokeDelegateContext.Key key, Method method, Class<?>[] parameterTypes, Class<?> returnType) {
         if (parameterTypes.length == 1 && parameterTypes[0] == Execution.class) {
            if (returnType != String.class) {
               throw new FlowException(String.format("\u6d41\u7a0b\u5b9a\u4e49Flow=%s,version=%s,nodeName=%s, Error\u5904\u7406\u5668\u8fd4\u56de\u53c2\u6570\u5b9a\u4e49\u51fa\u9519,\u4f8b\u52a0:void\u3001void func(Execution execution),\u51fa\u9519\u65b9\u6cd5method=%s,parameterTypes=%s", key.getFlowName(), key.getVersion(), key.getNodeName(), method, Arrays.toString(parameterTypes)));
            }
         } else {
            throw new FlowException(String.format("\u6d41\u7a0b\u5b9a\u4e49Flow=%s,version=%s,nodeName=%s,Error\u5904\u7406\u5668\u4eba\u53c2\u5b9a\u4e49\u51fa\u9519,\u4f8b\u52a0:func(Execution execution),\u51fa\u9519\u65b9\u6cd5method=%s,parameterTypes=%s", key.getFlowName(), key.getVersion(), key.getNodeName(), method, Arrays.toString(parameterTypes)));
         }
      }
   };

   private Class<? extends Annotation> annotationType;

   public InvokeDelegate create(InvokeDelegateContext.Key key, Object targetInstance, Method method) {
      Class<?>[] parameterTypes = method.getParameterTypes();
      Class<?> returnTypes = method.getReturnType();
      this.validateTypes(key, method, parameterTypes, returnTypes);
      return this.createInvokeDelegate(method, targetInstance, parameterTypes);
   }

   InvokeDelegate createInvokeDelegate(Method method, Object targetInstance, Class<?>[] parameterTypes) {
      Compiler compiler = Compiler.getInstance();
      CtClass ctClass = compiler.newCtClass(AbstractInvokeDelegate.class);
      StringBuilder constructScript = new StringBuilder();
      constructScript.append("public ").append(ctClass.getSimpleName()).append("(Object target){\n\t").append("super(target);\n").append("}\n");
      StringBuilder executeScript = new StringBuilder();
      executeScript.append("public Object invoke(Object[] args){\n\t");
      Class<?> returnType = method.getReturnType();
      if (returnType != Void.class && returnType != Void.TYPE) {
         executeScript.append("return ((").append(targetInstance.getClass().getName()).append(")getTarget()).").append(method.getName()).append("((").append(parameterTypes[0].getName()).append(")").append("args[0]);\n").append("}\n");
      } else {
         executeScript.append("((").append(targetInstance.getClass().getName()).append(")getTarget()).").append(method.getName()).append("((").append(parameterTypes[0].getName()).append("}").append("args[0]);\n").append("return null;").append("}\n");
      }

      compiler.constructImplement(ctClass, constructScript.toString());
      Object[] parameters = new Object[]{targetInstance};
      return (InvokeDelegate)compiler.methodWeave(ctClass, AbstractInvokeDelegate.class, executeScript.toString()).newInstance(ctClass, new Class[]{Object.class}, parameters);
   }

   private InvokeCoder(Class<? extends Annotation> annotationType) {
      this.annotationType = annotationType;
   }

   public static InvokeCoder coder(Annotation annotation) {
      InvokeCoder coder = null;

      for(InvokeCoder dc : values()) {
         if (dc.annotationType == annotation.annotationType()) {
            coder = dc;
            break;
         }
      }

      return coder;
   }

   protected abstract void validateTypes(InvokeDelegateContext.Key key, Method method, Class<?>[] parameterTypes, Class<?> returnTypes);

   // $FF: synthetic method
   private static InvokeCoder[] $values() {
      return new InvokeCoder[]{executor, before, after, end, condition, error};
   }
}
