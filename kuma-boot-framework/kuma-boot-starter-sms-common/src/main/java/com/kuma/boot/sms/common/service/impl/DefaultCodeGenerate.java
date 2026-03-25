package com.kuma.boot.sms.common.service.impl;

import com.kuma.boot.sms.common.properties.VerificationCodeProperties;
import com.kuma.boot.sms.common.service.CodeGenerate;
import java.text.NumberFormat;
import java.util.concurrent.ThreadLocalRandom;

public class DefaultCodeGenerate implements CodeGenerate {
   private final VerificationCodeProperties config;

   public DefaultCodeGenerate(VerificationCodeProperties config) {
      this.config = config;
   }

   public String generate() {
      int codeLength = this.config.getCodeLength();
      NumberFormat format = NumberFormat.getInstance();
      format.setGroupingUsed(false);
      format.setMaximumIntegerDigits(codeLength);
      format.setMinimumIntegerDigits(codeLength);
      return format.format((long)ThreadLocalRandom.current().nextInt((int)Math.pow((double)10.0F, (double)codeLength)));
   }
}
