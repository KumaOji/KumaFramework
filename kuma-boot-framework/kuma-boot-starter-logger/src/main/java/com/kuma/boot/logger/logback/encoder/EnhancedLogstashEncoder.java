package com.kuma.boot.logger.logback.encoder;

import net.logstash.logback.composite.JsonProviders;
import net.logstash.logback.encoder.LogstashEncoder;

public class EnhancedLogstashEncoder extends LogstashEncoder {
   public EnhancedLogstashEncoder() {
   }

   public void setExcludeProvider(String excludedProviderClassName) {
      JsonProviders<?> providers = this.getFormatter().getProviders();
      providers.getProviders().removeIf(provider -> provider.getClass().getName().equals(excludedProviderClassName));
   }
}
