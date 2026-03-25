package com.kuma.boot.encrypt.encrypt2.aspect;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.encrypt2.advice.DecryptRequestAdvice;
import com.kuma.boot.encrypt.encrypt2.advice.EncryptResponseAdvice;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class SecurityAspect {
   private DecryptRequestAdvice decryptRequestAdvice;
   private EncryptResponseAdvice encryptResponseAdvice;

   public SecurityAspect(DecryptRequestAdvice decryptRequestAdvice, EncryptResponseAdvice encryptResponseAdvice) {
      this.decryptRequestAdvice = decryptRequestAdvice;
      this.encryptResponseAdvice = encryptResponseAdvice;
   }

   @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)|| @annotation(org.springframework.web.bind.annotation.GetMapping)|| @annotation(org.springframework.web.bind.annotation.PostMapping)|| @annotation(org.springframework.web.bind.annotation.PutMapping)|| @annotation(org.springframework.web.bind.annotation.DeleteMapping)")
   public void httpPointCut() {
   }

   @Around("httpPointCut()")
   public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
      String methodName = joinPoint.getSignature().getName();
      Object[] arguments = joinPoint.getArgs();

      for(int i = 0; i < arguments.length; ++i) {
         Object arg = arguments[i];
         String paramName = this.getParamName(joinPoint, i);
         Annotation[] annotation = this.getAnnotation(joinPoint, i);
         Object decrypt = this.decryptRequestAdvice.decrypt(arg, annotation);
         arguments[i] = decrypt;
         LogUtils.debug("Method[{} {}] parameter[{} {}] is decrypted. Final parameter: {}", new Object[]{joinPoint.getSignature().getDeclaringTypeName(), methodName, arg.getClass().getName(), paramName, decrypt});
      }

      Object object = joinPoint.proceed(arguments);
      Method method = this.getMethod(joinPoint.getTarget().getClass(), methodName, arguments);
      Annotation[] methodAnnotations = method.getDeclaredAnnotations();
      Object encrypt = this.encryptResponseAdvice.encrypt(object, methodAnnotations);
      LogUtils.debug("Method[{} {}] returned value[{}] is encrypted. Final value: {}", new Object[]{joinPoint.getSignature().getDeclaringTypeName(), methodName, object.getClass().getName(), encrypt});
      return encrypt;
   }

   private Method getMethod(Class clazz, String methodName, Object[] arguments) throws NoSuchMethodException {
      Method[] methods = clazz.getMethods();

      for(Method method : methods) {
         if (method.getName().equals(methodName) && method.getParameterCount() == arguments.length) {
            return method;
         }
      }

      return null;
   }

   private String getParamName(JoinPoint joinPoint, int index) {
      String[] paramNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();
      return paramNames[index];
   }

   private Annotation[] getAnnotation(JoinPoint joinPoint, int index) {
      MethodSignature signature = (MethodSignature)joinPoint.getSignature();
      return signature.getMethod().getParameterAnnotations()[index];
   }
}
