package com.kuma.boot.idgenerator.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.idgenerator"
)
public class IdGeneratorProperties {
   public static final String PREFIX = "kuma.boot.idgenerator";
   private boolean enabled = true;
   private IdGeneratorEnum type;

   public IdGeneratorProperties() {
      this.type = IdGeneratorProperties.IdGeneratorEnum.REDIS;
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public IdGeneratorEnum getType() {
      return this.type;
   }

   public void setType(IdGeneratorEnum type) {
      this.type = type;
   }

   public static enum IdGeneratorEnum {
      REDIS,
      REDIS_LOCK,
      ZOOKEEPER;

      private IdGeneratorEnum() {
      }

      // $FF: synthetic method
      private static IdGeneratorEnum[] $values() {
         return new IdGeneratorEnum[]{REDIS, REDIS_LOCK, ZOOKEEPER};
      }
   }
}
