package com.kuma.boot.translation.translationextension.aop;

import com.kuma.boot.translation.translationextension.annotaion.TranslationResult;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.stereotype.Component;

@Component
public class TranslationAnnotationAdvisor extends AbstractPointcutAdvisor {
   private final TranslationInterceptor translationInterceptor;
   private final Pointcut pointcut = AnnotationMatchingPointcut.forMethodAnnotation(TranslationResult.class);

   public TranslationAnnotationAdvisor(TranslationInterceptor translationInterceptor) {
      this.translationInterceptor = translationInterceptor;
   }

   public Pointcut getPointcut() {
      return this.pointcut;
   }

   public Advice getAdvice() {
      return this.translationInterceptor;
   }
}
