package com.kuma.boot.websocket.spring.common.holder;

import com.kuma.boot.websocket.spring.common.handler.JsonMessageHandler;
import com.kuma.boot.websocket.spring.common.message.JsonWebSocketMessage;
import jakarta.annotation.PostConstruct;
import java.util.List;

public class JsonMessageHandlerInitializer {
   private final List<JsonMessageHandler<? extends JsonWebSocketMessage>> jsonMessageHandlerList;

   public JsonMessageHandlerInitializer(List<JsonMessageHandler<? extends JsonWebSocketMessage>> jsonMessageHandlerList) {
      this.jsonMessageHandlerList = jsonMessageHandlerList;
   }

   @PostConstruct
   public void initJsonMessageHandlerHolder() {
      for(JsonMessageHandler<? extends JsonWebSocketMessage> jsonMessageHandler : this.jsonMessageHandlerList) {
         JsonMessageHandlerHolder.addHandler(jsonMessageHandler);
      }

   }
}
