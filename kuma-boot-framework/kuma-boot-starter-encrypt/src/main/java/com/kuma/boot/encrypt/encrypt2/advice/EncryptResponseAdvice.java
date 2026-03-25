package com.kuma.boot.encrypt.encrypt2.advice;

import java.lang.annotation.Annotation;
import java.util.List;

public class EncryptResponseAdvice extends AbstractSecurityAdvice {
   public EncryptResponseAdvice(int maxDeep, List classPackage) {
      this.DEFAULT_CLEAN_DEPTH = maxDeep;
      this.STANDARD_CLASS = classPackage;
   }

   public Object encrypt(Object value, Annotation[] annotations) throws Exception {
      return this.handleObject(0, this.DEFAULT_CLEAN_DEPTH, value, annotations);
   }

   public String handleSecurity(String value, Annotation[] annotations) {
      if (this.sensitiveHandler != null) {
         Annotation acquire = this.sensitiveHandler.acquire(annotations);
         if (acquire != null) {
            value = this.sensitiveHandler.format(value, acquire);
         }
      }

      if (this.securityHandler != null) {
         Annotation acquire = this.securityHandler.acquire(annotations);
         if (acquire != null) {
            value = this.securityHandler.handleEncrypt(value, acquire);
         }
      }

      return value;
   }
}
