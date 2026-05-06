package com.kuma.boot.job.xxl.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(
   prefix = "kuma.boot.job.xxl"
)
public class XxlJobProperties {
   public static final String PREFIX = "kuma.boot.job.xxl";
   private boolean enabled = false;
   private boolean trace = true;
   private boolean autoRegister = false;
   @NestedConfigurationProperty
   private XxlAdminProperties admin = new XxlAdminProperties();
   @NestedConfigurationProperty
   private XxlExecutorProperties executor = new XxlExecutorProperties();

   public XxlJobProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public XxlAdminProperties getAdmin() {
      return this.admin;
   }

   public void setAdmin(XxlAdminProperties admin) {
      this.admin = admin;
   }

   public XxlExecutorProperties getExecutor() {
      return this.executor;
   }

   public void setExecutor(XxlExecutorProperties executor) {
      this.executor = executor;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public boolean isTrace() {
      return this.trace;
   }

   public void setTrace(boolean trace) {
      this.trace = trace;
   }

   public boolean isAutoRegister() {
      return this.autoRegister;
   }

   public void setAutoRegister(boolean autoRegister) {
      this.autoRegister = autoRegister;
   }
}
