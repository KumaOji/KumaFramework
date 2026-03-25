package com.kuma.boot.sms.common.service;

import org.jspecify.annotations.Nullable;

public interface VerificationCodeService {
   String MSG_KEY_CODE = "code";
   String MSG_KEY_IDENTIFICATION_CODE = "identificationCode";
   String MSG_KEY_EXPIRATION_TIME_OF_SECONDS = "expirationTimeOfSeconds";
   String MSG_KEY_EXPIRATION_TIME_OF_MINUTES = "expirationTimeOfMinutes";

   @Nullable String find(String phone, String identificationCode);

   default void send(String phone) {
      this.send(phone, (String)null);
   }

   void send(String phone, @Nullable String type);

   boolean verify(String phone, String code, @Nullable String identificationCode);
}
