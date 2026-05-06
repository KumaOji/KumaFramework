package com.kuma.boot.sms.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.sms.verification-code.repository.memory"
)
public class VerificationCodeMemoryRepositoryProperties {
   public static final String PREFIX = "kuma.boot.sms.verification-code.repository.memory";
   public static final long DEFAULT_GC_FREQUENCY = 300L;
   private long gcFrequency = 300L;

   public long getGcFrequency() {
      return this.gcFrequency;
   }

   public void setGcFrequency(long gcFrequency) {
      this.gcFrequency = gcFrequency;
   }
}
