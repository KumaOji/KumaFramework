package com.kuma.boot.logger.logging.listener;

import com.kuma.boot.logger.logging.config.LoggingProperties;
import com.kuma.boot.logger.logging.utils.LoggingUtil;
import org.springframework.boot.web.server.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

public class LoggingStartedEventListener {
   private final LoggingProperties properties;

   public LoggingStartedEventListener(LoggingProperties properties) {
      this.properties = properties;
   }

   @Async
   @Order
   @EventListener({WebServerInitializedEvent.class})
   public void afterStart() {
      LoggingProperties.Console console = this.properties.getConsole();
      if (console.isCloseAfterStart()) {
         LoggingUtil.detachAppender("CONSOLE");
      }

   }
}
