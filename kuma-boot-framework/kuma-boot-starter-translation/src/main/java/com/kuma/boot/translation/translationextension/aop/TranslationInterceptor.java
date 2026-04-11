package com.kuma.boot.translation.translationextension.aop;

import cn.hutool.core.util.TypeUtil;
import com.kuma.boot.translation.translationextension.annotaion.TranslationResult;
import com.kuma.boot.translation.translationextension.handler.TranslationHandler;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.stereotype.Component;

@Component
public class TranslationInterceptor implements MethodInterceptor {
   private final List<TranslationHandler> translationHandlers;

   public TranslationInterceptor(List<TranslationHandler> translationHandlers) {
      this.translationHandlers = translationHandlers;
   }

   public Object invoke(MethodInvocation invocation) throws Throwable {
      if (Objects.isNull(invocation)) {
         return null;
      } else {
         Class<?> cls = AopProxyUtils.ultimateTargetClass(invocation.getThis());
         if (!cls.equals(invocation.getThis().getClass())) {
            return invocation.proceed();
         } else {
            TranslationResult translationResult = (TranslationResult)invocation.getMethod().getAnnotation(TranslationResult.class);
            Object proceed = invocation.proceed();
            if (!Objects.isNull(proceed) && translationResult.enable()) {
               Type returnType = TypeUtil.getReturnType(invocation.getMethod());

               for(TranslationHandler translationHandler : this.translationHandlers) {
                  if (translationHandler.adaptation(returnType)) {
                     translationHandler.translation(proceed, returnType, translationResult);
                  }
               }

               return proceed;
            } else {
               return null;
            }
         }
      }
   }
}
