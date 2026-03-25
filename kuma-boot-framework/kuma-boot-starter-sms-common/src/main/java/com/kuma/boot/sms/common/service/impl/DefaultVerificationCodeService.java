package com.kuma.boot.sms.common.service.impl;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.sms.common.exception.PhoneIsNullException;
import com.kuma.boot.sms.common.exception.RetryTimeShortException;
import com.kuma.boot.sms.common.exception.TypeIsNullException;
import com.kuma.boot.sms.common.model.NoticeData;
import com.kuma.boot.sms.common.model.VerificationCode;
import com.kuma.boot.sms.common.model.VerificationCodeTypeGenerate;
import com.kuma.boot.sms.common.properties.VerificationCodeProperties;
import com.kuma.boot.sms.common.repository.VerificationCodeRepository;
import com.kuma.boot.sms.common.service.CodeGenerate;
import com.kuma.boot.sms.common.service.NoticeService;
import com.kuma.boot.sms.common.service.VerificationCodeService;
import com.kuma.boot.sms.common.utils.RandomUtils;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class DefaultVerificationCodeService implements VerificationCodeService {
   private final VerificationCodeRepository repository;
   private final VerificationCodeProperties config;
   private final NoticeService noticeService;
   private final CodeGenerate codeGenerate;
   private final VerificationCodeTypeGenerate verificationCodeTypeGenerate;

   public DefaultVerificationCodeService(VerificationCodeRepository repository, VerificationCodeProperties config, NoticeService noticeService, CodeGenerate codeGenerate, @Nullable VerificationCodeTypeGenerate verificationCodeTypeGenerate) {
      this.repository = repository;
      this.config = config;
      this.noticeService = noticeService;
      this.codeGenerate = codeGenerate;
      this.verificationCodeTypeGenerate = verificationCodeTypeGenerate;
   }

   public String find(String phone, String identificationCode) {
      if (StringUtils.isBlank(phone)) {
         return null;
      } else {
         this.phoneValidation(phone);
         VerificationCode verificationCode = this.repository.findOne(phone, identificationCode);
         return verificationCode == null ? null : verificationCode.getCode();
      }
   }

   private @Nullable String createIdentificationCode() {
      return !this.config.isUseIdentificationCode() ? null : RandomUtils.nextString(this.config.getIdentificationCodeLength());
   }

   public void send(String tempPhone, @Nullable String type) {
      String phone = StringUtils.trimToNull(tempPhone);
      if (phone == null) {
         throw new PhoneIsNullException();
      } else {
         this.phoneValidation(phone);
         String identificationCode = this.createIdentificationCode();
         VerificationCodeCheckResult verificationCodeCheckResult = this.verificationCodeCheck(phone, identificationCode);
         VerificationCode verificationCode = verificationCodeCheckResult.verificationCode;
         Map<String, String> params = this.buildSendParams(verificationCode);
         if (type == null && this.verificationCodeTypeGenerate != null) {
            type = this.verificationCodeTypeGenerate.getType(phone, params);
         }

         if (type == null) {
            type = this.config.getType();
         }

         if (type == null) {
            throw new TypeIsNullException();
         } else {
            NoticeData notice = new NoticeData();
            notice.setType(type);
            notice.setParams(params);
            if (this.noticeService.send(notice, phone) && verificationCodeCheckResult.newVerificationCode) {
               this.repository.save(verificationCode);
            }

         }
      }
   }

   private VerificationCodeCheckResult verificationCodeCheck(String phone, @Nullable String identificationCode) {
      VerificationCode verificationCode = this.repository.findOne(phone, identificationCode);
      Long expirationTime = this.config.getExpirationTime();
      boolean newVerificationCode = false;
      if (verificationCode == null) {
         verificationCode = new VerificationCode();
         verificationCode.setPhone(phone);
         verificationCode.setIdentificationCode(identificationCode);
         Long retryIntervalTime = this.config.getRetryIntervalTime();
         if (expirationTime != null && expirationTime > 0L) {
            verificationCode.setExpirationTime(LocalDateTime.now().plusSeconds(expirationTime));
         }

         if (retryIntervalTime != null && retryIntervalTime > 0L) {
            verificationCode.setRetryTime(LocalDateTime.now().plusSeconds(retryIntervalTime));
         }

         verificationCode.setCode(this.codeGenerate.generate());
         newVerificationCode = true;
      } else {
         LocalDateTime retryTime = verificationCode.getRetryTime();
         if (retryTime != null) {
            long surplus = Duration.between(LocalDateTime.now(), retryTime).getSeconds();
            if (surplus > 0L) {
               throw new RetryTimeShortException(surplus);
            }
         }
      }

      VerificationCodeCheckResult result = new VerificationCodeCheckResult();
      result.verificationCode = verificationCode;
      result.newVerificationCode = newVerificationCode;
      return result;
   }

   private Map buildSendParams(VerificationCode verificationCode) {
      Long expirationTime = this.config.getExpirationTime();
      Map<String, String> params = new HashMap(4);
      params.put("code", verificationCode.getCode());
      if (verificationCode.getIdentificationCode() != null) {
         params.put("identificationCode", verificationCode.getIdentificationCode());
      }

      if (this.config.isTemplateHasExpirationTime() && expirationTime != null && expirationTime > 0L) {
         params.put("expirationTimeOfSeconds", String.valueOf(expirationTime));
         params.put("expirationTimeOfMinutes", String.valueOf(expirationTime / 60L));
      }

      return params;
   }

   public boolean verify(String phone, String code, @Nullable String identificationCode) {
      if (StringUtils.isAnyBlank(new CharSequence[]{phone, code})) {
         return false;
      } else {
         this.phoneValidation(phone);
         VerificationCode verificationCode = this.repository.findOne(phone, identificationCode);
         if (verificationCode == null) {
            return false;
         } else {
            boolean verifyData = Objects.equals(verificationCode.getCode(), code);
            if (verifyData && this.config.isDeleteByVerifySucceed()) {
               this.repository.delete(phone, identificationCode);
            }

            if (!verifyData && this.config.isDeleteByVerifyFail()) {
               this.repository.delete(phone, identificationCode);
            }

            return verifyData;
         }
      }
   }

   private void phoneValidation(String phone) {
      if (!this.noticeService.phoneRegValidation(phone)) {
         throw new PhoneIsNullException();
      }
   }

   private static class VerificationCodeCheckResult {
      VerificationCode verificationCode;
      boolean newVerificationCode;
   }
}
