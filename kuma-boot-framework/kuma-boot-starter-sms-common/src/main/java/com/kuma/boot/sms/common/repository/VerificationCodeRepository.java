package com.kuma.boot.sms.common.repository;

import com.kuma.boot.sms.common.model.VerificationCode;
import org.jspecify.annotations.Nullable;

public interface VerificationCodeRepository {
   @Nullable VerificationCode findOne(String phone, @Nullable String identificationCode);

   void save(VerificationCode verificationCode);

   void delete(String phone, @Nullable String identificationCode);
}
