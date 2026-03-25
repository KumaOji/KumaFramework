package com.kuma.boot.encrypt.crypto.ext;

import com.google.common.base.MoreObjects;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.crypto.ext.crpto"
)
public class CryptoProperties {
   private CryptoStrategy cryptoStrategy;

   public CryptoProperties() {
      this.cryptoStrategy = CryptoStrategy.SM;
   }

   public CryptoStrategy getCryptoStrategy() {
      return this.cryptoStrategy;
   }

   public void setCryptoStrategy(CryptoStrategy cryptoStrategy) {
      this.cryptoStrategy = cryptoStrategy;
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("strategy", this.cryptoStrategy).toString();
   }
}
