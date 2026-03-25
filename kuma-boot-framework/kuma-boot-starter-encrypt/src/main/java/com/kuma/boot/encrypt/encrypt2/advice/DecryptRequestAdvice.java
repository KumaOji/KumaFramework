package com.kuma.boot.encrypt.encrypt2.advice;

import java.lang.annotation.Annotation;
import java.util.List;

public class DecryptRequestAdvice extends AbstractSecurityAdvice {
   public DecryptRequestAdvice(int maxDeep, List classPackage) {
      this.DEFAULT_CLEAN_DEPTH = maxDeep;
      this.STANDARD_CLASS = classPackage;
   }

   public String handleSecurity(String value, Annotation[] annotations) {
      if (this.securityHandler != null) {
         Annotation acquire = this.securityHandler.acquire(annotations);
         if (acquire != null) {
            value = this.securityHandler.handleDecrypt(value, acquire);
         }
      }

      return value;
   }

   public Object decrypt(Object value, Annotation[] annotations) throws Exception {
      return this.handleObject(0, this.DEFAULT_CLEAN_DEPTH, value, annotations);
   }
}
