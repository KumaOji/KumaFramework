package com.kuma.boot.sensitive.sensitivelog.core.support.config;

import com.kuma.boot.common.support.deepcopy.DeepCopy;
import com.kuma.boot.sensitive.sensitivelog.api.SensitiveConfig;

public class DefaultSensitiveConfig implements SensitiveConfig {
   private DeepCopy deepCopy;

   public DefaultSensitiveConfig() {
   }

   public static DefaultSensitiveConfig newInstance() {
      return new DefaultSensitiveConfig();
   }

   public DefaultSensitiveConfig deepCopy(DeepCopy deepCopy) {
      this.deepCopy = deepCopy;
      return this;
   }

   public DeepCopy deepCopy() {
      return this.deepCopy;
   }
}
