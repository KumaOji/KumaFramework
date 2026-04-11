package com.kuma.boot.websocket.stomp.autoconfigure;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration(
   proxyBeanMethods = false
)
@ComponentScan(
   basePackages = {"com.kuma.boot.websocket.service", "com.kuma.boot.websocket.controller", "com.kuma.boot.websocket.listener"}
)
public class WebSocketLogicAutoConfiguration {
   private static final Logger log = LoggerFactory.getLogger(WebSocketLogicAutoConfiguration.class);

   public WebSocketLogicAutoConfiguration() {
   }

   @PostConstruct
   public void postConstruct() {
      log.debug("[Websocket] |- SDK [WebSocket Logic] Auto Configure.");
   }
}
