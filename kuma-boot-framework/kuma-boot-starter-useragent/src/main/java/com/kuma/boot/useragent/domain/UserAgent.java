package com.kuma.boot.useragent.domain;

public record UserAgent(String userAgentStr, String browser, String browserType, String browserVersion, String os, String osVersion, String deviceType, String deviceName, String deviceBrand) {
   public static UserAgentBuilder builder(String userAgentStr) {
      return new UserAgentBuilder(userAgentStr);
   }

   public static class UserAgentBuilder {
      private final String userAgentStr;
      private String browser;
      private String browserType;
      private String browserVersion;
      private String os;
      private String osVersion;
      private String deviceType;
      private String deviceName;
      private String deviceBrand;

      UserAgentBuilder(String userAgentStr) {
         this.userAgentStr = userAgentStr;
      }

      public UserAgentBuilder browser(String browser) {
         this.browser = browser;
         return this;
      }

      public UserAgentBuilder browserType(String browserType) {
         this.browserType = browserType;
         return this;
      }

      public UserAgentBuilder browserVersion(String browserVersion) {
         this.browserVersion = browserVersion;
         return this;
      }

      public UserAgentBuilder os(String os) {
         this.os = os;
         return this;
      }

      public UserAgentBuilder osVersion(String osVersion) {
         this.osVersion = osVersion;
         return this;
      }

      public UserAgentBuilder deviceType(String deviceType) {
         this.deviceType = deviceType;
         return this;
      }

      public UserAgentBuilder deviceName(String deviceName) {
         this.deviceName = deviceName;
         return this;
      }

      public UserAgentBuilder deviceBrand(String deviceName) {
         this.deviceBrand = this.deviceBrand;
         return this;
      }

      public UserAgent build() {
         return new UserAgent(this.userAgentStr, this.browser, this.browserType, this.browserVersion, this.os, this.osVersion, this.deviceType, this.deviceName, this.deviceBrand);
      }
   }
}
