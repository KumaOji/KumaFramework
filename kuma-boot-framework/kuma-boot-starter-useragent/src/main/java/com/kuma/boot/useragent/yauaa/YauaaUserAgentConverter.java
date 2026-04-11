package com.kuma.boot.useragent.yauaa;

import com.kuma.boot.useragent.support.AbstractUserAgentConverter;
import java.util.Map;
import java.util.stream.Collectors;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.http.HttpHeaders;

public class YauaaUserAgentConverter extends AbstractUserAgentConverter<UserAgent> {
   private final UserAgentAnalyzer userAgentAnalyzer;

   public YauaaUserAgentConverter(UserAgentAnalyzer userAgentAnalyzer) {
      this.userAgentAnalyzer = userAgentAnalyzer;
   }

   protected UserAgent create(HttpHeaders headers) {
      Map<String, String> headersConcat = (Map)headers.toSingleValueMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (entry) -> String.join(",", (CharSequence)entry.getValue())));
      return this.userAgentAnalyzer.parse(headersConcat);
   }

   protected String browser(UserAgent userAgent) {
      return userAgent.getValue("AgentName");
   }

   protected String browserType(UserAgent userAgent) {
      return userAgent.getValue("AgentClass");
   }

   protected String browserVersion(UserAgent userAgent) {
      return userAgent.getValue("AgentVersion");
   }

   protected String os(UserAgent userAgent) {
      return userAgent.getValue("OperatingSystemName");
   }

   protected String osVersion(UserAgent userAgent) {
      return userAgent.getValue("OperatingSystemNameVersion");
   }

   protected String deviceType(UserAgent userAgent) {
      return userAgent.getValue("DeviceClass");
   }

   protected String deviceName(UserAgent userAgent) {
      return userAgent.getValue("DeviceName");
   }

   protected String deviceBrand(UserAgent userAgent) {
      return userAgent.getValue("DeviceBrand");
   }
}
