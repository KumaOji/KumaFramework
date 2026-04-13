package com.kuma.boot.data.jpa.tenant.properties;

import com.kuma.boot.data.jpa.tenant.MultiTenantApproach;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "herodotus.multi-tenant"
)
public class MultiTenantProperties {
   private String[] packageToScan = new String[]{"cn.herodotus.engine", "cn.herodotus.rocket"};
   private MultiTenantApproach approach;

   public MultiTenantProperties() {
      this.approach = MultiTenantApproach.DISCRIMINATOR;
   }

   public String[] getPackageToScan() {
      return this.packageToScan;
   }

   public void setPackageToScan(String[] packageToScan) {
      this.packageToScan = packageToScan;
   }

   public MultiTenantApproach getApproach() {
      return this.approach;
   }

   public void setApproach(MultiTenantApproach approach) {
      this.approach = approach;
   }
}
