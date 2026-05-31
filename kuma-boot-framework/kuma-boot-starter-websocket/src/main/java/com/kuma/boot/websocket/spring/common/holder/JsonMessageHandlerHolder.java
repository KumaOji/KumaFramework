package com.kuma.boot.websocket.spring.common.holder;

import com.kuma.boot.websocket.spring.common.handler.JsonMessageHandler;
import com.kuma.boot.websocket.spring.common.message.JsonWebSocketMessage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class JsonMessageHandlerHolder {
   private static final Map<String, JsonMessageHandler<JsonWebSocketMessage>> MESSAGE_HANDLER_MAP = new ConcurrentHashMap<>();

   private JsonMessageHandlerHolder() {
   }

   public static JsonMessageHandler<JsonWebSocketMessage> getHandler(String type) {
      return MESSAGE_HANDLER_MAP.get(type);
   }

   public static void addHandler(JsonMessageHandler<JsonWebSocketMessage> jsonMessageHandler) {
      MESSAGE_HANDLER_MAP.put(jsonMessageHandler.type(), jsonMessageHandler);
   }
}
