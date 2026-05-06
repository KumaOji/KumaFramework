package com.kuma.boot.data.jpa.autoconfigure.properties;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.data.jpa.tenant"
)
public class TenantProperties {
   public static final String PREFIX = "kuma.boot.data.jpa.tenant";
   private Boolean enabled = true;
   private List<String> ignoreTables = new ArrayList();
   private List<String> ignoreSqls = new ArrayList();

   public TenantProperties() {
   }

   public Boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }

   public List<String> getIgnoreTables() {
      return this.ignoreTables;
   }

   public void setIgnoreTables(List<String> ignoreTables) {
      this.ignoreTables = ignoreTables;
   }

   public List<String> getIgnoreSqls() {
      return this.ignoreSqls;
   }

   public void setIgnoreSqls(List<String> ignoreSqls) {
      this.ignoreSqls = ignoreSqls;
   }
}
