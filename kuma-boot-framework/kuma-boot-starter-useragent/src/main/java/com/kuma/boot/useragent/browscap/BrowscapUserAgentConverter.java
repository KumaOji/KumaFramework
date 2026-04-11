package com.kuma.boot.useragent.browscap;

import com.blueconic.browscap.BrowsCapField;
import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.UserAgentParser;
import com.kuma.boot.useragent.support.AbstractUserAgentConverter;
import org.springframework.http.HttpHeaders;

public class BrowscapUserAgentConverter extends AbstractUserAgentConverter<Capabilities> {
   private final UserAgentParser userAgentParser;

   public BrowscapUserAgentConverter(UserAgentParser userAgentParser) {
      this.userAgentParser = userAgentParser;
   }

   protected Capabilities create(HttpHeaders headers) {
      String userAgent = headers.getFirst("User-Agent");
      return this.userAgentParser.parse(userAgent);
   }

   protected String browser(Capabilities capabilities) {
      return capabilities.getBrowser();
   }

   protected String browserType(Capabilities capabilities) {
      return capabilities.getBrowserType();
   }

   protected String browserVersion(Capabilities capabilities) {
      return capabilities.getBrowserMajorVersion();
   }

   protected String os(Capabilities capabilities) {
      return capabilities.getPlatform();
   }

   protected String osVersion(Capabilities capabilities) {
      return capabilities.getPlatformVersion();
   }

   protected String deviceType(Capabilities capabilities) {
      return capabilities.getDeviceType();
   }

   protected String deviceName(Capabilities capabilities) {
      return capabilities.getValue(BrowsCapField.DEVICE_NAME);
   }

   protected String deviceBrand(Capabilities capabilities) {
      return capabilities.getValue(BrowsCapField.DEVICE_BRAND_NAME);
   }
}
