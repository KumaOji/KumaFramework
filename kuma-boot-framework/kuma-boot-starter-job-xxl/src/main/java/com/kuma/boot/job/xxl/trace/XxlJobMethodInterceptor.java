package com.kuma.boot.job.xxl.trace;

import com.xxl.job.core.handler.IJobHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.ReflectionUtils;

public class XxlJobMethodInterceptor<T extends IJobHandler> implements MethodInterceptor {
   private final T delegate;
   private final BeanFactory beanFactory;

   public XxlJobMethodInterceptor(T delegate, BeanFactory beanFactory) {
      this.delegate = delegate;
      this.beanFactory = beanFactory;
   }

   public Object invoke(MethodInvocation invocation) throws Throwable {
      IJobHandler jobHandler = new XxlJobTraceWrapper(this.beanFactory, this.delegate);
      Method methodOnTracedBean = this.getMethod(invocation, jobHandler);
      if (methodOnTracedBean != null) {
         try {
            return methodOnTracedBean.invoke(jobHandler, invocation.getArguments());
         } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            throw (Throwable)(cause != null ? cause : ex);
         }
      } else {
         return invocation.proceed();
      }
   }

   private Method getMethod(MethodInvocation invocation, Object object) {
      Method method = invocation.getMethod();
      return ReflectionUtils.findMethod(object.getClass(), method.getName(), method.getParameterTypes());
   }
}
