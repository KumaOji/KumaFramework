package com.kuma.boot.sms.common.properties;

import com.kuma.boot.sms.common.enums.RepositoryType;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.sms.verification-code"
)
public class VerificationCodeProperties {
   public static final String PREFIX = "kuma.boot.sms.verification-code";
   public static final String DEFAULT_TYPE = "VerificationCode";
   private String type = "VerificationCode";
   private RepositoryType repository;
   private Long expirationTime;
   private Long retryIntervalTime;
   private int codeLength;
   private boolean useIdentificationCode;
   private int identificationCodeLength;
   private boolean deleteByVerifySucceed;
   private boolean deleteByVerifyFail;
   private boolean templateHasExpirationTime;

   public VerificationCodeProperties() {
      this.repository = RepositoryType.REDIS;
      this.codeLength = 6;
      this.useIdentificationCode = false;
      this.identificationCodeLength = 3;
      this.deleteByVerifySucceed = true;
      this.deleteByVerifyFail = false;
      this.templateHasExpirationTime = false;
   }

   public String getType() {
      return this.type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public Long getExpirationTime() {
      return this.expirationTime;
   }

   public void setExpirationTime(Long expirationTime) {
      this.expirationTime = expirationTime;
   }

   public Long getRetryIntervalTime() {
      return this.retryIntervalTime;
   }

   public void setRetryIntervalTime(Long retryIntervalTime) {
      this.retryIntervalTime = retryIntervalTime;
   }

   public int getCodeLength() {
      return this.codeLength;
   }

   public void setCodeLength(int codeLength) {
      this.codeLength = codeLength;
   }

   public boolean isUseIdentificationCode() {
      return this.useIdentificationCode;
   }

   public void setUseIdentificationCode(boolean useIdentificationCode) {
      this.useIdentificationCode = useIdentificationCode;
   }

   public int getIdentificationCodeLength() {
      return this.identificationCodeLength;
   }

   public void setIdentificationCodeLength(int identificationCodeLength) {
      this.identificationCodeLength = identificationCodeLength;
   }

   public boolean isDeleteByVerifySucceed() {
      return this.deleteByVerifySucceed;
   }

   public void setDeleteByVerifySucceed(boolean deleteByVerifySucceed) {
      this.deleteByVerifySucceed = deleteByVerifySucceed;
   }

   public boolean isDeleteByVerifyFail() {
      return this.deleteByVerifyFail;
   }

   public void setDeleteByVerifyFail(boolean deleteByVerifyFail) {
      this.deleteByVerifyFail = deleteByVerifyFail;
   }

   public boolean isTemplateHasExpirationTime() {
      return this.templateHasExpirationTime;
   }

   public void setTemplateHasExpirationTime(boolean templateHasExpirationTime) {
      this.templateHasExpirationTime = templateHasExpirationTime;
   }

   public RepositoryType getRepository() {
      return this.repository;
   }

   public void setRepository(RepositoryType repository) {
      this.repository = repository;
   }
}
