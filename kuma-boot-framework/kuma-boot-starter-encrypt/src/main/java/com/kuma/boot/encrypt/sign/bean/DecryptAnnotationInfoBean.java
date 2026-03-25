package com.kuma.boot.encrypt.sign.bean;

import com.kuma.boot.encrypt.sign.enums.DecryptBodyMethod;
import com.kuma.boot.encrypt.sign.enums.RSAKeyType;

public class DecryptAnnotationInfoBean implements ISecurityInfo {
   private DecryptBodyMethod decryptBodyMethod;
   private String key;
   private RSAKeyType rsaKeyType;

   public DecryptBodyMethod getDecryptBodyMethod() {
      return this.decryptBodyMethod;
   }

   public void setDecryptBodyMethod(DecryptBodyMethod decryptBodyMethod) {
      this.decryptBodyMethod = decryptBodyMethod;
   }

   public String getKey() {
      return this.key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public RSAKeyType getRsaKeyType() {
      return this.rsaKeyType;
   }

   public void setRsaKeyType(RSAKeyType rsaKeyType) {
      this.rsaKeyType = rsaKeyType;
   }

   public static DecryptAnnotationInfoBeanBuilder builder() {
      return new DecryptAnnotationInfoBeanBuilder();
   }

   public static final class DecryptAnnotationInfoBeanBuilder {
      private DecryptAnnotationInfoBean decryptAnnotationInfoBean = new DecryptAnnotationInfoBean();

      private DecryptAnnotationInfoBeanBuilder() {
      }

      public DecryptAnnotationInfoBeanBuilder decryptBodyMethod(DecryptBodyMethod decryptBodyMethod) {
         this.decryptAnnotationInfoBean.setDecryptBodyMethod(decryptBodyMethod);
         return this;
      }

      public DecryptAnnotationInfoBeanBuilder key(String key) {
         this.decryptAnnotationInfoBean.setKey(key);
         return this;
      }

      public DecryptAnnotationInfoBeanBuilder rsaKeyType(RSAKeyType rsaKeyType) {
         this.decryptAnnotationInfoBean.setRsaKeyType(rsaKeyType);
         return this;
      }

      public DecryptAnnotationInfoBean build() {
         return this.decryptAnnotationInfoBean;
      }
   }
}
