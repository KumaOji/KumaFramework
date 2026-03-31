package com.kuma.boot.monitor.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.monitor.export"
)
public class ExportProperties {
   public static final String PREFIX = "kuma.boot.monitor.export";
   private boolean enabled = true;
   private int exportTimeSpan = 30;
   private String[] elkDestinations;
   private boolean elkEnabled = false;
   private boolean catEnabled = false;
   private String catServerUrl;

   public ExportProperties() {
   }

   public int getExportTimeSpan() {
      return this.exportTimeSpan;
   }

   public void setExportTimeSpan(int exportTimeSpan) {
      this.exportTimeSpan = exportTimeSpan;
   }

   public String[] getElkDestinations() {
      return this.elkDestinations;
   }

   public void setElkDestinations(String[] elkDestinations) {
      this.elkDestinations = elkDestinations;
   }

   public boolean getElkEnabled() {
      return this.elkEnabled;
   }

   public void setElkEnabled(boolean elkEnabled) {
      this.elkEnabled = elkEnabled;
   }

   public boolean isCatEnabled() {
      return this.catEnabled;
   }

   public void setCatEnabled(boolean catEnabled) {
      this.catEnabled = catEnabled;
   }

   public String getCatServerUrl() {
      return this.catServerUrl;
   }

   public void setCatServerUrl(String catServerUrl) {
      this.catServerUrl = catServerUrl;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
