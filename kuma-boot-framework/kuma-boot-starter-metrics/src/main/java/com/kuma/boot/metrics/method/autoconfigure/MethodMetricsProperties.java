package com.taotao.boot.metrics.method.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "method.metrics"
)
public class MethodMetricsProperties {
   private boolean enabled = true;
   private String prefix = "method";
   private boolean histogram = true;
   private double[] percentiles = new double[]{(double)0.5F, 0.95, 0.99};

   public MethodMetricsProperties() {
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public boolean isHistogram() {
      return this.histogram;
   }

   public void setHistogram(boolean histogram) {
      this.histogram = histogram;
   }

   public double[] getPercentiles() {
      return this.percentiles;
   }

   public void setPercentiles(double[] percentiles) {
      this.percentiles = percentiles;
   }
}
