package com.kuma.boot.tracer.env;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.sleuth.web.servlet")
public class CustomSleuthWebProperties {
   private String ignoreHeaders;
   private String ignoreParameters;

   public CustomSleuthWebProperties() {
   }

   public String getIgnoreHeaders() {
      return this.ignoreHeaders;
   }

   public void setIgnoreHeaders(String ignoreHeaders) {
      this.ignoreHeaders = ignoreHeaders;
   }

   public String getIgnoreParameters() {
      return this.ignoreParameters;
   }

   public void setIgnoreParameters(String ignoreParameters) {
      this.ignoreParameters = ignoreParameters;
   }
}
