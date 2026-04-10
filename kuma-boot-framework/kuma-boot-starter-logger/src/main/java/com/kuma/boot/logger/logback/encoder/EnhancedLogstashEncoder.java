package com.kuma.boot.logger.logback.encoder;

import java.util.ArrayList;
import net.logstash.logback.composite.JsonProvider;
import net.logstash.logback.composite.JsonProviders;
import net.logstash.logback.encoder.LogstashEncoder;

public class EnhancedLogstashEncoder extends LogstashEncoder {
   public EnhancedLogstashEncoder() {
   }

   public void setExcludeProvider(String excludedProviderClassName) {
      JsonProviders<?> providers = this.getFormatter().getProviders();

      for(JsonProvider<?> provider : new ArrayList(providers.getProviders())) {
         if (provider.getClass().getName().equals(excludedProviderClassName)) {
            providers.removeProvider(provider);
         }
      }

   }
}
