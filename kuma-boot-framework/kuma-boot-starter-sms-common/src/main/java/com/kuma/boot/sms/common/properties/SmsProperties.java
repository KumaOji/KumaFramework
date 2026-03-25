package com.kuma.boot.sms.common.properties;

import com.kuma.boot.sms.common.enums.SmsType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.sms"
)
public class SmsProperties {
   public static final String PREFIX = "kuma.boot.sms";
   private boolean enabled = false;
   private String reg;
   private SmsType type;
   private String loadBalancerType = "Random";

   public String getReg() {
      return this.reg;
   }

   public void setReg(String reg) {
      this.reg = reg;
   }

   public String getLoadBalancerType() {
      return this.loadBalancerType;
   }

   public void setLoadBalancerType(String loadBalancerType) {
      this.loadBalancerType = loadBalancerType;
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

   public SmsType getType() {
      return this.type;
   }

   public void setType(SmsType type) {
      this.type = type;
   }
}
