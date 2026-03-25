package com.kuma.boot.encrypt.sign.bean;

import com.kuma.boot.encrypt.sign.enums.EncryptBodyMethod;
import com.kuma.boot.encrypt.sign.enums.RSAKeyType;
import com.kuma.boot.encrypt.sign.enums.SHAEncryptType;

public class EncryptAnnotationInfoBean implements ISecurityInfo {
   private EncryptBodyMethod encryptBodyMethod;
   private SHAEncryptType shaEncryptType;
   private String key;
   private RSAKeyType rsaKeyType;

   public EncryptAnnotationInfoBean() {
   }

   public EncryptAnnotationInfoBean(EncryptBodyMethod encryptBodyMethod, SHAEncryptType shaEncryptType, String key, RSAKeyType rsaKeyType) {
      this.encryptBodyMethod = encryptBodyMethod;
      this.shaEncryptType = shaEncryptType;
      this.key = key;
      this.rsaKeyType = rsaKeyType;
   }

   public EncryptBodyMethod getEncryptBodyMethod() {
      return this.encryptBodyMethod;
   }

   public void setEncryptBodyMethod(EncryptBodyMethod encryptBodyMethod) {
      this.encryptBodyMethod = encryptBodyMethod;
   }

   public SHAEncryptType getShaEncryptType() {
      return this.shaEncryptType;
   }

   public void setShaEncryptType(SHAEncryptType shaEncryptType) {
      this.shaEncryptType = shaEncryptType;
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

   public static EncryptAnnotationInfoBeanBuilder builder() {
      return new EncryptAnnotationInfoBeanBuilder();
   }

   public static final class EncryptAnnotationInfoBeanBuilder {
      private EncryptAnnotationInfoBean encryptAnnotationInfoBean = new EncryptAnnotationInfoBean();

      private EncryptAnnotationInfoBeanBuilder() {
      }

      public EncryptAnnotationInfoBeanBuilder encryptBodyMethod(EncryptBodyMethod encryptBodyMethod) {
         this.encryptAnnotationInfoBean.setEncryptBodyMethod(encryptBodyMethod);
         return this;
      }

      public EncryptAnnotationInfoBeanBuilder shaEncryptType(SHAEncryptType shaEncryptType) {
         this.encryptAnnotationInfoBean.setShaEncryptType(shaEncryptType);
         return this;
      }

      public EncryptAnnotationInfoBeanBuilder key(String key) {
         this.encryptAnnotationInfoBean.setKey(key);
         return this;
      }

      public EncryptAnnotationInfoBeanBuilder rsaKeyType(RSAKeyType rsaKeyType) {
         this.encryptAnnotationInfoBean.setRsaKeyType(rsaKeyType);
         return this;
      }

      public EncryptAnnotationInfoBean build() {
         return this.encryptAnnotationInfoBean;
      }
   }
}
