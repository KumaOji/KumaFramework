package com.kuma.boot.encrypt.crypto.encrypt;

import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.Field;
import java.util.Objects;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class EncryptAspect {
   @Pointcut("@annotation(com.kuma.boot.encrypt.crypto.encrypt.EncryptMethod)")
   public void pointCut() {
   }

   @Around("pointCut()")
   public Object around(ProceedingJoinPoint joinPoint) {
      this.encrypt(joinPoint);
      return this.decrypt(joinPoint);
   }

   public void encrypt(ProceedingJoinPoint joinPoint) {
      try {
         Object[] objects = joinPoint.getArgs();

         for(Object o : objects) {
            if (o instanceof String) {
               this.encryptValue(o);
            } else {
               this.handler(o, "encrypt");
            }
         }
      } catch (IllegalAccessException e) {
         LogUtils.error(e);
      }

   }

   public Object decrypt(ProceedingJoinPoint joinPoint) {
      Object result = null;

      try {
         Object obj = joinPoint.proceed();
         if (obj != null) {
            if (obj instanceof String) {
               result = this.decryptValue(obj);
            } else {
               result = this.handler(obj, "decrypt");
            }
         }
      } catch (Throwable e) {
         LogUtils.error(e);
      }

      return result;
   }

   private Object handler(Object obj, String type) throws IllegalAccessException {
      if (Objects.isNull(obj)) {
         return null;
      } else {
         Field[] fields = obj.getClass().getDeclaredFields();

         for(Field field : fields) {
            boolean hasSecureField = field.isAnnotationPresent(EncryptField.class);
            if (hasSecureField) {
               field.setAccessible(true);
               String realValue = (String)field.get(obj);
               String value = "";
               field.set(obj, value);
            }
         }

         return obj;
      }
   }

   public String encryptValue(Object realValue) {
      String value = null;
      return value;
   }

   public String decryptValue(Object realValue) {
      String value = String.valueOf(realValue);
      return value;
   }
}
