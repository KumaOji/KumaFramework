package com.kuma.boot.websocket.stomp.autoconfigure.properties;

import com.kuma.boot.common.constant.SymbolConstants;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.websocket.stomp"
)
public class WebSocketStompProperties {
   public static final String PREFIX = "kuma.boot.websocket.stomp";
   private boolean enabled = false;
   private String endpoint = "stomp/ws";
   private List<String> applicationDestinationPrefixes = Collections.singletonList("/app");
   private String userDestinationPrefix = "/user";
   private String topic = "ws";
   private String principalHeader = "X-Websocket-Open-Id";

   public WebSocketStompProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   private String format(String endpoint) {
      return StringUtils.isNotBlank(endpoint) && !StringUtils.startWith(endpoint, SymbolConstants.FORWARD_SLASH) ? SymbolConstants.FORWARD_SLASH + endpoint : endpoint;
   }

   public String getEndpoint() {
      return this.format(this.endpoint);
   }

   public void setEndpoint(String endpoint) {
      this.endpoint = endpoint;
   }

   public List<String> getApplicationDestinationPrefixes() {
      return this.applicationDestinationPrefixes;
   }

   public void setApplicationDestinationPrefixes(List<String> applicationDestinationPrefixes) {
      this.applicationDestinationPrefixes = applicationDestinationPrefixes;
   }

   public String[] getApplicationPrefixes() {
      List<String> prefixes = this.getApplicationDestinationPrefixes();
      if (CollectionUtils.isNotEmpty(prefixes)) {
         List<String> wellFormed = prefixes.stream().map(this::format).toList();
         String[] result = new String[wellFormed.size()];
         return (String[])wellFormed.toArray(result);
      } else {
         return new String[0];
      }
   }

   public String getUserDestinationPrefix() {
      return this.format(this.userDestinationPrefix);
   }

   public void setUserDestinationPrefix(String userDestinationPrefix) {
      this.userDestinationPrefix = userDestinationPrefix;
   }

   public String getTopic() {
      return this.topic;
   }

   public void setTopic(String topic) {
      this.topic = topic;
   }

   public String getPrincipalHeader() {
      return this.principalHeader;
   }

   public void setPrincipalHeader(String principalHeader) {
      this.principalHeader = principalHeader;
   }
}
